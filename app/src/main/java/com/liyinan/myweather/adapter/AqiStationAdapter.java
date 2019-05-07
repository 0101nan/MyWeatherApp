package com.liyinan.myweather.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liyinan.myweather.R;
import com.liyinan.myweather.gson.AQI;

public class AqiStationAdapter extends RecyclerView.Adapter<AqiStationAdapter.ViewHolder> {
    private AQI mAQI;

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView mAqiStationName;
        TextView mAqiStationMain;
        TextView mAqiStationQlty;
        TextView mAqiStationAqi;
        public ViewHolder(View itemView){
            super(itemView);
            mAqiStationAqi=itemView.findViewById(R.id.aqi_station_aqi_text);
            mAqiStationMain=itemView.findViewById(R.id.aqi_station_main_text);
            mAqiStationName=itemView.findViewById(R.id.aqi_station_name_text);
            mAqiStationQlty=itemView.findViewById(R.id.aqi_station_qlty_text);
        }

    }

    public AqiStationAdapter(AQI aqi){
        mAQI=aqi;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
         View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_aqi_station,parent,false);
         ViewHolder holder=new ViewHolder(view);
         return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mAqiStationQlty.setText(mAQI.airStationList.get(i).qlty);
        viewHolder.mAqiStationName.setText(mAQI.airStationList.get(i).air_sta);
        viewHolder.mAqiStationMain.setText(mAQI.airStationList.get(i).main);
        viewHolder.mAqiStationAqi.setText(mAQI.airStationList.get(i).aqi);
    }

    @Override
    public int getItemCount() {

        return mAQI.airStationList.size();
    }
}
