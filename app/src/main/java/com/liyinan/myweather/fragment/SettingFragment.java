package com.liyinan.myweather.fragment;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.liyinan.myweather.R;

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
        }
        return super.onPreferenceTreeClick(preference);
    }
}
