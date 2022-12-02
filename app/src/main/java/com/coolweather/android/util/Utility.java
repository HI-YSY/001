package com.coolweather.android.util;

import android.text.TextUtils;

import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utility {
    /**
     * 解析服务器返回的省份数据  500页
     * 服务器返回的数据格式 487页**********
     * [{"id":1,"name":"北京"},{"id":2,"name":"上海"},{"id":3,"name":"天津"},{"id":4,"name":"重庆"},
     * {"id":5,"name":"香港"},{"id":6,"name":"澳门"},{"id":7,"name":"台湾"},{"id":8,"name":"黑龙江"},
     * {"id":9,"name":"吉林"},{"id":10,"name":"辽宁"},{"id":11,"name":"内蒙古"},{"id":12,"name":"河北"},
     * {"id":13,"name":"河南"},{"id":14,"name":"山西"},{"id":15,"name":"山东"},{"id":16,"name":"江苏"},
     * {"id":17,"name":"浙江"},{"id":18,"name":"福建"},{"id":19,"name":"江西"},{"id":20,"name":"安徽"},
     * {"id":21,"name":"湖北"},{"id":22,"name":"湖南"},{"id":23,"name":"广东"},{"id":24,"name":"广西"},
     * {"id":25,"name":"海南"},{"id":26,"name":"贵州"},{"id":27,"name":"云南"},{"id":28,"name":"四川"},
     * {"id":29,"name":"西藏"},{"id":30,"name":"陕西"},{"id":31,"name":"宁夏"},{"id":32,"name":"甘肃"},
     * {"id":33,"name":"青海"},{"id":34,"name":"新疆"}]
     */
    public static boolean handleProvinceResponse(String response) {//访问地址：http://guolin.tech/api/china
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allProvinces = new JSONArray(response);//解析服务器返回的JSON格式数据[{"id":1,"name":"北京"}，{"id":2,"name":"上海"}，...]
                for (int i = 0; i < allProvinces.length(); i++) {
                    JSONObject provinceObject = allProvinces.getJSONObject(i);//把里面的数据拿一个出来{"id":1,"name":"北京"}
                    Province province = new Province();//数据库
                    province.setProvinceName(provinceObject.getString("name"));// 取出（"name":"北京"）
                    province.setProvinceCode(provinceObject.getInt("id"));// 取出（"id":1）
                    province.save();//储存到数据库中
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的市级数据*****************************************************************************
     *[{"id":113,"name":"南京"},{"id":114,"name":"无锡"},{"id":115,"name":"镇江"},{"id":116,"name":"苏州"},
     * {"id":117,"name":"南通"},{"id":118,"name":"扬州"},{"id":119,"name":"盐城"},{"id":120,"name":"徐州"},
     * {"id":121,"name":"淮安"},{"id":122,"name":"连云港"},{"id":123,"name":"常州"},{"id":124,"name":"泰州"},
     * {"id":125,"name":"宿迁"}]
     */
    public static boolean handleCityResponse(String response, int provinceId) {//访问地址：http://guolin.tech/api/china/ + 省份id
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCities = new JSONArray(response);
                for (int i = 0; i < allCities.length(); i++) {
                    JSONObject cityObject = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObject.getString("name"));//市名
                    city.setCityCode(cityObject.getInt("id"));//市名id
                    city.setProvinceId(provinceId);//省份id
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的县级数据***********************************************************************
     [{"id":41,"name":"重庆","weather_id":"CN101040100"},{"id":42,"name":"永川","weather_id":"CN101040200"},
     {"id":43,"name":"合川","weather_id":"CN101040300"},{"id":44,"name":"南川","weather_id":"CN101040400"},
     {"id":45,"name":"江津","weather_id":"CN101040500"},{"id":46,"name":"万盛","weather_id":"CN101040600"},
     {"id":47,"name":"渝北","weather_id":"CN101040700"},{"id":48,"name":"北碚","weather_id":"CN101040800"},
     {"id":49,"name":"巴南","weather_id":"CN101040900"},{"id":50,"name":"长寿","weather_id":"CN101041000"},
     {"id":51,"name":"黔江","weather_id":"CN101041100"},{"id":52,"name":"万州","weather_id":"CN101041300"},
     {"id":53,"name":"涪陵","weather_id":"CN101041400"},{"id":54,"name":"开县","weather_id":"CN101041500"},
     {"id":55,"name":"城口","weather_id":"CN101041600"},{"id":56,"name":"云阳","weather_id":"CN101041700"},
     {"id":57,"name":"巫溪","weather_id":"CN101041800"},{"id":58,"name":"奉节","weather_id":"CN101041900"},
     {"id":59,"name":"巫山","weather_id":"CN101042000"},{"id":60,"name":"潼南","weather_id":"CN101042100"},
     {"id":61,"name":"垫江","weather_id":"CN101042200"},{"id":62,"name":"梁平","weather_id":"CN101042300"},
     {"id":63,"name":"忠县","weather_id":"CN101042400"},{"id":64,"name":"石柱","weather_id":"CN101042500"},
     {"id":65,"name":"大足","weather_id":"CN101042600"},{"id":66,"name":"荣昌","weather_id":"CN101042700"},
     {"id":67,"name":"铜梁","weather_id":"CN101042800"},{"id":68,"name":"璧山","weather_id":"CN101042900"},
     {"id":69,"name":"丰都","weather_id":"CN101043000"},{"id":70,"name":"武隆","weather_id":"CN101043100"},
     {"id":71,"name":"彭水","weather_id":"CN101043200"},{"id":72,"name":"綦江","weather_id":"CN101043300"},
     {"id":73,"name":"酉阳","weather_id":"CN101043400"},{"id":74,"name":"秀山","weather_id":"CN101043600"}]
     */
    public static boolean handleCountyResponse(String response, int cityId) {////访问地址：http://guolin.tech/api/china/ + 省份id / + 市级id
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray allCounties = new JSONArray(response);
                for (int i = 0; i < allCounties.length(); i++) {
                    JSONObject countyObject = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObject.getString("name"));//县名
                    county.setWeatherId(countyObject.getString("weather_id"));//"weather_id":"CN101043600" 用于向和风天气申请天气数据
                    county.setCityId(cityId);//市级id
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
