package com.liyinan.myweather.activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.liyinan.myweather.adapter.SearchAdapter;
import com.liyinan.myweather.fragment.AreaListFragment;
import com.liyinan.myweather.R;
import com.liyinan.myweather.gson.AreaBasic;
import com.liyinan.myweather.gson.Location;
import com.liyinan.myweather.util.ActivityCollector;
import com.liyinan.myweather.util.HttpUtil;
import com.liyinan.myweather.util.Utility;

import java.io.IOException;
import java.lang.reflect.Method;
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
    private SearchView mSearchView;

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

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Method method = mSearchView.getClass().getDeclaredMethod("onCloseClicked");
                    method.setAccessible(true);
                    method.invoke(mSearchView);
                }catch (Exception e){
                    e.printStackTrace();
                }
                mRecyclerView.setVisibility(View.GONE);
                areaListContainer.setVisibility(View.VISIBLE);
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_setting:
                        Intent intent=new Intent(AreaActivity.this,SettingActivity.class);
                        startActivity(intent);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_area,menu);
        MenuItem searchItem=menu.findItem(R.id.menu_area_search);
        mSearchView=(SearchView) searchItem.getActionView();
        mSearchView.setQueryHint(getResources().getString(R.string.search_area_hint));
        mSearchView.setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                areaListContainer.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
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
