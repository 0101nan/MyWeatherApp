package com.liyinan.myweather.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.liyinan.myweather.R;
import com.liyinan.myweather.gson.Weather;

public class WeatherHourlyDialogFragment extends DialogFragment {
    private static final String POSITION="position";
    private static final String WEATHER="weather";

    private TextView hourlyTime;
    private TextView hourlyTmpText;
    private TextView hourlyCondText;
    private TextView hourlyPopText;
    private TextView hourlyHumText;
    private TextView hourlyWindDirText;
    private TextView hourlyWindScText;
    private TextView hourlyDewText;
    private TextView hourlyCloudText;

    public static WeatherHourlyDialogFragment newInstance(Integer position, Weather weather){
        Bundle args=new Bundle();
        args.putInt(POSITION,position);
        args.putSerializable(WEATHER,weather);

        WeatherHourlyDialogFragment weatherHourlyDialogFragment=new WeatherHourlyDialogFragment();
        weatherHourlyDialogFragment.setArguments(args);
        return weatherHourlyDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.weather_hourly_dialog,null);
        Weather weather=(Weather)getArguments().getSerializable(WEATHER);
        Integer position=getArguments().getInt(POSITION);

        hourlyTime=v.findViewById(R.id.hourly_time);
        hourlyTmpText=v.findViewById(R.id.hourly_tmp_text);
        hourlyCondText=v.findViewById(R.id.hourly_cond_text);
        hourlyPopText=v.findViewById(R.id.hourly_pop_text);
        hourlyHumText=v.findViewById(R.id.hourly_hum_text);
        hourlyWindDirText=v.findViewById(R.id.hourly_wind_dir_text);
        hourlyWindScText=v.findViewById(R.id.hourly_wind_sc_text);
        hourlyDewText=v.findViewById(R.id.hourly_dew_text);
        hourlyCloudText=v.findViewById(R.id.hourly_cloud_text);

        hourlyTime.setText(weather.hourlyList.get(position).time);
        hourlyTmpText.setText(weather.hourlyList.get(position).tmp+"℃");
        hourlyCondText.setText(weather.hourlyList.get(position).cond_txt);
        hourlyPopText.setText(weather.hourlyList.get(position).pop+"%");
        hourlyHumText.setText(weather.hourlyList.get(position).hum+"%");
        hourlyWindDirText.setText(weather.hourlyList.get(position).wind_dir);
        hourlyWindScText.setText(weather.hourlyList.get(position).wind_sc+"级");
        hourlyDewText.setText(weather.hourlyList.get(position).dew+"℃");
        hourlyCloudText.setText(weather.hourlyList.get(position).cloud+"%");
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }

}
