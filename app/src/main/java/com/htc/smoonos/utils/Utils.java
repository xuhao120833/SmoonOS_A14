package com.htc.smoonos.utils;

import android.graphics.drawable.Drawable;

import com.htc.smoonos.R;

import java.util.ArrayList;

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

}
