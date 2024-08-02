package com.htc.launcher;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.security.keystore.AndroidKeyStoreKeyPairGeneratorSpi;
import android.telecom.ParcelableCallAnalytics;
import android.util.DisplayMetrics;
import android.util.Log;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.htc.launcher.entry.Config;
import com.htc.launcher.utils.Contants;
import com.htc.launcher.utils.FileUtils;
import com.htc.launcher.utils.KeystoneUtils;
import com.htc.launcher.utils.ShareUtil;

import java.io.File;

/**
 * Author:
 * Date:
 * Description:
 */
public class MyApplication extends Application {

    private static String TAG = "MyApplication";

    public static Config config = new Config();
    public static BitmapDrawable mainDrawable = null;
    public static BitmapDrawable otherDrawable = null;


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Contants.TimeOffStatus, false);
        editor.putInt(Contants.TimeOffIndex, 0);
        editor.apply();
        if (new File(Contants.WALLPAPER_MAIN).exists())
            mainDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN));
        if (new File(Contants.WALLPAPER_OTHER).exists())
            otherDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_OTHER));

        parseConfigFile();

        initDisplaySize();


        StatService.init(this,"5dd227fad8","Baidu Market");
        //启动百度自动埋点服务 https://mtj.baidu.com/static/userguide/book/android/adconfig/circle/circle.html
        StatService.setAuthorizedState(this, true);
//        StatService.autoTrace(this);
        StatService.autoTrace(this, true, false);



        //需要对webview监控的话，换如下方法
        // 自动埋点，建议在Application中调用。否则可能造成部分页面遗漏，无法完整统计。
        // @param autoTrace：如果设置为true，打开自动埋点；反之关闭
        // @param autoTrackWebview：
        // 如果设置为true，则自动track所有webview，如果有对webview绑定WebChromeClient，
        // 为避免影响APP本身回调，请调用trackWebView接口；
        // 如果设置为false，则不自动track webview，如需对特定webview进行统计，需要对特定
        // webview调用trackWebView()即可。
        // StatService.autoTrace(Context context, boolean autoTrace, boolean autoTrackWebview)
    }


    private void parseConfigFile() {
        String configContent;
        if (new File("/oem/shortcuts.config").exists()) {
            configContent = FileUtils.readFileContent("/oem/shortcuts.config"); //这里的作用就是从shortcuts.config中一行一行的读取字符，然后将它们合并成一行字符串
        } else {
            configContent = FileUtils.readFileContent("/system/shortcuts.config");
        }
        if (configContent == null || configContent.equals(""))
            return;

        Log.d(TAG, " 配置文件configContent " + configContent);
        try {
            Gson gson = new Gson();
            config = gson.fromJson(configContent, Config.class); //gson解析

            Log.d(TAG, " 配置文件apps " + config.apps.get(0).resident);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initDisplaySize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.d("hzj", "screenWidth " + screenWidth + " screenHeight " + screenHeight);
        KeystoneUtils.lcd_h = screenHeight;
        KeystoneUtils.lcd_w = screenWidth;
        KeystoneUtils.minH_size = config.manualKeystoneWidth;
        KeystoneUtils.minV_size = config.manualKeystoneHeight;
    }

}
