package com.liyinan.myweather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.liyinan.myweather.adapter.WeatherPerHourAdapter;
import com.liyinan.myweather.view.AQIView;
import com.liyinan.myweather.R;
import com.liyinan.myweather.gson.AQI;
import com.liyinan.myweather.gson.Daily;
import com.liyinan.myweather.gson.Hourly;
import com.liyinan.myweather.gson.Weather;
import com.liyinan.myweather.adapter.DiagramAdapter;
import com.liyinan.myweather.util.HttpUtil;
import com.liyinan.myweather.util.LineChartUtil;
import com.liyinan.myweather.util.Utility;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static java.lang.Integer.parseInt;

public class WeatherFragment extends Fragment {
    private static final String ARG_AREA_ID="area_id";
    private static final String TAG = "WeatherFragment";

    private ImageView titleImageView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mWeatherId;

    private CardView mWeatherNowCardView;
    private CardView mWeatherNowAqiCardView;
    private CardView mWeatherPerdayCardView;

    private TextView nowCondText;
    private TextView nowTmp;
    private TextView nowTime;
    private TextView nowCityName;
    private TextView aqiMainText;
    private TextView aqiPM10Text;
    private TextView aqiPM25Text;
    private TextView aqiNO2Text;
    private TextView aqiSO2Text;
    private TextView aqiCOTExt;
    private TextView aqiO3Text;

    private AQIView mAQIView;

    private RecyclerView mDailyForcastRecyclerView;
    private List<Integer> mHeights;
    private List<Integer> mLows;
    private DiagramAdapter mDailyForcastAdapter;

    private RecyclerView mHourilForcastRecyclerView;
    private List<Integer> mHourTmps;
    private WeatherPerHourAdapter mWeatherPerHourAdapter;


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
        nowCityName=view.findViewById(R.id.city_name);

        aqiMainText=view.findViewById(R.id.aqi_main_text);
        aqiPM10Text=view.findViewById(R.id.aqi_pm10_text);
        aqiPM25Text=view.findViewById(R.id.aqi_pm25_text);
        aqiNO2Text=view.findViewById(R.id.aqi_no2_text);
        aqiCOTExt=view.findViewById(R.id.aqi_co_text);
        aqiO3Text=view.findViewById(R.id.aqi_o3_text);
        aqiSO2Text=view.findViewById(R.id.aqi_so2_text);

        mWeatherNowCardView=view.findViewById(R.id.weather_now_cardview);
        mWeatherNowAqiCardView=view.findViewById(R.id.weather_now_aqi_cardview);
        mWeatherPerdayCardView=view.findViewById(R.id.weather_perday_cardview);

        mAQIView=view.findViewById(R.id.aqi_view);

        mDailyForcastRecyclerView=view.findViewById(R.id.weather_perday_recyclerview);
        mHourilForcastRecyclerView=view.findViewById(R.id.weather_perhour_recyclerview);



        mWeatherNowCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mWeatherNowAqiCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mWeatherPerdayCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


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
                mDailyForcastAdapter.notifyDataSetChanged();
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

        //显示逐日天气
        mHeights=new ArrayList<>();
        mLows=new ArrayList<>();
        for (Daily daily:weather.dailyForecastList) {
            mHeights.add(parseInt(daily.tmp_max));
            mLows.add(parseInt(daily.tmp_min));
        }
        int[] mheights=new int[mHeights.size()];
        int[] mlows=new int[mHeights.size()];
        for (int i=0;i<mHeights.size();i++){
            mheights[i]=mHeights.get(i);
            mlows[i]=mLows.get(i);
        }

        mDailyForcastAdapter=new DiagramAdapter(mheights,mlows,1,weather,getFragmentManager());
        mDailyForcastRecyclerView.setAdapter(mDailyForcastAdapter);
        LinearLayoutManager manager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mDailyForcastRecyclerView.setLayoutManager(manager);


        //每小时温度曲线
        mHourTmps=new ArrayList<>();
        for (Hourly hourly:weather.hourlyList){
            mHourTmps.add(parseInt(hourly.tmp));
        }
        mWeatherPerHourAdapter=new WeatherPerHourAdapter(mHourTmps,weather,getFragmentManager());
        mHourilForcastRecyclerView.setAdapter(mWeatherPerHourAdapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        mHourilForcastRecyclerView.setLayoutManager(linearLayoutManager);

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
        mAQIView.setProgress(parseInt(aqi.airNow.aqi));

        aqiMainText.setText(aqi.airNow.main);
        aqiPM10Text.setText(aqi.airNow.pm10);
        aqiPM25Text.setText(aqi.airNow.pm25);
        aqiNO2Text.setText(aqi.airNow.no2);
        aqiCOTExt.setText(aqi.airNow.co);
        aqiO3Text.setText(aqi.airNow.o3);
        aqiSO2Text.setText(aqi.airNow.so2);

    }

}
