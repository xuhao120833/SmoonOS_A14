package com.htc.luminaos.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.databinding.ActivityProjectBinding;
import com.htc.luminaos.databinding.ResetKeystoreLayoutBinding;
import com.htc.luminaos.utils.AppUtils;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.KeystoneUtils;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.ReflectUtil;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.utils.ToastUtil;
import com.softwinner.tv.AwTvDisplayManager;
import com.softwinner.tv.common.AwTvDisplayTypes;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProjectActivity extends BaseActivity implements View.OnKeyListener {

    private ActivityProjectBinding projectBinding;
    private int cur_project_mode = 0;
    List<String> project_name = new ArrayList<>();
    private AwTvDisplayManager tvDisplayManager;

    private String TAG = "ProjectActivity";

    private ExecutorService singer;
    private int left = 100;
    private int top = 100;
    private  int right = 100;
    private int bottom = 100;
    private int max_value = 100;
    private int All;
    private int ZOOM_MAX = 20;
    DecimalFormat df = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.CHINESE));//格式化小数
    private double scale = 1D;//缩放比例，根据选中的屏幕缩放模式
    private int step_x = 16;//X轴步进
    private int step_y = 9;//Y轴步进
    double zoom_step_x =(double) KeystoneUtils.lcd_w / 100 ;
    double zoom_step_y =(double) KeystoneUtils.lcd_h / 100 ;
    private SharedPreferences sharedPreferences;

    private int cur_device_Mode = 0;
    long cur_time = 0;

    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectBinding = ActivityProjectBinding.inflate(LayoutInflater.from(this));
        setContentView(projectBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){
        projectBinding.rlProjectMode.setOnClickListener(this);
        projectBinding.rlAutoKeystone.setOnClickListener(this);
        projectBinding.rlInitAngle.setOnClickListener(this);
        projectBinding.autoKeystoneSwitch.setOnClickListener(this);

        projectBinding.rlProjectMode.setOnKeyListener(this);
        projectBinding.rlDeviceMode2.setOnKeyListener(this);
        projectBinding.rlDigitalZoom.setOnKeyListener(this);
        projectBinding.rlHorizontalCorrect.setOnKeyListener(this);
        projectBinding.rlVerticalCorrect.setOnKeyListener(this);

        projectBinding.rlManualKeystone.setOnClickListener(this);
        projectBinding.rlResetKeystone.setOnClickListener(this);

        projectBinding.rlAutoFocus.setOnClickListener(this);
        projectBinding.autoFocusSwitch.setOnClickListener(this);
        projectBinding.rlAutoFourCorner.setOnClickListener(this);
        projectBinding.autoFourCornerSwitch.setOnClickListener(this);
        projectBinding.rlScreenRecognition.setOnClickListener(this);
        projectBinding.screenRecognitionSwitch.setOnClickListener(this);
        projectBinding.rlIntelligentObstacle.setOnClickListener(this);
        projectBinding.intelligentObstacleSwitch.setOnClickListener(this);
        projectBinding.rlCalibration.setOnClickListener(this);

        projectBinding.rlDisplaySettings.setVisibility(MyApplication.config.displaySetting?View.VISIBLE:View.GONE);
        projectBinding.rlProjectMode.setVisibility(MyApplication.config.projectMode?View.VISIBLE:View.GONE);
        projectBinding.rlDeviceMode2.setVisibility(MyApplication.config.deviceMode?View.VISIBLE:View.GONE);
        projectBinding.rlDigitalZoom.setVisibility(MyApplication.config.wholeZoom?View.VISIBLE:View.GONE);
        projectBinding.rlAutoKeystone.setVisibility(MyApplication.config.autoKeystone?View.VISIBLE:View.GONE);
        projectBinding.rlInitAngle.setVisibility(MyApplication.config.initAngleCorrect?View.VISIBLE:View.GONE);
        projectBinding.rlManualKeystone.setVisibility(MyApplication.config.manualKeystone?View.VISIBLE:View.GONE);
        projectBinding.rlResetKeystone.setVisibility(MyApplication.config.resetKeystone?View.VISIBLE:View.GONE);
        projectBinding.rlAutoFocus.setVisibility(MyApplication.config.autoFocus?View.VISIBLE:View.GONE);
        projectBinding.rlAutoFourCorner.setVisibility(MyApplication.config.autoFourCorner?View.VISIBLE:View.GONE);
        projectBinding.rlScreenRecognition.setVisibility(MyApplication.config.screenRecognition?View.VISIBLE:View.GONE);
        projectBinding.rlIntelligentObstacle.setVisibility(MyApplication.config.intelligentObstacle?View.VISIBLE:View.GONE);

        if (SystemProperties.get("persist.sys.camok", "0").equals("1")) {
            projectBinding.rlAutoFocus.setVisibility(View.VISIBLE);
            projectBinding.rlIntelligentObstacle.setVisibility(View.VISIBLE);
            projectBinding.rlScreenRecognition.setVisibility(View.VISIBLE);
            projectBinding.rlAutoFourCorner.setVisibility(View.VISIBLE);
            projectBinding.rlInitAngle.setVisibility(View.GONE);
            projectBinding.rlAutoKeystone.setVisibility(View.GONE);

            if (SystemProperties.get("persist.sys.focusupdn", "0").equals("1")) {
                //自动梯形
                projectBinding.rlAutoKeystone.setVisibility(View.VISIBLE);
                projectBinding.rlInitAngle.setVisibility(MyApplication.config.initAngleCorrect?View.VISIBLE:View.GONE);
                projectBinding.rlAutoFourCorner.setVisibility(View.GONE);
                projectBinding.rlIntelligentObstacle.setVisibility(View.GONE);
                projectBinding.rlScreenRecognition.setVisibility(View.GONE);
            }

        } else {
            projectBinding.rlAutoKeystone.setVisibility(View.VISIBLE);
            projectBinding.rlInitAngle.setVisibility(MyApplication.config.initAngleCorrect?View.VISIBLE:View.GONE);
            projectBinding.rlAutoFourCorner.setVisibility(View.GONE);
            projectBinding.rlIntelligentObstacle.setVisibility(View.GONE);
            projectBinding.rlScreenRecognition.setVisibility(View.GONE);
            projectBinding.rlAutoFocus.setVisibility(View.GONE);

        }

        if ((boolean)ShareUtil.get(this,Contants.KEY_DEVELOPER_MODE,false)
                && projectBinding.rlAutoFourCorner.getVisibility()==View.VISIBLE){
            projectBinding.rlCalibration.setVisibility(View.VISIBLE);
        }

        projectBinding.rlProjectMode.requestFocus();
        projectBinding.rlProjectMode.requestFocusFromTouch();
    }

    private void initData(){
        tvDisplayManager = AwTvDisplayManager.getInstance();
        project_name.add(getString(R.string.project_mode_1));
        project_name.add(getString(R.string.project_mode_2));
        project_name.add(getString(R.string.project_mode_3));
        project_name.add(getString(R.string.project_mode_4));
        cur_project_mode = tvDisplayManager.factoryGetPanelValue(AwTvDisplayTypes.EnumPanelConfigType.E_AW_PANEL_CONFIG_MIRROR);
        projectBinding.projectModeTv.setText(project_name.get(cur_project_mode));
        singer = Executors.newSingleThreadExecutor();
        sharedPreferences = ShareUtil.getInstans(this);
        String zoom_mode = sharedPreferences.getString("zoom_mode","16:9");
        assert zoom_mode != null;
        switch (zoom_mode) {
            case "16:9":

                scale = 1D;
                step_x = 16;
                step_y = 9;
                break;
            case "4:3":

                scale = 0.875D;
                step_x = 12;
                step_y = 9;
                break;
            case "16:10":

                scale = 0.95D;
                step_x = 16;
                step_y = 10;
                break;
        }

        All = KeystoneUtils.readGlobalSettings(this,KeystoneUtils.ZOOM_VALUE,0);
        updateZoomView();
        initAuto();
        initBstacle();
        initMbRecognize();
        initAutoFourCorner();
        projectBinding.autoFocusSwitch.setChecked(get_auto_focus());
    }

    private void  initAuto(){
        boolean auto = getAuto();
        if (!auto){
            projectBinding.autoKeystoneSwitch.setChecked(false);
        }else {
            projectBinding.autoKeystoneSwitch.setChecked(true);
        }
    }

    private void setAuto() {
        //int auto = PrjScreen.get_prj_auto_keystone_enable();
        //PrjScreen.set_prj_auto_keystone_enable(auto == 0 ? 1 : 0);
        boolean auto = getAuto();
        SystemProperties.set("persist.sys.tpryauto", String.valueOf(auto ? 0 : 1));
        //自动梯形打开的时候发送一次更新
        if (!auto){
            sendKeystoneBroadcast();
        }else {
            updateZoomValue();
        }
        initAuto();
    }

    private void updateZoomValue(){
        String value = SystemProperties.get("persist.sys.zoom.value","0,0,0,0,0,0,0,0");
        String[] va = value.split(",");
        if (va.length==8){
            KeystoneUtils.lb_X = Integer.parseInt(va[0]);
            KeystoneUtils.lb_Y = Integer.parseInt(va[1]);
            KeystoneUtils.lt_X = Integer.parseInt(va[2]);
            KeystoneUtils.lt_Y = Integer.parseInt(va[3]);
            KeystoneUtils.rt_X = Integer.parseInt(va[4]);
            KeystoneUtils.rt_Y = Integer.parseInt(va[5]);
            KeystoneUtils.rb_X = Integer.parseInt(va[6]);
            KeystoneUtils.rb_Y = Integer.parseInt(va[7]);
            KeystoneUtils.UpdateKeystoneZOOM(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_power_mode:
                if (cur_project_mode==project_name.size()-1)
                    cur_project_mode=0;
                else
                    cur_project_mode++;

                updateProjectMode();
                break;
            case R.id.rl_manual_keystone:
                if (getAuto() && projectBinding.rlAutoKeystone.getVisibility()==View.VISIBLE) {
                    ToastUtil.showShortToast(this, getString(R.string.auto_keystone_on));
                    break;
                }else if (get_AutoFourCorner() && projectBinding.rlAutoFourCorner.getVisibility()==View.VISIBLE){
                    ToastUtil.showShortToast(this, getString(R.string.auto_four_corner_on));
                    break;
                }
                    startNewActivity(CorrectionActivity.class);
                break;
            case R.id.rl_reset_keystone:
                if (getAuto() && projectBinding.rlAutoKeystone.getVisibility()==View.VISIBLE) {
                    ToastUtil.showShortToast(this, getString(R.string.auto_keystone_on));
                    break;
                }else if (get_AutoFourCorner() && projectBinding.rlAutoFourCorner.getVisibility()==View.VISIBLE){
                    ToastUtil.showShortToast(this, getString(R.string.auto_four_corner_on));
                    break;
                }
                ShowResetKeystoreDialog();
                break;
            case R.id.rl_auto_keystone:
            case R.id.auto_keystone_switch:
                setAuto();
                break;
            case R.id.rl_auto_focus:
            case R.id.auto_focus_switch:
                set_auto_focus(!get_auto_focus());
                projectBinding.autoFocusSwitch.setChecked(get_auto_focus());
                break;
            case R.id.rl_auto_four_corner:
            case R.id.auto_four_corner_switch:
                setAutoFourCorner();
                break;
            case R.id.rl_screen_recognition:
            case R.id.screen_recognition_switch:
                setMbRecognize();
                break;
            case R.id.rl_intelligent_obstacle:
            case R.id.intelligent_obstacle_switch:
                setBstacle();
                break;
            case R.id.rl_calibration:
                AppUtils.startNewApp(this,"com.hysd.vafocus","com.hysd.vafocus.VajzActivity");
                break;
            case R.id.rl_init_angle:
                AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                builder.setTitle(getString(R.string.hint));
                builder.setMessage(getString(R.string.defaultcorrectionhint));
                builder.setPositiveButton(getString(R.string.enter), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        initCorrectAngle();
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), null);
                builder.show();
                break;
        }
    }

    private ProgressDialog dialog = null;
    private void initCorrectAngle() {
        ReflectUtil.invokeSet_angle_offset();
        dialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        dialog.setMessage(getString(R.string.defaultcorrectionin));
        dialog.show();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog!=null && dialog.isShowing())
                    dialog.dismiss();
                LogUtils.d("get_angle_offset "+ReflectUtil.invokeGet_angle_offset());
            }
        },3000);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if ((event.getKeyCode()==KeyEvent.KEYCODE_DPAD_LEFT ||event.getKeyCode()==KeyEvent.KEYCODE_DPAD_RIGHT)
                && (System.currentTimeMillis()-cur_time<150)){
            return true;
        }

        if (keyCode==KeyEvent.KEYCODE_DPAD_LEFT){
            switch (v.getId()){
                case R.id.rl_project_mode:
                    Log.d(TAG,"向左切换安装模式");
                    if (event.getAction()!=KeyEvent.ACTION_UP)
                        break;

                    if (cur_project_mode==0)
                        cur_project_mode=project_name.size()-1;
                    else
                        cur_project_mode--;

                    updateProjectMode();
                    break;
                case R.id.rl_digital_zoom:
                    if (event.getAction()!=KeyEvent.ACTION_DOWN)
                        break;

                    if (All<=0)
                        break;

                    All--;
                    set_screen_zoom(All,All,All,All);
                    updateZoomView();
                    break;
                case R.id.rl_horizontal_correct:
                    break;
                case R.id.rl_vertical_correct:
                    break;
                case R.id.rl_device_mode2:
                    if (event.getAction()!=KeyEvent.ACTION_UP)
                        break;
                    Log.d(TAG,"向左切换设备模式");
                    cur_device_Mode--;
                    if(cur_device_Mode<0) {
                        cur_device_Mode= 2;
                    }
                    updateText(cur_device_Mode);
                    ReflectUtil.invokeSet_brightness_level(cur_device_Mode);
                    break;
            }
        }else if (keyCode==KeyEvent.KEYCODE_DPAD_RIGHT ){
            switch (v.getId()){
                case R.id.rl_project_mode:
                    Log.d(TAG,"向右切换安装模式");
                    if (event.getAction()!=KeyEvent.ACTION_UP)
                        break;
                    if (cur_project_mode==project_name.size()-1)
                        cur_project_mode=0;
                    else
                        cur_project_mode++;

                    updateProjectMode();
                    break;
                case R.id.rl_digital_zoom:
                    if (event.getAction()!=KeyEvent.ACTION_DOWN)
                        break;

                    if (All>=ZOOM_MAX)
                        break;

                    All++;
                    set_screen_zoom(All,All,All,All);
                    updateZoomView();
                    break;
                case R.id.rl_horizontal_correct:
                    break;
                case R.id.rl_vertical_correct:
                    break;
                case R.id.rl_device_mode2:
                    if (event.getAction()!=KeyEvent.ACTION_UP)
                        break;
                    Log.d(TAG,"向右切换设备模式");
                    cur_device_Mode++;
                    if(cur_device_Mode>2) {
                        cur_device_Mode= 0;
                    }
                    updateText(cur_device_Mode);
                    ReflectUtil.invokeSet_brightness_level(cur_device_Mode);
                    break;
            }
        }

        return false;
    }

    private void updateText(int mode){
        switch (mode) {
            case 0:
                projectBinding.deviceModeTv.setText(getString(R.string.device_mode0));
                break;
            case 1:
                projectBinding.deviceModeTv.setText(getString(R.string.device_mode1));
                break;
            case 2:
                projectBinding.deviceModeTv.setText(getString(R.string.device_mode2));
                break;
        }

    }

    private void updateProjectMode(){
        tvDisplayManager.factorySetPanelValue(AwTvDisplayTypes.EnumPanelConfigType.E_AW_PANEL_CONFIG_MIRROR,cur_project_mode);
        projectBinding.projectModeTv.setText(project_name.get(cur_project_mode));
        if (getAuto())
            sendProjectBroadCast();
    }

    private void sendProjectBroadCast(){
        Intent intent = new Intent("android.intent.projective_mode");
        intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }

    public  void get_screen_zoom() {
        String zoomV = SystemProperties.get("persist.vendor.overscan.main", "overscan 100,100,100,100");
        LogUtils.i("PrjScreen", "get_screen_zoom zoomV=" + zoomV);
        if (!zoomV.equals("")) {
            String[] arraysZoom = zoomV.substring(9).split(",");
            if (arraysZoom.length == 4){
                left = (int) Double.parseDouble(arraysZoom[0]);
                top =(int) Double.parseDouble(arraysZoom[1]);
                right =(int) Double.parseDouble(arraysZoom[2]);
                bottom =(int) Double.parseDouble(arraysZoom[3]);
            }
        }
    }

    private void updateZoomView(){
        projectBinding.digitalZoomTv.setText(String.valueOf(All));
        if (All<=0)
            projectBinding.digitalZoomLeft.setVisibility(View.GONE);
        else if (All>=ZOOM_MAX)
            projectBinding.digitalZoomRight.setVisibility(View.GONE);
        else{
            projectBinding.digitalZoomRight.setVisibility(View.VISIBLE);
            projectBinding.digitalZoomLeft.setVisibility(View.VISIBLE);
        }

    }


    public  void set_screen_zoom(int l,int t,int r,int b){
        KeystoneUtils.writeGlobalSettings(this,KeystoneUtils.ZOOM_VALUE,l);
        l = max_value - l;
        t = max_value - t;
        r = max_value - r;
        b = max_value - b;
        changeform(l,t,r,b);
    }

    public  void changeform(int l,int t,int right,int bottom){
        KeystoneUtils.lt_X = Integer.parseInt(df.format(((100-100*scale)*zoom_step_x + (100-l)*step_x)*1000/KeystoneUtils.lcd_w ));
        KeystoneUtils.lt_Y =1000- Integer.parseInt(df.format((KeystoneUtils.lcd_h -(100-t)*step_y)*1000/KeystoneUtils.lcd_h));

        KeystoneUtils.lb_X = Integer.parseInt(df.format(((100-100*scale)*zoom_step_x + (100-l)*step_x)*1000/KeystoneUtils.lcd_w));
        KeystoneUtils.lb_Y = Integer.parseInt(df.format(((100-bottom)*step_y)*1000/KeystoneUtils.lcd_h));

        KeystoneUtils.rt_X =1000- Integer.parseInt(df.format((KeystoneUtils.lcd_w *scale - (100-right)*step_x)*1000/KeystoneUtils.lcd_w));
        KeystoneUtils.rt_Y =1000- Integer.parseInt(df.format((KeystoneUtils.lcd_h -(100-t)*step_y)*1000/KeystoneUtils.lcd_h));

        KeystoneUtils.rb_X =1000- Integer.parseInt(df.format((KeystoneUtils.lcd_w *scale - (100-right)*step_x)*1000/KeystoneUtils.lcd_w));
        KeystoneUtils.rb_Y = Integer.parseInt(df.format(((100-bottom)*step_y)*1000/KeystoneUtils.lcd_h));

        if (getAuto()){
            KeystoneUtils.UpdateKeystoneZOOM(false);
            sendKeystoneBroadcast();
        }else {
            KeystoneUtils.UpdateKeystoneZOOM(true);
        }
    }

    private void sendKeystoneBroadcast(){
        Intent intent = new Intent("android.intent.hotack_keystone");
        intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        sendBroadcast(intent);
    }

    public  boolean getAuto(){
        return SystemProperties.getBoolean("persist.sys.tpryauto",false);
    }

    public  boolean get_auto_focus(){
        return SystemProperties.getBoolean("persist.sys.vafocus",false);
    }
    public  void set_auto_focus(boolean b) {
        if (b) {
            SystemProperties.set("persist.sys.vafocus", "1");
        } else {
            SystemProperties.set("persist.sys.vafocus", "0");
        }
    }

    private void initAutoFourCorner(){
        boolean auto = get_AutoFourCorner();
        if (!auto) {
            // 手动
            projectBinding.autoFourCornerSwitch.setChecked(false);
        } else {
            // 自动
            projectBinding.autoFourCornerSwitch.setChecked(true);
        }
    }


    private void setAutoFourCorner() {
        boolean auto = get_AutoFourCorner();
        set_AutoFourCorner(!auto);
        initAutoFourCorner();
    }

    public  void set_AutoFourCorner(boolean b) {
        if (b) {
            SystemProperties.set("persist.sys.tpryxcrt", "1");
        } else {
            SystemProperties.set("persist.sys.tpryxcrt", "0");
        }
    }

    public  boolean get_AutoFourCorner(){
        return SystemProperties.getBoolean("persist.sys.tpryxcrt",false);
    }

    private void initMbRecognize(){
        boolean auto = get_MbRecognize();
        projectBinding.screenRecognitionSwitch.setChecked(auto);
    }

    //智能避障状态更新
    private void initBstacle(){
        boolean auto = get_Bstacle();
        projectBinding.intelligentObstacleSwitch.setChecked(auto);
    }

    private void setMbRecognize() {
        boolean auto = get_MbRecognize();
        set_MbRecognize(!auto);
        initMbRecognize();
    }

    private void setBstacle() {
        boolean auto = get_Bstacle();
        set_Bstacle(!auto);
        initBstacle();
    }

    public  void set_MbRecognize(boolean b) {
        if (b) {
            SystemProperties.set("persist.sys.mbrecognize", "1");
        } else {

            SystemProperties.set("persist.sys.mbrecognize", "0");
        }
    }
    public  void set_Bstacle(boolean b) {
        if (b) {
            SystemProperties.set("persist.sys.obstacle", "1");
        } else {

            SystemProperties.set("persist.sys.obstacle", "0");
        }
    }

    public  boolean get_MbRecognize(){
        return SystemProperties.getBoolean("persist.sys.mbrecognize",false);
    }

    public  boolean get_Bstacle(){
        return SystemProperties.getBoolean("persist.sys.obstacle",false);
    }

    private void ShowResetKeystoreDialog(){
        ResetKeystoreLayoutBinding resetKeystoreLayoutBinding = ResetKeystoreLayoutBinding.inflate(LayoutInflater.from(this));
        Dialog dialoge = new Dialog(this,R.style.DialogTheme);
        dialoge.setContentView(resetKeystoreLayoutBinding.getRoot());
        /*builder.setMessage(getString(R.string.reset_keystore));
        builder.setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                KeystoneUtils.resetKeystone();
                All =0;
                updateZoomView();
                dialog.dismiss();
            }
        });*/
        Window window = dialoge.getWindow();
        if (window!=null){
            window.setWindowAnimations(R.style.right_in_right_out_anim);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            //设置dialog在界面中的属性
            window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            //背景全透明
            window.setDimAmount(0f);
        }
        Display d = getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams params = window.getAttributes(); // 获取对话框当前的参数值
        params.width = (int) (d.getWidth() * 0.4); // 宽度设置为屏幕的0.8，根据实际情况调整
        params.height = (int) (d.getHeight() * 0.4);
        //params.x = parent.getWidth();
        window.setGravity(Gravity.CENTER);// 设置对话框位置
        window.setAttributes(params);window.setAttributes(params);
        resetKeystoreLayoutBinding.enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeystoneUtils.resetKeystone();
                All =0;
                updateZoomView();
                dialoge.dismiss();
            }
        });
        resetKeystoreLayoutBinding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoge.dismiss();
            }
        });
        dialoge.show();
    }

}