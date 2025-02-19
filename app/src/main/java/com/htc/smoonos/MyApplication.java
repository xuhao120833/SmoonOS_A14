package com.htc.smoonos;

import static com.htc.smoonos.utils.BlurImageView.MAX_BITMAP_SIZE;
import static com.htc.smoonos.utils.BlurImageView.narrowBitmap;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.baidu.mobstat.StatService;
import com.google.gson.Gson;
import com.htc.smoonos.entry.Config;
import com.htc.smoonos.utils.Contants;
import com.htc.smoonos.utils.FileUtils;
import com.htc.smoonos.utils.KeystoneUtils;
import com.htc.smoonos.utils.ShareUtil;
import com.htc.smoonos.utils.Utils;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private MutableLiveData<Boolean> isDataInitialized = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getIsDataInitialized() {
        return isDataInitialized; // 只暴露不可变的 LiveData
    }


    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Contants.TimeOffStatus, false);
        editor.putInt(Contants.TimeOffIndex, 0);
        editor.apply();
        if (new File(Contants.WALLPAPER_MAIN).exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN);
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            //判断图片大小，如果超过限制就做缩小处理
            if (width * height * 6 >= MAX_BITMAP_SIZE) {
                bitmap = narrowBitmap(bitmap);
            }
            mainDrawable = new BitmapDrawable(bitmap);
