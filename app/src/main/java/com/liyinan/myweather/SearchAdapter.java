package com.liyinan.myweather;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.liyinan.myweather.db.Area;
import com.liyinan.myweather.gson.AreaBasic;
import com.liyinan.myweather.gson.Location;

import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private List<AreaBasic>  mAreaList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView searchTextView;
        View areaView;
        public ViewHolder(View view){
            super(view);
            areaView=view;
            searchTextView=view.findViewById(R.id.search_area_name);
        }
    }

    public SearchAdapter(List<AreaBasic> areaList){
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
                //点击添加城市到数据库
                int position=holder.getAdapterPosition();
                AreaBasic areaBasic=mAreaList.get(position);
                Area area=new Area();
                area.setAreaName(areaBasic.location);
                area.setAreaCode(areaBasic.cid);
                area.save();

                //切回城市列表
                Intent intent=new Intent(v.getContext(),AreaActivity.class);
                v.getContext().startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
        AreaBasic area=mAreaList.get(i);
        holder.searchTextView.setText(area.location);
    }

    @Override
    public int getItemCount() {
        return mAreaList.size();
    }
}
