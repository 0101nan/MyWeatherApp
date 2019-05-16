package com.liyinan.myweather.gson;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class AQI implements Serializable {
    public Basic basic;
    public Update update;
    public String status;
    @SerializedName("air_now_city")
    public AirNow airNow;
    @SerializedName("air_now_station")
    public List<AirStation> airStationList;
}
