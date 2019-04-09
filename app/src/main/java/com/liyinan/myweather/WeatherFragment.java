package com.liyinan.myweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.liyinan.myweather.gson.AQI;
import com.liyinan.myweather.gson.Daily;
import com.liyinan.myweather.gson.Weather;
import com.liyinan.myweather.util.HttpUtil;
import com.liyinan.myweather.util.LineChartUtil;
import com.liyinan.myweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.lang.Integer.parseInt;

public class WeatherFragment extends Fragment {
    private static final String ARG_AREA_ID="area_id";

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout forecastLayout;
    private Toolbar toolbar;
    private ImageView titleImageView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mWeatherId;
    private LineChart lineChart;

    private TextView nowCondText;
    private TextView nowTmp;
    private TextView nowTime;
    private TextView nowQlty;
    private TextView nowPm25;

    //由启动处创建附带地址的fragment
    public static WeatherFragment newInstance(String areaId){
        Bundle args=new Bundle();
        args.putString(ARG_AREA_ID,areaId);

        WeatherFragment weatherFragment=new WeatherFragment();
        weatherFragment.setArguments(args);
        return weatherFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_weather,container,false);
        //控件绑定
        collapsingToolbarLayout=view.findViewById(R.id.weather_collapsing_toolbar);
        toolbar=view.findViewById(R.id.weather_toolbar);
        titleImageView=view.findViewById(R.id.weather_title_img);
        mSwipeRefreshLayout=view.findViewById(R.id.weather_swipe_refresh);
        nowCondText=view.findViewById(R.id.now_cond_txt);
        nowTime=view.findViewById(R.id.now_date);
        nowTmp=view.findViewById(R.id.now_tmp);
        lineChart=view.findViewById(R.id.line_chart);
        forecastLayout=view.findViewById(R.id.weather_forecast_layout);
        nowPm25=view.findViewById(R.id.now_pm25);
        nowQlty=view.findViewById(R.id.now_qlty_txt);

        //设置标题栏
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar=((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle("无数据");

        //设置头图
        Glide.with(this).load(R.drawable.titleimg).into(titleImageView);

        //查询天气
        mWeatherId=getArguments().getString(ARG_AREA_ID);
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        String weatherString=preferences.getString("area_weather"+mWeatherId,null);
        String aqiString=preferences.getString("area_aqi"+mWeatherId,null);
        if(weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            requestWeather(mWeatherId);
        }
        if(aqiString!=null){
            AQI aqi= Utility.handleAQIResponse(aqiString);
            showAQIInfo(aqi);
        }else{
            requestAQI(mWeatherId);
        }


        //刷新天气
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                requestAQI(mWeatherId);
            }
        });
        return view;
    }

    //获取天气信息
    public void requestWeather(String weatherId) {
        //设置api地址
        String weatherUrl="https://api.heweather.net/s6/weather?location="+weatherId+"&key=ab4bb0964d4d4b3894f8cdaf1b79302c";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Weather weather= Utility.handleWeatherResponse(responseText);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather!=null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString("area_weather"+mWeatherId,responseText);
                            editor.apply();
                            mWeatherId=weather.basic.cid;
                            showWeatherInfo(weather);
                        }else{
                            Toast.makeText(getContext(), "获取天气信息失败 ", Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    //显示天气信息
    private void showWeatherInfo(Weather weather) {
        String cityName=weather.basic.location;
        String updateTime=weather.update.loc.split(" ")[1];
        String temperature=weather.now.tmp;
        String cond=weather.now.cond_txt;

        //显示标题
        collapsingToolbarLayout.setTitle(cityName);

        //显示当日天气
        nowTmp.setText(temperature+"℃");
        nowCondText.setText(cond);
        nowTime.setText(updateTime);

        //显示逐日天气
        forecastLayout.removeAllViews();
        for (Daily daily :weather.dailyForecastList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.weather_perday_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.weather_perday_date_text);
            TextView infoText = view.findViewById(R.id.weather_perday_cond_text);
            dateText.setText(daily.date.split("-")[2]);
            infoText.setText(daily.cond_txt_d);
            forecastLayout.addView(view);
        }

        //显示每日天气曲线
        LineChartUtil.initChart(lineChart);
        List<Integer> maxTmpList=new ArrayList<>();
        List<Integer> minTmpList=new ArrayList<>();
        for(Daily tmp:weather.dailyForecastList){
            maxTmpList.add(parseInt(tmp.tmp_max));
            minTmpList.add(parseInt(tmp.tmp_min));
        }
        LineChartUtil.showLineChart(maxTmpList,"tmpMax","#1698a6",lineChart);
        LineChartUtil.addLine(minTmpList,"tmpMin","#104744",lineChart);
        lineChart.notifyDataSetChanged();
        //lineChart.invalidate();
    }
    //获取空气质量
    public void requestAQI(final String weatherId){
        String aqiUrl="https://api.heweather.net/s6/air?location="+weatherId+"&key=ab4bb0964d4d4b3894f8cdaf1b79302c";
        HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取空气质量失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final AQI aqi=Utility.handleAQIResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(aqi!=null && aqi.status.equals("ok")){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString("area_aqi"+mWeatherId,responseText);
                            editor.apply();
                            showAQIInfo(aqi);
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    //显示空气质量
    private void showAQIInfo(AQI aqi) {
        String qlty=aqi.airNow.qlty;
        String pm25=aqi.airNow.pm25;
        nowQlty.setText(qlty);
        nowPm25.setText(pm25);
    }


}
