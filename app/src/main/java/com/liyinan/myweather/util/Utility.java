package com.liyinan.myweather.util;

import com.google.gson.Gson;
import com.liyinan.myweather.gson.AQI;
import com.liyinan.myweather.gson.Area;
import com.liyinan.myweather.gson.Location;
import com.liyinan.myweather.gson.Weather;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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

    public static List<Area> handleAreaList(String jsonAreaList){
        List<Area> mAreaList=new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonAreaList);
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObject=jsonArray.getJSONObject(i);
                Area area = new Area();
                area.setAreaName(jsonObject.getString("mAreaName"));
                area.setAreaCode(jsonObject.getString("mAreaCode"));
                mAreaList.add(area);
            }
            return mAreaList;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getTitleImg(String lastImg,Weather weather){
        List<String> titleImgListUsual=new ArrayList<>(Arrays.asList("title_img_1_0", "title_img_1_1", "title_img_1_2",
                "title_img_1_3","title_img_1_4","title_img_1_5"));
        List<String> titleImgListSummer=new ArrayList<>(Arrays.asList("title_img_2_0", "title_img_2_1", "title_img_2_2"));
        List<String> titleImgListWinter=new ArrayList<>(Arrays.asList("title_img_3_0", "title_img_3_1"));
        List<String> titleImgListAutumn=new ArrayList<>(Arrays.asList("title_img_4_0"));
        List<String> titleImgListNight=new ArrayList<>(Arrays.asList("title_img_6_0", "title_img_6_1"));
        List<String> titleImgListRain=new ArrayList<>(Arrays.asList("title_img_5_0"));
        List<String> baseList;

        if(weather.status.equals("ok")) {
            if (Integer.parseInt(weather.now.tmp) > 28) {
                baseList = titleImgListSummer;
            } else if (Integer.parseInt(weather.now.tmp) < 0) {
                baseList = titleImgListWinter;
            } else {
                baseList = titleImgListUsual;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = sdf.parse(weather.update.loc);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                if (calendar.get(Calendar.MONTH) >= 8 && calendar.get(Calendar.MONTH) <= 10) {
                    baseList.addAll(titleImgListAutumn);
                }
                if (calendar.get(Calendar.HOUR_OF_DAY) >= 20 || calendar.get(Calendar.HOUR_OF_DAY) <= 6) {
                    baseList = titleImgListNight;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Integer.parseInt(weather.now.cond_code) >= 300 && Integer.parseInt(weather.now.cond_code) <= 399) {
                baseList = titleImgListRain;
            }
        }else{
            baseList=titleImgListUsual;
        }
        Random random=new Random();
        int position=random.nextInt(baseList.size());
        if (lastImg==null){
            return baseList.get(position);
        }
        if(baseList.get(position).split("_")[2].equals(lastImg.split("_")[2])){
            return lastImg;
        }else{
            return baseList.get(position);
        }

    }

    public static String weatherImgTitle(String cond_id,boolean isDay) {
        String result;
        if (cond_id.equals("100")) {
            result = "sun";
        } else if (Integer.parseInt(cond_id) >= 102 && Integer.parseInt(cond_id) <= 104) {
            result = "few_cloud";
        } else if (cond_id.equals("101")) {
            result = "cloud";
        } else if (cond_id.equals("300") || cond_id.equals("305") || cond_id.equals("309")) {
            result = "drizzle";
        } else if (cond_id.equals("301") || (Integer.parseInt(cond_id) >= 306 && Integer.parseInt(cond_id) <= 308) || (Integer.parseInt(cond_id) >= 310 && Integer.parseInt(cond_id) <= 399)) {
            result = "heavy_rain";
        } else if (cond_id.equals("404")) {
            result = "snow_rain";
        } else if (cond_id.equals("304")) {
            result = "hail";
        } else if ((Integer.parseInt(cond_id) >= 401 && Integer.parseInt(cond_id) <= 403) || (Integer.parseInt(cond_id) >= 405 && Integer.parseInt(cond_id) <= 499)) {
            result = "heavy_snow";
        } else if ((Integer.parseInt(cond_id) >= 302 && Integer.parseInt(cond_id) <= 303)) {
            result = "storm";
        } else if ((Integer.parseInt(cond_id) >= 200 && Integer.parseInt(cond_id) <= 213)) {
            result = "wind";
        } else if ((Integer.parseInt(cond_id) >= 500 && Integer.parseInt(cond_id) <= 515)) {
            result = "haze";
        } else if (cond_id.equals("400")) {
            result = "snow";
        } else {
            return "none";
        }
        if (isDay) {
            result = result + "_day";
        } else {
            result = result + "_night";
        }
        return result;
    }
}
