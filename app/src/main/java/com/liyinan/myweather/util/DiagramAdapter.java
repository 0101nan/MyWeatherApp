package com.liyinan.myweather.util;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liyinan.myweather.DiagramView;
import com.liyinan.myweather.R;
import com.liyinan.myweather.WeatherPagerActivity;
import com.liyinan.myweather.gson.Weather;

public class DiagramAdapter extends RecyclerView.Adapter<DiagramAdapter.ViewHolder> {
    private  int[] mHeight;
    private  int[] mLows;
    private final int HOURTYPE=0;
    private final int DAYTYPE=1;
    private int mType;
    private  int times;
    private int mMax;
    private int mMin;
    private Weather mWeather;

    class ViewHolder extends RecyclerView.ViewHolder{
        DiagramView mDiagramView;
        TextView mDateText;
        ImageView mDayImg;
        ImageView mNightImg;
        public ViewHolder(View itemView){
            super(itemView);
            mDiagramView=itemView.findViewById(R.id.dv);
            mDateText=itemView.findViewById(R.id.weather_perday_date);
            mDayImg=itemView.findViewById(R.id.weather_perday_imgd);
            mNightImg=itemView.findViewById(R.id.weather_perday_imgn);
        }
    }

    public DiagramAdapter(int[] height, int[] low, int type,Weather weather){
        mHeight=height;
        mLows=low;
        mType=type;
        mWeather=weather;
        caculateTimes();
    }

    private void caculateTimes() {
        /*
        int max =mHeight[0];
        int min = mLows[0];
        for (int i=1;i<mHeight.length;i++){
            if (mHeight[i]>max){
                max = mHeight[i];
            }
            if (mHeight[i]<min){
                min = mHeight[i];
            }
        }
        int difference = max-min;
        if (difference<=10&&difference>5){
            times = 5;
        }else if (difference<=5&&difference>=3){
            times = 7;
        }else if (difference<3){
            times = 10;
        }else if (difference>10&&difference<=13){
            times = 3;
        }else {
            times = 1;
        }
        */
        times=1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_weather_perday,null,false);
        ViewHolder holder=new ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mMax=mHeight[0];
        mMin=mLows[0];
        for (int j=0;j<mHeight.length;j++){
            if(mHeight[j]>mMax){
                mMax=mHeight[j];
            }
            if(mLows[j]<mMin){
                mMin=mLows[j];
            }
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiagramAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        int prePosition=position-1;
        int nextPosition=position+1;
        if(mType== HOURTYPE){
            switch(position) {
                case 0:
                    holder.mDiagramView.draws(times * mHeight[position], times * mLows[position], times * mHeight[nextPosition], times * mLows[nextPosition], 0,mMax,mMin);
                    //holder.itemView.setBackgroundResource(R.drawable.drawableBackground);
                    break;
                case 23:
                    holder.mDiagramView.draws(times * mHeight[prePosition], times * mLows[prePosition], times * mHeight[position], times * mLows[position], 2, true,mMax,mMin);

                    break;
                default:
                    holder.mDiagramView.draws(times * mHeight[prePosition], times * mLows[prePosition], times * mHeight[position], times * mLows[position], times * mHeight[nextPosition], times * mLows[nextPosition], 1,mMax,mMin);

                    break;
            }
        }else if (mType==DAYTYPE){
                switch(position){
                    case 0:
                        holder.mDiagramView.draws(times*mHeight[position], times*mLows[position], times*mHeight[nextPosition], times*mLows[nextPosition], 0,mMax,mMin);
                        //holder.itemView.setBackgroundResource(R.drawable.drawableBackground);
                        break;
                    case 6:
                        holder.mDiagramView.draws(times*mHeight[prePosition], times*mLows[prePosition], times*mHeight[position], times*mLows[position], 2, true,mMax,mMin);

                        break;
                    default:
                        holder.mDiagramView.draws(times*mHeight[prePosition],times* mLows[prePosition],times* mHeight[position],times* mLows[position], times*mHeight[nextPosition], times*mLows[nextPosition], 1,mMax,mMin);

                        break;
            }
        }
        holder.mDiagramView.setText(mHeight[position],mLows[position]);
        holder.mDateText.setText(mWeather.dailyForecastList.get(position).date.split("-")[2]);
        Glide.with(holder.itemView.getContext()).load(R.drawable.weather_snow_rain).into(holder.mDayImg);
        Glide.with(holder.itemView.getContext()).load(R.drawable.weather_snow_rain).into(holder.mNightImg);
    }

    @Override
    public int getItemCount() {
        return mHeight.length;
    }
}
