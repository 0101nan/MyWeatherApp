package com.liyinan.myweather;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liyinan.myweather.db.Area;

import org.litepal.LitePal;

import java.util.List;
import java.util.WeakHashMap;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {
    private List<Area>  mAreaList;
    private Area mArea;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView areaNameTextView;
        View areaView;
        public ViewHolder(View view){
            super(view);
            areaView=view;
            areaNameTextView=view.findViewById(R.id.area_name);
        }
    }

    public AreaAdapter(List<Area> areaList){
        mAreaList=areaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.area_list_item,parent,false);
        ViewHolder holder=new ViewHolder(view);
        holder.areaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=WeatherPagerActivity.newIntent(v.getContext(),mArea.getAreaCode());
                v.getContext().startActivity(intent);
            }
        });
        holder.areaView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LitePal.deleteAll(Area.class,"mAreaName=?","mArea.getAreaName()");
                notifyDataSetChanged();
                return true;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        mArea=mAreaList.get(i);
        holder.areaNameTextView.setText(mArea.getAreaName());
    }

    @Override
    public int getItemCount() {
        return mAreaList.size();
    }
}
