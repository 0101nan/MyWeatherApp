package com.liyinan.myweather.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.liyinan.myweather.R;
import com.liyinan.myweather.adapter.SearchAdapter;
import com.liyinan.myweather.gson.AreaBasic;
import com.liyinan.myweather.gson.Location;
import com.liyinan.myweather.util.HttpUtil;
import com.liyinan.myweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AreaAddFragment extends Fragment {
    private EditText mEditText;
    private List<AreaBasic> mSearchResultList=new ArrayList<>();
    private RecyclerView mRecyclerView;
    private SearchAdapter mAreaAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_area,container,false);

        //响应输入框
        mEditText=view.findViewById(R.id.search_area_edit_text);
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()>0) {
                    requestArea(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //设置列表
        mRecyclerView=view.findViewById(R.id.search_area_recyeler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //添加横向的分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        mAreaAdapter=new SearchAdapter(getActivity(),mSearchResultList);
        mRecyclerView.setAdapter(mAreaAdapter);

        return view;
    }

    //请求搜索结果
    private void requestArea(final CharSequence inputText){
        String searchUrl="https://search.heweather.net/find?location="+inputText.toString()+"&key=ab4bb0964d4d4b3894f8cdaf1b79302c&group=cn";
        HttpUtil.sendOkHttpRequest(searchUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText=response.body().string();
                final Location location= Utility.handleAreaResponse(responseText);

                //如果有搜索结果才向列表中添加
                if (location.status.equals("ok")){
                    //清空列表，否则会叠加
                    mSearchResultList.clear();
                    //添加搜索结果
                    for (AreaBasic areaBasic:location.AreaBasicList){
                        mSearchResultList.add(areaBasic);
                    }
                    //主线程更新列表
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAreaAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
