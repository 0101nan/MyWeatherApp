package com.liyinan.myweather.util;

import com.google.gson.Gson;
import com.liyinan.myweather.db.Area;
import com.liyinan.myweather.gson.AQI;
import com.liyinan.myweather.gson.Location;
import com.liyinan.myweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {
    public static Location handleAreaResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather6");
            String AreaContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(AreaContent, Location.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static Weather handleWeatherResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather6");
            String weatherContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static AQI handleAQIResponse(String response){
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray jsonArray=jsonObject.getJSONArray("HeWeather6");
            String AQIContent=jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(AQIContent,AQI.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
