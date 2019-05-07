package com.liyinan.myweather.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.liyinan.myweather.adapter.SearchAdapter;
import com.liyinan.myweather.fragment.AreaAddFragment;
import com.liyinan.myweather.fragment.AreaListFragment;
import com.liyinan.myweather.R;
import com.liyinan.myweather.gson.AreaBasic;
import com.liyinan.myweather.gson.Location;
import com.liyinan.myweather.util.ActivityCollector;
import com.liyinan.myweather.util.HttpUtil;
import com.liyinan.myweather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AreaActivity extends AppCompatActivity {
    FrameLayout areaListContainer;
    private List<AreaBasic> mSearchResultList=new ArrayList<>();
    private SearchAdapter mSearchAdapter;
    private RecyclerView mRecyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        ActivityCollector.addActivity(this);

        Toolbar toolbar=findViewById(R.id.area_activity_toolbar);
        areaListContainer=findViewById(R.id.area_fragment_container);
        mRecyclerView=findViewById(R.id.search_recyclerview);
        setSupportActionBar(toolbar);

        //显示地区列表
        FragmentManager fragmentManager=getSupportFragmentManager();
        Fragment fragment=fragmentManager.findFragmentById(R.id.area_fragment_container);
        if(fragment==null){
            fragment=new AreaListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.area_fragment_container,fragment)
                    .commit();
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSearchAdapter=new SearchAdapter(this,mSearchResultList);
        mRecyclerView.setAdapter(mSearchAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_area,menu);

        MenuItem searchItem=menu.findItem(R.id.menu_area_search);
        final SearchView searchView=(SearchView) searchItem.getActionView();
        searchView.setQueryHint(getResources().getString(R.string.search_area_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                areaListContainer.setVisibility(View.GONE);
                requestArea(s);
                return true;
            }
        });
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
    private void requestArea(final CharSequence inputText){
        String searchUrl="https://search.heweather.net/find?location="+inputText.toString()+"&key=4477c8824b5f44da84a872578614bdc2&group=cn";
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mSearchAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
    }
}
