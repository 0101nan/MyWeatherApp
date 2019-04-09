package com.liyinan.myweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;
    public Basic basic;
    public Now now;
    public Update update;
    @SerializedName("daily_forecast")
    public List<Daily> dailyForecastList;
    @SerializedName("hourly")
    public List<Hourly> hourlyList;

}
