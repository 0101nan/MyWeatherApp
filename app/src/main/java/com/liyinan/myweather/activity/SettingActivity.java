package com.liyinan.myweather.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;

import com.liyinan.myweather.R;
import com.liyinan.myweather.fragment.SettingFragment;

public class SettingActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        mToolbar=findViewById(R.id.setting_toolbar);

        setSupportActionBar(mToolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.setting_container,new SettingFragment()).commit();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
