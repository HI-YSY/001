package com.coolweather.android;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.coolweather.android.db.City;
import com.coolweather.android.db.County;
import com.coolweather.android.db.Province;
import com.coolweather.android.gson.BingPic;

import com.coolweather.android.gson.Forecast;
import com.coolweather.android.gson.Weather;
import com.coolweather.android.util.HttpUtil;
import com.coolweather.android.util.Utility;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    public SwipeRefreshLayout swipeRefresh;
    public DrawerLayout drawerLayout;
    public Button navButton;
    public String weatherIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.transparency));//?????????????????????
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);//?????????????????????????????????????????????
        }
        setContentView(R.layout.activity_weather);
        //???????????????
        weatherLayout = findViewById(R.id.weather_layout);
        titleCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        bingPicImg = findViewById(R.id.bing_pic_img);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.black);//?????????????????????
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String bingPic = prefs.getString("bing_pic", null);
        String weatherString = prefs.getString("weather", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingPicImg);
        } else {
            loadBingPic();
        }

        if (weatherString != null) {
            //?????????????????????????????????
            Weather weather = Utility.handleWeatherResponse(weatherString);
            weatherIds = Objects.requireNonNull(weather).basic.weatherId;
            showWeatherInfo(weather);
            requestWeather(weatherIds);//??????????????????????????????
        } else {
            //????????????????????????????????????
            weatherIds = getIntent().getStringExtra("weather_id");
            weatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherIds);
        }

//        if (ping()) {//??????????????????
//            if (weatherString == null) {//?????????
//                //????????????????????????????????????
//                weatherIds = getIntent().getStringExtra("weather_id");
//                weatherLayout.setVisibility(View.INVISIBLE);
//                requestWeather(weatherIds);
//            }
//            requestWeather(weatherIds);
//        } else {
//            Toast.makeText(this, "???????????????", Toast.LENGTH_SHORT).show();
//        }

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {//?????? 533
            @Override
            public void onRefresh() {
                requestWeather(weatherIds);
            }
        });

    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
     */
//        process.waitFor()??????0?????????????????????
//        process.waitFor()??????1????????????????????????wifi
//        process.waitFor()??????2????????????????????????
    public static boolean ping() {
        String result = null;
        try {
            String ip = "www.baidu.com";// ping ???????????????????????????????????????????????????
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping??????3???
            // ??????ping????????????????????????
//            InputStream input = p.getInputStream();
//            BufferedReader in = new BufferedReader(new InputStreamReader(input));
//            StringBuilder stringBuffer = new StringBuilder();
//            String content = "";
//            while ((content = in.readLine()) != null) {
//                stringBuffer.append(content);
//            }

//            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping?????????
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        }
//        finally {
////            Log.d("----result---", "result = " + result);
//        }
        return false;
    }

    /**
     * ????????????id????????????????????????
     */
    public void requestWeather(String weatherId) {
        weatherIds = weatherId;//???????????????????????????????????????????????????(????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????)????????????????????????????????????????????????????????????
//       String weatherUrl=" https://devapi.qweather.com/v7/weather/now?location="+weatherId+"&key=2c5cc1e0b664441b9d1a726f88635942";
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=5cf1e3cad3a34b29a164b888038b4b6a";
        HttpUtil.sendOKHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText = Objects.requireNonNull(response.body()).string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
//                Toast.makeText(WeatherActivity.this, "123", Toast.LENGTH_SHORT).show();
                runOnUiThread(() -> {
                    if (weather != null && "ok".equals(weather.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                        editor.putString("weather", responseText);
                        editor.apply();
                        showWeatherInfo(weather);
                    } else {
                        Toast.makeText(WeatherActivity.this, "????????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                    swipeRefresh.setRefreshing(false);
                });
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(WeatherActivity.this, "????????????????????????", Toast.LENGTH_SHORT).show());
                swipeRefresh.setRefreshing(false);
            }
        });
        loadBingPic();
    }

    /**
     * ????????????????????????
     */
    private void loadBingPic() {
        String requestBingPic = "https://cn.bing.com/HPImageArchive.aspx?format=js&idx=0&n=1&mkt=zh-CN";
        HttpUtil.sendOKHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                final String responseText = Objects.requireNonNull(response.body()).string();
                final BingPic bingPic = Utility.handleBingPicResponse(responseText);
                assert bingPic != null;
                String url = "https://cn.bing.com" + (bingPic.url).split("&")[0];
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", url).apply();
                runOnUiThread(() -> Glide.with(WeatherActivity.this).load(url).into(bingPicImg));
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        String cityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        String degree = weather.now.temperature + "???";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(cityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dataText = view.findViewById(R.id.data_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dataText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            minText.setText(forecast.temperature.min);
            maxText.setText(forecast.temperature.max);
            forecastLayout.addView(view);
        }
        if (weather.aqi != null) {
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "?????????:" + weather.suggestion.comfort.info;
        String carWash = "????????????:" + weather.suggestion.carWash.info;
        String sport = "????????????:" + weather.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}