//            mainDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN));
        }
        try {
            // JSON 解析
            parseConfigFile();
            initDisplaySize();
            initWallpaperData();
        } catch (Exception e) {
            // 打印异常日志
            e.printStackTrace();
        }
        StatService.init(this, "5dd227fad8", "Baidu Market");
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
        //优先读取oem分区，其次读取system分区
        if (new File("/oem/config.ini").exists()) {
            configContent = FileUtils.readFileContent("/oem/config.ini"); //这里的作用就是从shortcuts.config中一行一行的读取字符，然后将它们合并成一行字符串
        } else if (new File("/product/etc/config.ini").exists()) {
            configContent = FileUtils.readFileContent("/product/etc/config.ini");
        } else {
            configContent = FileUtils.readFileContent("/system/config.ini");
        }
        if (configContent == null || configContent.equals(""))
            return;
        Log.d(TAG, " 配置文件configContent " + configContent);
        try {
            Gson gson = new Gson();
            config = gson.fromJson(configContent, Config.class); //gson解析

//            Log.d(TAG, " 配置文件apps " + config.apps.get(0).resident);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Utils.sourceList = config.sourceList.split(",");
        Utils.sourceListTitle = config.sourceListTitle.split(",");
        Log.d(TAG,"Utils.sourceList  sourceListTitle "+Utils.sourceList.length+" "+Utils.sourceListTitle.length);
        //读取背景的默认图片
        SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int defaultBg = sharedPreferences.getInt("defaultBg", 0);
        if (defaultBg == 0) {
            readBackground();
            editor.putInt("defaultBg", 1);
            editor.apply();
        }
    }

    private void initDisplaySize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.d(TAG, "screenWidth " + screenWidth + " screenHeight " + screenHeight);
        KeystoneUtils.lcd_h = screenHeight;
        KeystoneUtils.lcd_w = screenWidth;
        KeystoneUtils.minH_size = config.manualKeystoneWidth;
        KeystoneUtils.minV_size = config.manualKeystoneHeight;
    }

    private void readBackground() {
        File file = new File("/oem/shortcuts.config");
        if (!file.exists()) {
            file = new File("/system/shortcuts.config");
        }
        if (!file.exists()) {
            Log.d(TAG, " readBackground shortcuts.config文件不存在 ");
            return;
        }
        try {
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            String result = new String(b);
            List<String> residentList = new ArrayList<>();
            JSONObject obj = new JSONObject(result);
            readDefaultBackground(obj);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void readDefaultBackground(JSONObject obj) {
        try {
            SharedPreferences sharedPreferences = ShareUtil.getInstans(getApplicationContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (obj.has("defaultbackground")) { //如果配置字段为空或者没有配置默认背景，则默认使用第一张图片作为背景。
                String DefaultBackground = obj.getString("defaultbackground").trim();
                Log.d(TAG, " readDefaultBackground " + DefaultBackground);
                // 将字符串存入数据库；
                editor.putString(Contants.DefaultBg, DefaultBackground);
                editor.apply();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWallpaperData() {
        Log.d(TAG,"initWallpaperData config.custombackground "+config.custombackground);
        if (!config.custombackground.isEmpty() && copyCustomBg()) {
//            copyCustomBg();
            Utils.customBackground = true;
            copyMyWallpaper();
            Utils.drawables.add(getResources().getDrawable(R.drawable.wallpaper_add));
//            isDataInitialized.postValue(true);//UI线程用setValue
        } else {
            Utils.drawables.add(getResources().getDrawable(R.drawable.background8));
            Utils.drawables.add(R.drawable.background_main);
            Utils.drawables.add(R.drawable.muqi_background1);
            Utils.drawables.add(R.drawable.muqi_background2);
            Utils.drawables.add(R.drawable.muqi_background3);
            Utils.drawables.add(R.drawable.muqi_background4);
            Utils.drawables.add(R.drawable.muqi_background5);
            Utils.drawables.add(R.drawable.muqi_background6);
            Utils.drawables.add(R.drawable.muqi_background7);
            Utils.drawables.add(R.drawable.muqi_background8);
            Utils.drawables.add(R.drawable.muqi_background9);
            Utils.drawables.add(R.drawable.muqi_background10);
            Utils.drawables.add(R.drawable.muqi_background11);
            Utils.drawables.add(R.drawable.muqi_background12);
            Utils.drawables.add(R.drawable.muqi_background13);
            Utils.drawables.add(R.drawable.muqi_background14);
            Utils.drawables.add(R.drawable.muqi_background15);
            Utils.drawables.add(R.drawable.muqi_background16);
            Utils.drawables.add(R.drawable.muqi_background17);
            Utils.drawables.add(R.drawable.muqi_background18);
            Utils.drawables.add(R.drawable.muqi_background19);

            // 调用 copyMyWallpaper 方法
            copyMyWallpaper();
            Utils.drawables.add(getResources().getDrawable(R.drawable.wallpaper_add));
            // 数据加载完成后更新 LiveData
            Log.d(TAG, "执行完initWallpaperData");
            isDataInitialized.postValue(true);//UI线程用setValue
        }
    }

    private void copyMyWallpaper() {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".bmp", ".webp",".gif"};
        File directory = new File("/sdcard/.mywallpaper");
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        for (String extension : imageExtensions) {
                            if (file.getName().toLowerCase().endsWith(extension)) {
                                Utils.drawables.add(file.getAbsolutePath());
                                break; // 找到一个匹配后就跳出循环
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean copyCustomBg() {
        String[] imageExtensions = {".jpg", ".jpeg", ".png", ".bmp", ".webp"};
        File directory = new File(config.custombackground);
        Log.d(TAG,"copyCustomBg 文件目录 "+config.custombackground);
        if (directory.exists() && directory.isDirectory()) {
            Log.d(TAG,"copyCustomBg 目录存在 ");
            File[] files = directory.listFiles();
            if (files != null) {//排序
                // 按数字排序
                Arrays.sort(files, (f1, f2) -> {
                    // 提取文件名中的数字
                    int num1 = extractNumber(f1.getName());
                    int num2 = extractNumber(f2.getName());
                    return Integer.compare(num1, num2); // 按数值升序排序
                });
            }
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        for (String extension : imageExtensions) {
                            if (file.getName().toLowerCase().endsWith(extension)) {
                                Utils.drawables.add(file.getAbsolutePath());
                                break; // 找到一个匹配后就跳出循环
                            }
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }

    // 从文件名中提取数字的方法
    private static int extractNumber(String fileName) {
        // 去掉文件后缀
        String name = fileName.replaceAll("\\.[a-zA-Z]+$", "");
        try {
            // 尝试将文件名解析为数字
            return Integer.parseInt(name);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE; // 如果无法解析数字，将其放在排序末尾
        }
    }

}
