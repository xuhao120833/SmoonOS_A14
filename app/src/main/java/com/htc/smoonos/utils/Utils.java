package com.htc.smoonos.utils;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.htc.smoonos.R;

import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    public static boolean hasfocus = false;

    public static boolean hasUsbDevice = false;

    //首页默认背景resId,无配置默认-1
    public static int mainBgResId = -1;

    public static int usbDevicesNumber = 0;

    //默认背景使用的ArrayList
    public static ArrayList<Drawable> drawables = new ArrayList<>();

    public static int[] drawablesId = {
            R.drawable.background8,
            R.drawable.background_main,
            R.drawable.muqi_background1,
            R.drawable.muqi_background2,
            R.drawable.muqi_background3,
            R.drawable.muqi_background4,
            R.drawable.muqi_background5,
            R.drawable.muqi_background6,
            R.drawable.muqi_background7,
            R.drawable.muqi_background8,
            R.drawable.muqi_background9,
            R.drawable.muqi_background10,
            R.drawable.muqi_background11,
            R.drawable.muqi_background12,
            R.drawable.muqi_background13,
            R.drawable.muqi_background14,
            R.drawable.muqi_background15,
            R.drawable.muqi_background16,
            R.drawable.muqi_background17,
            R.drawable.muqi_background18,
            R.drawable.muqi_background19
    };

    public static final int REQUEST_CODE_PICK_IMAGE = 1;

    //全局时区列表
    public static ArrayList<HashMap> list = null;

    /**
     * 打印 Intent 的 Extras 信息
     *
     * @param intent 需要打印的 Intent
     * @param tag    用于日志的 TAG
     */
    public static void logIntentExtras(Intent intent, String tag) {
        if (intent == null) {
            Log.d(tag, "logIntentExtras Intent is null");
            return;
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Log.d(tag, "logIntentExtras Intent extras:");
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(tag, "[" + key + "] = " + value);
            }
        } else {
            Log.d(tag, "logIntentExtras No extras in the Intent");
        }
    }

}
