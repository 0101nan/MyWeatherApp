package com.liyinan.myweather.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.liyinan.myweather.R;
import com.liyinan.myweather.gson.Weather;

public class WeatherNowDialogFragment extends DialogFragment {
    private static final String POSITION="position";
    private static final String WEATHER="weather";
    private TextView max_tmp_text;
    private TextView min_tmp_text;
    private TextView pop_text;
    private TextView pcpn_text;
    private TextView wind_dir_text;
    private TextView wind_sc_text;
    private TextView uv_index_text;
    private TextView vis_text;
    private TextView DailyDateTextView;
    private TextView DailyCondDayTextView;
    private TextView DailyCondNightTextView;

    public static WeatherNowDialogFragment newInstance(Integer position, Weather weather){
        Bundle args=new Bundle();
        args.putInt(POSITION,position);
        args.putSerializable(WEATHER,weather);

        WeatherNowDialogFragment weatherNowDialogFragment=new WeatherNowDialogFragment();
        weatherNowDialogFragment.setArguments(args);
        return weatherNowDialogFragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.weather_daily_dialog,null);
        Weather weather=(Weather)getArguments().getSerializable(WEATHER);
        Integer position=getArguments().getInt(POSITION);

        max_tmp_text=v.findViewById(R.id.max_tmp_text);
        min_tmp_text=v.findViewById(R.id.min_tmp_text);
        pop_text=v.findViewById(R.id.pop_text);
        pcpn_text=v.findViewById(R.id.pcpn_text);
        wind_dir_text=v.findViewById(R.id.wind_dir_text);
        wind_sc_text=v.findViewById(R.id.wind_sc_text);
        uv_index_text=v.findViewById(R.id.uv_index_text);
        vis_text=v.findViewById(R.id.vis_text);
        DailyDateTextView=v.findViewById(R.id.date_text);
        DailyCondDayTextView=v.findViewById(R.id.cond_txt_d_text);
        DailyCondNightTextView=v.findViewById(R.id.cond_txt_n_text);

        max_tmp_text.setText(weather.dailyForecastList.get(position).tmp_max+"℃");
        min_tmp_text.setText(weather.dailyForecastList.get(position).tmp_min+"℃");
        pop_text.setText(weather.dailyForecastList.get(position).pop+"%");
        pcpn_text.setText(weather.dailyForecastList.get(position).pcpn+"mm");
        wind_dir_text.setText(weather.dailyForecastList.get(position).wind_dir);
        wind_sc_text.setText(weather.dailyForecastList.get(position).wind_sc+"级");
        uv_index_text.setText(weather.dailyForecastList.get(position).uv_index+"级");
        vis_text.setText(weather.dailyForecastList.get(position).vis+"km");
        DailyDateTextView.setText(weather.dailyForecastList.get(position).date);
        DailyCondDayTextView.setText(weather.dailyForecastList.get(position).cond_txt_d);
        DailyCondNightTextView.setText(weather.dailyForecastList.get(position).cond_txt_n);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();


    }

}
