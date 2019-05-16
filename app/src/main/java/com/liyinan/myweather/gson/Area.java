package com.liyinan.myweather.gson;

import java.io.Serializable;

public class Area implements Serializable {
    private String mAreaName;
    private String mAreaCode;
    private String titleImg;
    private String mLonLat;

    public String getLonLat() {
        return mLonLat;
    }

    public void setLonLat(String lonLat) {
        mLonLat = lonLat;
    }

    public String getTitleImg() {
        return titleImg;
    }

    public void setTitleImg(String titleImg) {
        this.titleImg = titleImg;
    }

    public String getAreaName() {
        return mAreaName;
    }

    public void setAreaName(String areaName) {
        mAreaName = areaName;
    }

    public String getAreaCode() {
        return mAreaCode;
    }

    public void setAreaCode(String areaCode) {
        mAreaCode = areaCode;
    }
}
