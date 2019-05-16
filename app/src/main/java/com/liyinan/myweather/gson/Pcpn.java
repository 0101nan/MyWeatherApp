package com.liyinan.myweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pcpn {
    public Basic basic;
    @SerializedName("grid_minute_forecast")
    public GridMinuteForecast GridMinuteForecast;
    @SerializedName("pcpn_5m")
    public List<Pcpn5m> Pcpn5mList;
    @SerializedName("pcpn_type")
    public PcpnType PcpnType;
    public String status;
    public Update Update;
}
