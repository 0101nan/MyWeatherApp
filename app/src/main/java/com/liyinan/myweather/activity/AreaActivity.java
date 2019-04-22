package com.liyinan.myweather.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.liyinan.myweather.fragment.AreaAddFragment;
import com.liyinan.myweather.fragment.AreaListFragment;
import com.liyinan.myweather.R;
import com.liyinan.myweather.util.ActivityCollector;

public class AreaActivity extends AppCompatActivity {
    FrameLayout areaListContainer;
    FrameLayout areaSearchContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        ActivityCollector.addActivity(this);

        Toolbar toolbar=findViewById(R.id.area_activity_toolbar);
        areaListContainer=findViewById(R.id.area_fragment_container);
        areaSearchContainer=findViewById(R.id.search_fragment_container);
        setSupportActionBar(toolbar);
        areaSearchContainer.setVisibility(View.GONE);

        //显示地区列表
        FragmentManager fragmentManager=getSupportFragmentManager();
        Fragment fragment=fragmentManager.findFragmentById(R.id.area_fragment_container);
        if(fragment==null){
            fragment=new AreaListFragment();
            fragmentManager.beginTransaction()
                    .add(R.id.area_fragment_container,fragment)
                    .commit();
        }

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
                areaSearchContainer.setVisibility(View.VISIBLE);
                FragmentManager fragmentManager=getSupportFragmentManager();
                Fragment  fragment=fragmentManager.findFragmentById(R.id.search_fragment_container);
                if(fragment==null){
                    fragment=AreaAddFragment.newInstance(s);
                    fragmentManager.beginTransaction()
                            .add(R.id.search_fragment_container,fragment)
                            .commit();
                }

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

}
