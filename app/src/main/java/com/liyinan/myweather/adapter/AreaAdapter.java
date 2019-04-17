package com.liyinan.myweather.adapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.liyinan.myweather.R;
import com.liyinan.myweather.activity.WeatherPagerActivity;
import com.liyinan.myweather.gson.Area1;

import java.util.Collections;
import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {
    private List<Area1>  mAreaList;
    private Area1 mArea;
    private SharedPreferences.Editor editor;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView areaNameTextView;
        View areaView;
        ImageView areaTitleImg;
        public ViewHolder(View view){
            super(view);
            areaView=view;
            areaNameTextView=view.findViewById(R.id.area_name);
            areaTitleImg=view.findViewById(R.id.area_item_title_img);
        }

    }

    public AreaAdapter(List<Area1> areaList){
        mAreaList=areaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.area_list_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        editor=PreferenceManager.getDefaultSharedPreferences(view.getContext()).edit();
        holder.areaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Intent intent= WeatherPagerActivity.newIntent(v.getContext(),mAreaList.get(position).getAreaCode());
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        mArea=mAreaList.get(i);
        holder.areaNameTextView.setText(mArea.getAreaName());

        //从内存读取对应图片地址并显示，若为空则显示默认
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(holder.areaView.getContext());
        int resID=preferences.getInt("area_titleImg"+mArea.getAreaCode(),0);
        if(resID==0) {
            Glide.with(holder.areaView.getContext()).load(R.drawable.title_img_1_0).into(holder.areaTitleImg);
        }else{
            Glide.with(holder.areaView.getContext()).load(resID).into(holder.areaTitleImg);
        }

    }

    @Override
    public int getItemCount() {
        return mAreaList.size();
    }

    //删除数据
    public final void delData(int position){
        mAreaList.remove(position);
        Gson gson=new Gson();
        editor.putString("areaList",gson.toJson(mAreaList));
        editor.apply();
        notifyItemRemoved(position);
    }

    //交换数据
    public final void move(int fromPosition,int toPosition){
        Collections.swap(mAreaList,fromPosition,toPosition);
        Gson gson=new Gson();
        editor.putString("areaList",gson.toJson(mAreaList));
        editor.apply();
        notifyItemMoved(fromPosition,toPosition);
    }



}
