package com.liyinan.myweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Location {
    @SerializedName("basic")
    public List<AreaBasic> AreaBasicList;
    public String status;
}
