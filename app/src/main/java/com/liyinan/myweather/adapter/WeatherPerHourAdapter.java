package com.liyinan.myweather.adapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liyinan.myweather.fragment.WeatherHourlyDialogFragment;
import com.liyinan.myweather.R;
import com.liyinan.myweather.gson.Weather;
import com.liyinan.myweather.util.Utility;
import com.liyinan.myweather.view.WeatherPerHourView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.litepal.LitePalApplication.getContext;

public class WeatherPerHourAdapter extends RecyclerView.Adapter<WeatherPerHourAdapter.ViewHolder> {
    private List<Integer> mHeight;
    private int mMax;
    private int mMin;
    private Weather mWeather;
    private FragmentManager mManager;

    class ViewHolder extends RecyclerView.ViewHolder{
        WeatherPerHourView mWeatherPerHourView;
        TextView mTimeText;
        ImageView mWeatherImg;
        TextView mWeatherText;
        public ViewHolder(View itemView){
            super(itemView);
            mWeatherPerHourView=itemView.findViewById(R.id.weather_perhour_linechart);
            mTimeText=itemView.findViewById(R.id.weather_perhour_time);
            mWeatherImg=itemView.findViewById(R.id.weather_perhour_img);
            mWeatherText=itemView.findViewById(R.id.weather_perhour_cond);
        }
    }

    public WeatherPerHourAdapter(List<Integer> height,Weather weather,FragmentManager manager){
        mHeight=height;
        mWeather=weather;
        mManager=manager;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_weather_perhour,null,false);
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
    public void onBindViewHolder(@NonNull WeatherPerHourAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        int prePosition=position-1;
        int nextPosition=position+1;

        switch(position) {
            case 0:
                holder.mWeatherPerHourView.draws(mHeight.get(position),mHeight.get(nextPosition),0,mMax,mMin);
                break;
            case 23:
                holder.mWeatherPerHourView.draws(mHeight.get(prePosition),mHeight.get(position), 2, true,mMax,mMin);
                break;
            default:
                holder.mWeatherPerHourView.draws(mHeight.get(prePosition),mHeight.get(position),mHeight.get(nextPosition), 1,mMax,mMin);
                break;
        }

        holder.mWeatherPerHourView.setText(mHeight.get(position));
        holder.mTimeText.setText(mWeather.hourlyList.get(position).time.split(" ")[1]);
        holder.mWeatherText.setText(mWeather.hourlyList.get(position).cond_txt);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = sdf.parse(mWeather.hourlyList.get(position).time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 20 || calendar.get(Calendar.HOUR_OF_DAY) <= 6) {
                String weatherImg= Utility.weatherImgTitle(mWeather.hourlyList.get(position).cond_code,false);
                int weatherId = getContext().getResources().getIdentifier(weatherImg, "drawable", getContext().getPackageName());
                Glide.with(holder.itemView.getContext()).load(weatherId).into(holder.mWeatherImg);
            }else{
                String weatherImg= Utility.weatherImgTitle(mWeather.hourlyList.get(position).cond_code,true);
                int weatherId = getContext().getResources().getIdentifier(weatherImg, "drawable", getContext().getPackageName());
                Glide.with(holder.itemView.getContext()).load(weatherId).into(holder.mWeatherImg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherHourlyDialogFragment weatherHourlyDialogFragment=WeatherHourlyDialogFragment.newInstance(position,mWeather);
                weatherHourlyDialogFragment.show(mManager,null);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mHeight.size();
    }
}
