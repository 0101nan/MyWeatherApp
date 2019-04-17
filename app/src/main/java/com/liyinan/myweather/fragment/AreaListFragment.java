package com.liyinan.myweather.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liyinan.myweather.R;
import com.liyinan.myweather.adapter.AreaAdapter;
import com.liyinan.myweather.gson.Area1;
import com.liyinan.myweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class AreaListFragment extends Fragment {
    private AreaAdapter mAreaAdapter;
    private RecyclerView mRecyclerView;

    private List<Area1> mArea1List=new ArrayList<>();
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
        //读取城市列表
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String jsonAreaList=prefs.getString("areaList",null);
        if(jsonAreaList!=null){
            mArea1List= Utility.handleAreaList(jsonAreaList);
        }
        //绑定adapter
        mAreaAdapter=new AreaAdapter(mArea1List);
        mRecyclerView.setAdapter(mAreaAdapter);

        //设置横划和拖动
        final ItemTouchHelper itemTouchHelper=new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags=ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
                int swipeFlags=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
                return makeMovementFlags(dragFlags,swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                mAreaAdapter.move(viewHolder.getAdapterPosition(),target.getAdapterPosition());
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                mAreaAdapter.delData(viewHolder.getAdapterPosition());
            }

        });
        //关联recyclerview
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        return view;
    }

    //更新列表
    @Override
    public void onResume() {
        super.onResume();
        mAreaAdapter.notifyDataSetChanged();
    }

}
