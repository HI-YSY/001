package com.coolweather.android.db;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {

    private int id;

    private String countyName;//县

    private String weatherId;//县的id

    private int cityId;//属于某市的id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

}
