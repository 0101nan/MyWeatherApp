package com.liyinan.myweather.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.util.Log;
import android.view.View;

import com.liyinan.myweather.R;
import com.liyinan.myweather.fragment.WeatherFragment;
import com.liyinan.myweather.gson.Area1;
import com.liyinan.myweather.service.AutoUpdateService;
import com.liyinan.myweather.service.UpdateJobService;
import com.liyinan.myweather.util.ActivityCollector;
import com.liyinan.myweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class WeatherPagerActivity extends AppCompatActivity {
    private static final String TAG = "WeatherPagerActivity";
    private static final String EXTRA_AREA_ID="com.liyinan.myweather.area_id";
    private static JobScheduler mJobScheduler;
    private static int JOB_ID=0;

    private ViewPager mViewPager;
    private List<Area1> mAreas=new ArrayList<>();
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton floatingActionButton;
    private CircleIndicator mIndicator;
    //private JobScheduler mJobScheduler=null;
    //int JOB_ID=0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //融合状态栏
        View decorview=getWindow().getDecorView();
        decorview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        setContentView(R.layout.activity_weather_pager);
        ActivityCollector.addActivity(this);

        //绑定控件
        mViewPager=findViewById(R.id.weather_view_pager);
        mIndicator=findViewById(R.id.indicator);
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

        //查询城市数据库，若为空则启动添加页面
        String areaId=getIntent().getStringExtra(EXTRA_AREA_ID);
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String jsonAreaList=prefs.getString("areaList",null);
        if(jsonAreaList!=null){
            mAreas= Utility.handleAreaList(jsonAreaList);
        }else{
            Intent intent=new Intent(this,AreaActivity.class);
            startActivity(intent);
        }

        //设置adapter
        FragmentManager fragmentManager=getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {
            @Override
            public Fragment getItem(int i) {
                Area1 area=mAreas.get(i);
                return WeatherFragment.newInstance(area.getAreaCode());
            }
            @Override
            public int getCount() {
                return mAreas.size();
            }

            @Override
            public int getItemPosition(@NonNull Object object) {
                return POSITION_NONE;
            }
        });
        for (int i=0;i<mAreas.size();i++){
            if(mAreas.get(i).getAreaCode().equals(areaId)){
                mViewPager.setCurrentItem(i);
                break;
            }
        }

        //解决偶尔会偏移到状态栏中的问题
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

        mIndicator.setViewPager(mViewPager);

    }

    public static Intent newIntent(Context packageContext, String areaId){
        Intent intent=new Intent(packageContext,WeatherPagerActivity.class);
        intent.putExtra(EXTRA_AREA_ID,areaId);
        return intent;
    }

    public static void startService(Context context){
        Log.d(TAG, "startService: jobstart???????????????????????????");
        if(mJobScheduler!=null){
            mJobScheduler.cancel(JOB_ID);
        }
        mJobScheduler=(JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        JobInfo.Builder builder=new JobInfo.Builder(JOB_ID,new ComponentName(context,UpdateJobService.class));
        builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        builder.setMinimumLatency(1000*60*60);
        builder.setOverrideDeadline(1000*60*90);
        mJobScheduler.schedule(builder.build());
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
            ActivityCollector.finishAll();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //刷新viewpager
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        String jsonAreaList=prefs.getString("areaList",null);
        if(jsonAreaList!=null){
            mAreas= Utility.handleAreaList(jsonAreaList);
            mViewPager.getAdapter().notifyDataSetChanged();
            mIndicator.setViewPager(mViewPager);
        }
    }
}
