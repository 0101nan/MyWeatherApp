package com.liyinan.myweather;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;
import android.widget.Toast;

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
        int position=holder.getAdapterPosition();
        holder.areaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Intent intent=WeatherPagerActivity.newIntent(v.getContext(),mAreaList.get(position).getAreaCode());
                v.getContext().startActivity(intent);
            }
        });
        holder.areaView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Snackbar.make(v,"是否删除该城市？",Snackbar.LENGTH_SHORT)
                        .setAction("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int position=holder.getAdapterPosition();
                                LitePal.deleteAll(Area.class,"mAreaCode=?",mAreaList.get(position).getAreaCode());
                                Toast.makeText(v.getContext(), "已删除:"+mAreaList.get(position).getAreaName(), Toast.LENGTH_SHORT).show();
                                mAreaList=LitePal.findAll(Area.class);
                                AreaAdapter.this.notifyDataSetChanged();
                            }
                        }).show();
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
