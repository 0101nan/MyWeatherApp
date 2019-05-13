package com.liyinan.myweather.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.Snackbar;
import com.liyinan.myweather.R;
import com.liyinan.myweather.activity.SettingActivity;

public class SettingFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preference,rootKey);

    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch(preference.getKey()){
            case "area_list":
                getActivity().finish();
                break;
            case "version":
                break;
            case "github":
                Intent intent =new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/0101nan/MyWeatherApp"));
                startActivity(intent);
        }
        return super.onPreferenceTreeClick(preference);
    }
}
