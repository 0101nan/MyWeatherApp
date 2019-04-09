package com.liyinan.myweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liyinan.myweather.db.Area;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class AreaListFragment extends Fragment {
    private AreaAdapter mAreaAdapter;
    private RecyclerView mRecyclerView;
    private List<Area> mAreaList=new ArrayList<>();
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_list_area,container,false);
        mRecyclerView=view.findViewById(R.id.area_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAreaList=LitePal.findAll(Area.class);
        mAreaAdapter=new AreaAdapter(mAreaList);
        mRecyclerView.setAdapter(mAreaAdapter);
        return view;
    }

    //更新列表
    @Override
    public void onResume() {
        super.onResume();
        mAreaAdapter.notifyDataSetChanged();
    }

}
