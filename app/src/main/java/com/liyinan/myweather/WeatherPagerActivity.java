package com.liyinan.myweather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import com.liyinan.myweather.db.Area;

import org.litepal.LitePal;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class WeatherPagerActivity extends AppCompatActivity {
    private static final String EXTRA_AREA_ID="com.liyinan.myweather.area_id";

    private ViewPager mViewPager;
    private List<Area> mAreas;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_pager);

        //绑定控件
        mViewPager=findViewById(R.id.weather_view_pager);
        CircleIndicator indicator =findViewById(R.id.indicator);
        mCoordinatorLayout=findViewById(R.id.weather_coordinator_layout);
        floatingActionButton=findViewById(R.id.floating_button);

        //设置悬浮键
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),AreaActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        //查询城市数据库,如果还没添加先暂时添加一个默认地址
        mAreas= LitePal.findAll(Area.class);
        if(mAreas.size()==0){
            Area area=new Area();
            area.setAreaName("无锡");
            area.setAreaCode("CN101190201");
            area.save();
            mAreas= LitePal.findAll(Area.class);
        }
        String areaId=getIntent().getStringExtra(EXTRA_AREA_ID);

        //设置adapter
        FragmentManager fragmentManager=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Area area=mAreas.get(i);
                return WeatherFragment.newInstance(area.getAreaCode());
            }

            @Override
            public int getCount() {
                return mAreas.size();
            }
        });
        for (int i=0;i<mAreas.size();i++){
            if(mAreas.get(i).getAreaCode().equals(areaId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        //解决偶尔会便宜到状态栏中的问题
        ViewCompat.setOnApplyWindowInsetsListener(mViewPager,
                new OnApplyWindowInsetsListener() {
                    @Override
                    public WindowInsetsCompat onApplyWindowInsets(View v,
                                                                  WindowInsetsCompat insets) {
                        insets = ViewCompat.onApplyWindowInsets(v, insets);
                        if (insets.isConsumed()) {
                            return insets;
                        }

                        boolean consumed = false;
                        for (int i = 0, count = mViewPager.getChildCount(); i <  count; i++) {
                            ViewCompat.dispatchApplyWindowInsets(mViewPager.getChildAt(i), insets);
                            if (insets.isConsumed()) {
                                consumed = true;
                            }
                        }
                        return consumed ? insets.consumeSystemWindowInsets() : insets;
                    }
                });

        indicator.setViewPager(mViewPager);
    }

    public static Intent newIntent(Context packageContext, String areaId){
        Intent intent=new Intent(packageContext,WeatherPagerActivity.class);
        intent.putExtra(EXTRA_AREA_ID,areaId);
        return intent;
    }

    //按两次退出程序
    //第一次按返回键系统的时间戳，默认为0。
    private long firstTime = 0;
    @Override
    public void onBackPressed() {
        //第二次按返回键的时间戳
        long secondTime = System.currentTimeMillis();
        //如果第二次的时间戳减去第一次的时间戳大于2000毫秒，则提示再按一次退出，如果小于2000毫秒则直接退出。
        if (secondTime - firstTime > 2000) {
            //弹出是提示消息，推荐Snackbar
            Snackbar sb = Snackbar.make(mCoordinatorLayout, "再按一次退出", Snackbar.LENGTH_SHORT);
            sb.show();
            firstTime = secondTime;
        } else {
            finish();
        }
    }

}
