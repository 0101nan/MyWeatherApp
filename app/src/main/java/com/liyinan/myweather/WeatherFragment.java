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
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.lang.Integer.parseInt;

public class WeatherFragment extends Fragment {
    private static final String ARG_AREA_ID="area_id";
    private static final String TAG = "WeatherFragment";

    private LinearLayout forecastLayout;
    private ImageView titleImageView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mWeatherId;
    private LineChart lineChart;

    private CardView mWeatherNowCard;

    private TextView nowCondText;
    private TextView nowTmp;
    private TextView nowTime;
    private TextView nowQlty;
    private TextView nowPm25;
    private TextView nowCityName;
    private TextView max_tmp_text;
    private TextView min_tmp_text;
    private TextView pop_text;
    private TextView pcpn_text;
    private TextView wind_dir_text;
    private TextView wind_sc_text;
    private TextView uv_index_text;
    private TextView vis_text;


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
        titleImageView=view.findViewById(R.id.weather_title_img);
        mSwipeRefreshLayout=view.findViewById(R.id.weather_swipe_refresh);
        nowCondText=view.findViewById(R.id.now_cond_txt);
        nowTime=view.findViewById(R.id.now_date);
        nowTmp=view.findViewById(R.id.now_tmp);
        lineChart=view.findViewById(R.id.line_chart);
        forecastLayout=view.findViewById(R.id.weather_forecast_layout);
        nowPm25=view.findViewById(R.id.now_pm25);
        nowQlty=view.findViewById(R.id.now_qlty_txt);
        nowCityName=view.findViewById(R.id.city_name);

        max_tmp_text=view.findViewById(R.id.max_tmp_text);
        min_tmp_text=view.findViewById(R.id.min_tmp_text);
        pop_text=view.findViewById(R.id.pop_text);
        pcpn_text=view.findViewById(R.id.pcpn_text);
        wind_dir_text=view.findViewById(R.id.wind_dir_text);
        wind_sc_text=view.findViewById(R.id.wind_sc_text);
        uv_index_text=view.findViewById(R.id.uv_index_text);
        vis_text=view.findViewById(R.id.vis_text);

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

        //设置头图并保存图片地址
        String titleImg=Utility.getTitleImg(weather);
        int resId = getContext().getResources().getIdentifier(titleImg, "drawable", getContext().getPackageName());
        Glide.with(this).load(resId).into(titleImageView);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putInt("area_titleImg"+weather.basic.cid,resId);
        editor.apply();

        nowCityName.setText(weather.basic.location);
        nowTime.setText(weather.update.loc.split(" ")[1]);
        //显示当日天气
        nowTmp.setText(temperature+"℃");
        nowCondText.setText(cond);
       // nowTime.setText(updateTime);
        max_tmp_text.setText(weather.dailyForecastList.get(0).tmp_max+"℃");
        min_tmp_text.setText(weather.dailyForecastList.get(0).tmp_min+"℃");
        pop_text.setText(weather.dailyForecastList.get(0).pop+"%");
        pcpn_text.setText(weather.dailyForecastList.get(0).pcpn+"mm");
        wind_dir_text.setText(weather.dailyForecastList.get(0).wind_dir);
        wind_sc_text.setText(weather.dailyForecastList.get(0).wind_sc+"级");
        uv_index_text.setText(weather.dailyForecastList.get(0).uv_index+"级");
        vis_text.setText(weather.dailyForecastList.get(0).vis+"km");

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

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(weather.update.loc);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            Log.d(TAG, "showWeatherInfo: "+calendar.get(Calendar.HOUR_OF_DAY));
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                        }else{
                            Toast.makeText(getContext(), "获取空气信息失败 ", Toast.LENGTH_SHORT).show();
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
