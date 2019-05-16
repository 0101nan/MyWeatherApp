package com.liyinan.myweather.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.liyinan.myweather.R;
import com.liyinan.myweather.fragment.WeatherHourlyDialogFragment;
import com.liyinan.myweather.gson.Pcpn;
import com.liyinan.myweather.gson.Weather;
import com.liyinan.myweather.util.Utility;
import com.liyinan.myweather.view.WeatherPcpnView;
import com.liyinan.myweather.view.WeatherPerHourView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class WeatherPcpnAdapter extends RecyclerView.Adapter<WeatherPcpnAdapter.ViewHolder> {
    private List<Float> mHeight;
    private Float mMax;
    private Float mMin;
    private Pcpn mPcpn;
    class ViewHolder extends RecyclerView.ViewHolder{
        WeatherPcpnView mWeatherPcpnView;
        TextView mTimeText;
        public ViewHolder(View itemView){
            super(itemView);
            mWeatherPcpnView=itemView.findViewById(R.id.weather_pcpn_linechart);
            mTimeText=itemView.findViewById(R.id.weather_pcpn_time);
        }
    }

    public WeatherPcpnAdapter(List<Float> height, Pcpn pcpn){
        mHeight=height;
        mPcpn=pcpn;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_pcpn,null,false);
        ViewHolder holder=new ViewHolder(view);

        //查找最值
        mMax=mHeight.get(0);
        mMin=mHeight.get(0);
        for (int j=0;j<mHeight.size();j++){
            if(mHeight.get(j)>mMax){
                mMax=mHeight.get(j);
            }
            if(mHeight.get(j)<mMin){
                mMin=mHeight.get(j);
            }
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherPcpnAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        int prePosition=position-1;
        int nextPosition=position+1;

        switch(position) {
            case 0:
                holder.mWeatherPcpnView.draws(mHeight.get(position),mHeight.get(nextPosition),0,mMax,mMin);
                break;
            case 23:
                holder.mWeatherPcpnView.draws(mHeight.get(prePosition),mHeight.get(position), 2, true,mMax,mMin);
                break;
            default:
                holder.mWeatherPcpnView.draws(mHeight.get(prePosition),mHeight.get(position),mHeight.get(nextPosition), 1,mMax,mMin);
                break;
        }

        holder.mWeatherPcpnView.setText(mHeight.get(position));
        holder.mTimeText.setText(mPcpn.Pcpn5mList.get(position).time.split(" ")[1]);
    }

    @Override
    public int getItemCount() {
        return mHeight.size();
    }
}
