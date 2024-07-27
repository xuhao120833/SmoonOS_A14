package com.htc.launcher;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.security.keystore.AndroidKeyStoreKeyPairGeneratorSpi;
import android.util.DisplayMetrics;
import android.util.Log;

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
        editor.putBoolean(Contants.TimeOffStatus,false);
        editor.putInt(Contants.TimeOffIndex,0);
        editor.apply();
        if (new File(Contants.WALLPAPER_MAIN).exists())
            mainDrawable =new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN));
        if (new File(Contants.WALLPAPER_OTHER).exists())
            otherDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_OTHER));

        parseConfigFile();
        initDisplaySize();
    }


    private void parseConfigFile(){
        String configContent;
        if (new File("/oem/shortcuts.config").exists()){
            configContent = FileUtils.readFileContent("/oem/shortcuts.config");
        }else {
            configContent = FileUtils.readFileContent("/system/shortcuts.config");
        }
        if (configContent==null || configContent.equals(""))
            return;

        Log.d(TAG," 配置文件configContent " + configContent);
        try{
            Gson gson = new Gson();
            config = gson.fromJson(configContent, Config.class);

            Log.d(TAG," 配置文件apps " + config.apps.get(0).resident);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void initDisplaySize(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.d("hzj","screenWidth "+screenWidth+" screenHeight "+screenHeight);
        KeystoneUtils.lcd_h = screenHeight;
        KeystoneUtils.lcd_w = screenWidth;
        KeystoneUtils.minH_size = config.manualKeystoneWidth;
        KeystoneUtils.minV_size = config.manualKeystoneHeight;
    }

}
