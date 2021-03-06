package com.liyinan.myweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;

import com.liyinan.myweather.gson.AQI;
import com.liyinan.myweather.gson.Area;
import com.liyinan.myweather.gson.Weather;
import com.liyinan.myweather.util.HttpUtil;
import com.liyinan.myweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class AutoUpdateService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        updateWeather();
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour=60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+anHour;
        Intent i=new Intent(this,AutoUpdateService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
        return super.onStartCommand(intent,flags,startId);
    }

    private void updateWeather(){
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String jsonAreaList=prefs.getString("areaList",null);
        if(jsonAreaList!=null){
            List<Area> mAreaList = Utility.handleAreaList(jsonAreaList);
            for (Area area : mAreaList){
                String weatherUrl="https://api.heweather.net/s6/weather?location="+ area.getAreaCode()+"&key=4477c8824b5f44da84a872578614bdc2";
                HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText=response.body().string();
                        Weather weather=Utility.handleWeatherResponse(responseText);
                        if(weather!=null&&"ok".equals(weather.status)){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("area_weather"+ area.getAreaCode(),responseText);
                            editor.apply();
                        }
                    }
                });
                String aqiUrl="https://api.heweather.net/s6/air?location="+ area.getAreaCode()+"&key=4477c8824b5f44da84a872578614bdc2";
                HttpUtil.sendOkHttpRequest(aqiUrl, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseText=response.body().string();
                        AQI aqi=Utility.handleAQIResponse(responseText);
                        if(aqi!=null&&aqi.status.equals("ok")){
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                            editor.putString("area_aqi"+ area.getAreaCode(),responseText);
                            editor.apply();
                        }
                    }
                });
            }
        }

    }
}
