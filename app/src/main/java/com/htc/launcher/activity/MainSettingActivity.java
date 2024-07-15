package com.htc.launcher.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.htc.launcher.R;
import com.htc.launcher.databinding.ActivityMainSettingBinding;

public class MainSettingActivity extends BaseActivity {

    ActivityMainSettingBinding mainSettingBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainSettingBinding = ActivityMainSettingBinding.inflate(LayoutInflater.from(this));
        setContentView(mainSettingBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){
        mainSettingBinding.rlAbout.setOnClickListener(this);
        mainSettingBinding.rlAppsManager.setOnClickListener(this);
        mainSettingBinding.rlBluetooth.setOnClickListener(this);
        mainSettingBinding.rlDateTime.setOnClickListener(this);
        mainSettingBinding.rlLanguage.setOnClickListener(this);
        mainSettingBinding.rlOther.setOnClickListener(this);
        mainSettingBinding.rlProject.setOnClickListener(this);
        mainSettingBinding.rlWifi.setOnClickListener(this);

        mainSettingBinding.rlAbout.setOnHoverListener(this);
        mainSettingBinding.rlAppsManager.setOnHoverListener(this);
        mainSettingBinding.rlBluetooth.setOnHoverListener(this);
        mainSettingBinding.rlDateTime.setOnHoverListener(this);
        mainSettingBinding.rlLanguage.setOnHoverListener(this);
        mainSettingBinding.rlOther.setOnHoverListener(this);
        mainSettingBinding.rlProject.setOnHoverListener(this);
        mainSettingBinding.rlWifi.setOnHoverListener(this);

        mainSettingBinding.rlProject.requestFocus();
        mainSettingBinding.rlProject.requestFocusFromTouch();

    }

    private void initData(){

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_wifi:
                startNewActivity(NetworkActivity.class);
                break;
            case R.id.rl_bluetooth:
                startNewActivity(BluetoothActivity.class);
                break;
            case R.id.rl_project:
                startNewActivity(ProjectActivity.class);
                break;
            case R.id.rl_apps_manager:
                startNewActivity(AppsManagerActivity.class);
                break;
            case R.id.rl_language:
                startNewActivity(LanguageAndKeyboardActivity.class);
                break;
            case R.id.rl_date_time:
                startNewActivity(DateTimeActivity.class);
                break;
            case R.id.rl_other:
                startNewActivity(OtherSettingsActivity.class);
                break;
            case R.id.rl_about:
                startNewActivity(AboutActivity.class);
                break;
        }
    }
}