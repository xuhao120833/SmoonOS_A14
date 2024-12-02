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

    public static final int REQUEST_CODE_PICK_IMAGE = 1;

}
