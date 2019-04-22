package com.liyinan.myweather.adapter;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.liyinan.myweather.R;
import com.liyinan.myweather.activity.WeatherPagerActivity;
import com.liyinan.myweather.gson.Area1;
import com.liyinan.myweather.gson.AreaBasic;
import com.liyinan.myweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<AreaBasic>  mAreaList;
    private Activity mAreaActivity;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView searchTextView;
        View areaView;
        public ViewHolder(View view){
            super(view);
            areaView=view;
            searchTextView=view.findViewById(R.id.search_area_name);
        }
    }

    public SearchAdapter(Activity areaActivity,List<AreaBasic> areaList){
        mAreaActivity=areaActivity;
        mAreaList=areaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.search_list_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        holder.areaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //读取城市列表，若为空则需要新建列表
                SharedPreferences pref=PreferenceManager.getDefaultSharedPreferences(v.getContext());
                String jsonAreaList=pref.getString("areaList",null);
                List<Area1> area1List= Utility.handleAreaList(jsonAreaList);
                if(area1List==null){
                    area1List=new ArrayList<>();
                }
                //点击添加城市到数据库
                int position=holder.getAdapterPosition();
                AreaBasic areaBasic=mAreaList.get(position);
                Area1 area1=new Area1();
                area1.setAreaName(areaBasic.location);
                area1.setAreaCode(areaBasic.cid);
                area1List.add(area1);

                //储存
                Gson gson=new Gson();
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(v.getContext()).edit();
                editor.putString("areaList",gson.toJson(area1List));
                editor.apply();

                Intent intent= WeatherPagerActivity.newIntent(v.getContext(),areaBasic.cid);
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        AreaBasic area=mAreaList.get(i);
        holder.searchTextView.setText(area.location+" - "+area.parent_city+" - "+area.admin_area);
    }

    @Override
    public int getItemCount() {
        return mAreaList.size();
    }
}
