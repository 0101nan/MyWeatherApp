package com.liyinan.myweather.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.liyinan.myweather.view.WeatherPerDay;
import com.liyinan.myweather.R;
import com.liyinan.myweather.fragment.WeatherNowDialogFragment;
import com.liyinan.myweather.gson.Weather;
import com.liyinan.myweather.util.Utility;

import static org.litepal.LitePalApplication.getContext;

public class WeatherPerDayAdapter extends RecyclerView.Adapter<WeatherPerDayAdapter.ViewHolder> {
    private  int[] mHeight;
    private  int[] mLows;
    private final int HOURTYPE=0;
    private final int DAYTYPE=1;
    private int mType;
    private  int times;
    private int mMax;
    private int mMin;
    private Weather mWeather;
    private FragmentManager mManager;

    class ViewHolder extends RecyclerView.ViewHolder{
        WeatherPerDay mWeatherPerDay;
        TextView mDateText;
        ImageView mDayImg;
        ImageView mNightImg;
        TextView mDayText;
        TextView mNightText;
        public ViewHolder(View itemView){
            super(itemView);
            mWeatherPerDay =itemView.findViewById(R.id.dv);
            mDateText=itemView.findViewById(R.id.weather_perday_date);
            mDayImg=itemView.findViewById(R.id.weather_perday_imgd);
            mNightImg=itemView.findViewById(R.id.weather_perday_imgn);
            mDayText=itemView.findViewById(R.id.weather_perday_day_text);
            mNightText=itemView.findViewById(R.id.weather_perday_night_text);
        }
    }

    public WeatherPerDayAdapter(int[] height, int[] low, int type, Weather weather, FragmentManager manager){
        mHeight=height;
        mLows=low;
        mType=type;
        mWeather=weather;
        mManager=manager;
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
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherPerDayAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        int prePosition=position-1;
        int nextPosition=position+1;
        if(mType== HOURTYPE){
            switch(position) {
                case 0:
                    holder.mWeatherPerDay.draws(times * mHeight[position], times * mLows[position], times * mHeight[nextPosition], times * mLows[nextPosition], 0,mMax,mMin);
                    //holder.itemView.setBackgroundResource(R.drawable.drawableBackground);
                    break;
                case 23:
                    holder.mWeatherPerDay.draws(times * mHeight[prePosition], times * mLows[prePosition], times * mHeight[position], times * mLows[position], 2, true,mMax,mMin);

                    break;
                default:
                    holder.mWeatherPerDay.draws(times * mHeight[prePosition], times * mLows[prePosition], times * mHeight[position], times * mLows[position], times * mHeight[nextPosition], times * mLows[nextPosition], 1,mMax,mMin);

                    break;
            }
        }else if (mType==DAYTYPE){
                switch(position){
                    case 0:
                        holder.mWeatherPerDay.draws(times*mHeight[position], times*mLows[position], times*mHeight[nextPosition], times*mLows[nextPosition], 0,mMax,mMin);
                        //holder.itemView.setBackgroundResource(R.drawable.drawableBackground);
                        break;
                    case 6:
                        holder.mWeatherPerDay.draws(times*mHeight[prePosition], times*mLows[prePosition], times*mHeight[position], times*mLows[position], 2, true,mMax,mMin);

                        break;
                    default:
                        holder.mWeatherPerDay.draws(times*mHeight[prePosition],times* mLows[prePosition],times* mHeight[position],times* mLows[position], times*mHeight[nextPosition], times*mLows[nextPosition], 1,mMax,mMin);

                        break;
            }
        }
        holder.mWeatherPerDay.setText(mHeight[position],mLows[position]);
        holder.mDateText.setText(mWeather.dailyForecastList.get(position).date.split("-")[2]+"日");
        holder.mDayText.setText(mWeather.dailyForecastList.get(position).cond_txt_d);
        holder.mNightText.setText(mWeather.dailyForecastList.get(position).cond_txt_n);
        String dayImg= Utility.weatherImgTitle(mWeather.dailyForecastList.get(position).cond_code_d,true);
        String nightImg=Utility.weatherImgTitle(mWeather.dailyForecastList.get(position).cond_code_n,false);
        int dayId = getContext().getResources().getIdentifier(dayImg, "drawable", getContext().getPackageName());
        int nightId = getContext().getResources().getIdentifier(nightImg, "drawable", getContext().getPackageName());
        Glide.with(holder.itemView.getContext()).load(dayId).into(holder.mDayImg);
        Glide.with(holder.itemView.getContext()).load(nightId).into(holder.mNightImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherNowDialogFragment fragment=WeatherNowDialogFragment.newInstance(position,mWeather);
                fragment.show(mManager,null);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mHeight.length;
    }
}
