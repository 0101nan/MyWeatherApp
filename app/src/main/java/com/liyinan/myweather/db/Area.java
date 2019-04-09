package com.liyinan.myweather.db;

import org.litepal.crud.LitePalSupport;

public class Area extends LitePalSupport {
    private String mAreaName;
    private String mAreaCode;

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
