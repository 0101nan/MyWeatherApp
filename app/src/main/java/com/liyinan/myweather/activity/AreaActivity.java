package com.liyinan.myweather.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.liyinan.myweather.fragment.AreaAddFragment;
import com.liyinan.myweather.fragment.AreaListFragment;
import com.liyinan.myweather.R;
import com.liyinan.myweather.util.ActivityCollector;

public class AreaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);
        ActivityCollector.addActivity(this);

        Toolbar toolbar=findViewById(R.id.area_activity_toolbar);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_area,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.new_area:
                FragmentManager fragmentManager=getSupportFragmentManager();
                Fragment fragment=new AreaAddFragment();
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.area_fragment_container,fragment)
                        .commit();
                break;
                default:
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}