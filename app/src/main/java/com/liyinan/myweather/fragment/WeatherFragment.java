package com.liyinan.myweather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.liyinan.myweather.activity.WeatherPagerActivity;
import com.liyinan.myweather.adapter.WeatherPcpnAdapter;
import com.liyinan.myweather.adapter.WeatherPerHourAdapter;
import com.liyinan.myweather.gson.Area;
import com.liyinan.myweather.gson.Pcpn;
import com.liyinan.myweather.gson.Pcpn5m;
import com.liyinan.myweather.view.AQIView;
import com.liyinan.myweather.R;
import com.liyinan.myweather.gson.AQI;
import com.liyinan.myweather.gson.Daily;
import com.liyinan.myweather.gson.Hourly;
import com.liyinan.myweather.gson.Weather;
import com.liyinan.myweather.adapter.WeatherPerDayAdapter;
import com.liyinan.myweather.util.HttpUtil;
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
    private static final String TAG = "WeatherFragment";
    private static final String LAST_WEATHER_UPDATE_TIME="last_weather_update_time";
    private static final String LAST_AQI_UPDATE_TIME="last_aqi_update_time";

    private ImageView titleImageView;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private String mWeatherId;
    private Area mArea;
    private String mLotLat;
    private CardView mWeatherNowCardView;
    private CardView mWeatherNowAqiCardView;
    private CardView mWeatherPerdayCardView;
    private CardView mWeatherPcpnCardView;

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
    private TextView pcpnText;

    private AQIView mAQIView;

    private RecyclerView mDailyForcastRecyclerView;
    private List<Integer> mHeights;
    private List<Integer> mLows;
    private WeatherPerDayAdapter mDailyForcastAdapter;

    private RecyclerView mHourilForcastRecyclerView;
    private List<Integer> mHourTmps;
    private WeatherPerHourAdapter mWeatherPerHourAdapter;

    private String lastWeatherUpdateTime;
    private String lastAqiUpdateTime;
    private boolean isRefreshed=false;

    private AQI mAQI;
    private Pcpn mPcpn;
    private boolean autoUpdate;

    private List<Float> mPcpns;
    private WeatherPcpnAdapter mWeatherPcpnAdapter;
    private RecyclerView mPcpnRecyclerView;

    //由启动处创建附带地址的fragment
    public static WeatherFragment newInstance(Area area){
        Bundle args=new Bundle();
        args.putSerializable(ARG_AREA_ID,area);
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
        pcpnText=view.findViewById(R.id.weather_pcpn_text);

        mWeatherNowCardView=view.findViewById(R.id.weather_now_cardview);
        mWeatherNowAqiCardView=view.findViewById(R.id.weather_now_aqi_cardview);
        mWeatherPerdayCardView=view.findViewById(R.id.weather_perday_cardview);
        mWeatherPcpnCardView=view.findViewById(R.id.weather_pcpn_cardview);

        mAQIView=view.findViewById(R.id.aqi_view);

        mDailyForcastRecyclerView=view.findViewById(R.id.weather_perday_recyclerview);
        mHourilForcastRecyclerView=view.findViewById(R.id.weather_perhour_recyclerview);
        mPcpnRecyclerView=view.findViewById(R.id.weather_pcpn_recyclerview);

        mWeatherNowAqiCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAQI.airStationList!=null){
                    FragmentManager manager=getFragmentManager();
                    AQIStationDialogFragment aqiStationDialogFragment=AQIStationDialogFragment.newInstance(mAQI);
                    aqiStationDialogFragment.show(manager,null);
                }
            }
        });


        //查询天气
        mArea=(Area) getArguments().getSerializable(ARG_AREA_ID);
        mWeatherId=mArea.getAreaCode();
        mLotLat=mArea.getLonLat();
        Log.d(TAG, "onCreateView: "+mLotLat);
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        String weatherString=preferences.getString("area_weather"+mWeatherId,null);
        String aqiString=preferences.getString("area_aqi"+mWeatherId,null);
        String pcpnString=preferences.getString("area_pcpn"+mWeatherId,null);
        autoUpdate=preferences.getBoolean("auto_update",false);
        lastWeatherUpdateTime=preferences.getString(LAST_WEATHER_UPDATE_TIME+mWeatherId,null);
        lastAqiUpdateTime=preferences.getString(LAST_AQI_UPDATE_TIME+mWeatherId,null);
        if(lastWeatherUpdateTime==null){
            lastWeatherUpdateTime=new String();
        }
        if(lastAqiUpdateTime==null){
            lastAqiUpdateTime=new String();
        }
        if(weatherString!=null){
            Weather weather= Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        }else{
            requestWeather(mWeatherId);
        }
        if(aqiString!=null){
            AQI aqi= Utility.handleAQIResponse(aqiString);
            mAQI=aqi;
            showAQIInfo(aqi);
        }else{
            requestAQI(mWeatherId);
        }
        if(pcpnString!=null){
            Pcpn pcpn= Utility.handlePcpnResponse(pcpnString);
            mPcpn=pcpn;
            showPcpnInfo(pcpn);
        }else{
            requestPcpn(mLotLat);
        }



        //刷新天气
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
                requestAQI(mWeatherId);
                requestPcpn(mLotLat);
                if(isRefreshed){
                Snackbar.make(view,"天气信息已更新",Snackbar.LENGTH_SHORT)
                        .show();
                isRefreshed=false;
                }else{
                Snackbar.make(view,"已是最新的天气信息",Snackbar.LENGTH_SHORT)
                        .show();
                }
            }
        });
        return view;
    }

    //获取天气信息
    public void requestWeather(String weatherId) {
        //启动服务
        if(autoUpdate!=false) {
            WeatherPagerActivity.startService(getContext());
        }

        //设置api地址
        String weatherUrl="https://api.heweather.net/s6/weather?location="+weatherId+"&key=4477c8824b5f44da84a872578614bdc2";
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
                            if(!lastWeatherUpdateTime.equals(weather.update.loc)){
                                lastWeatherUpdateTime=weather.update.loc;
                                isRefreshed=true;
                                SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                editor.putString("area_weather"+mWeatherId,responseText);
                                editor.putString(LAST_WEATHER_UPDATE_TIME+mWeatherId,lastWeatherUpdateTime);
                                editor.apply();
                                mWeatherId=weather.basic.cid;
                                showWeatherInfo(weather);
                            }
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
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(getContext());
        String weatherImg=preferences.getString("area_titleImg"+weather.basic.cid,null);
        String titleImg=Utility.getTitleImg(weatherImg,weather);
        int resId = getContext().getResources().getIdentifier(titleImg, "drawable", getContext().getPackageName());
        Glide.with(this).load(resId).into(titleImageView);
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putString("area_titleImg"+weather.basic.cid,titleImg);
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

        //LayoutAnimationController controller= AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_animation_slide_right);
        mDailyForcastAdapter=new WeatherPerDayAdapter(mheights,mlows,1,weather,getFragmentManager());
        //mDailyForcastRecyclerView.setLayoutAnimation(controller);
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
        String aqiUrl="https://api.heweather.net/s6/air?location="+weatherId+"&key=4477c8824b5f44da84a872578614bdc2";
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
                            if(!lastAqiUpdateTime.equals(aqi.update.loc)){
                                isRefreshed=true;
                                lastAqiUpdateTime=aqi.update.loc;
                                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                editor.putString("area_aqi"+mWeatherId,responseText);
                                editor.putString(LAST_AQI_UPDATE_TIME+mWeatherId,lastAqiUpdateTime);
                                editor.apply();
                                mAQI=aqi;
                                showAQIInfo(aqi);
                            }
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

    //获取降雨量
    public void requestPcpn(final String lonlat){
        String pcpnUrl="https://api.heweather.net/s6/weather/grid-minute?location="+lonlat+"&key=4477c8824b5f44da84a872578614bdc2";
        HttpUtil.sendOkHttpRequest(pcpnUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取降水量质量失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Pcpn pcpn=Utility.handlePcpnResponse(responseText);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(pcpn!=null && pcpn.status.equals("ok")){
                                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                editor.putString("area_pcpn"+mWeatherId,responseText);
                                editor.apply();
                                mPcpn=pcpn;
                                showPcpnInfo(pcpn);
                        }else{
                            Toast.makeText(getContext(), "获取降水量信息失败 ", Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
    //显示降水量
    private void showPcpnInfo(Pcpn pcpn) {

        float mpcpnMax=0;
        mPcpns=new ArrayList<>();
        for (Pcpn5m pcpn5m:pcpn.Pcpn5mList){
            mPcpns.add(Float.parseFloat(pcpn5m.pcpn));
            if (Float.parseFloat(pcpn5m.pcpn)>mpcpnMax){
                mpcpnMax=Float.parseFloat(pcpn5m.pcpn);
            }
        }
        if (mpcpnMax>0){
            mWeatherPcpnCardView.setVisibility(View.VISIBLE);
            pcpnText.setText(pcpn.GridMinuteForecast.txt);
            mWeatherPcpnAdapter=new WeatherPcpnAdapter(mPcpns,pcpn);
            mPcpnRecyclerView.setAdapter(mWeatherPcpnAdapter);
            LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
            mPcpnRecyclerView.setLayoutManager(linearLayoutManager1);
        }else{
            mWeatherPcpnCardView.setVisibility(View.GONE);
        }

    }
}
