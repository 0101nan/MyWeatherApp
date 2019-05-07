package com.liyinan.myweather.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.liyinan.myweather.R;
import com.liyinan.myweather.adapter.AqiStationAdapter;
import com.liyinan.myweather.gson.AQI;

public class AQIStationDialogFragment extends DialogFragment {
    private static final String AQI_DATA="aqi";
    private RecyclerView mAqiStationRecyclerView;
    private AqiStationAdapter mAqiStationAdapter;

    public static AQIStationDialogFragment newInstance(com.liyinan.myweather.gson.AQI aqi){
        Bundle args=new Bundle();
        args.putSerializable(AQI_DATA,aqi);

        AQIStationDialogFragment aqiStationDialogFragment=new AQIStationDialogFragment();
        aqiStationDialogFragment.setArguments(args);
        return aqiStationDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View v= LayoutInflater.from(getActivity()).inflate(R.layout.aqi_station_dialog,null);
        AQI aqi=(AQI) getArguments().getSerializable(AQI_DATA);
        mAqiStationRecyclerView=v.findViewById(R.id.aqi_station_recyclerview);
        mAqiStationRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAqiStationAdapter=new AqiStationAdapter(aqi);
        mAqiStationRecyclerView.setAdapter(mAqiStationAdapter);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();
    }
}
