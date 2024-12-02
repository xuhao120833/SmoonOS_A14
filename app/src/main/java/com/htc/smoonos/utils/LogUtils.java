package com.htc.smoonos.utils;

import android.util.Log;

/**
 * Author:
 * Date:
 * Description:
 */
public class LogUtils {
    private static String TAG = "LogUtils";
    private static boolean debug =true;

    public static void d(String msg){
        if (!debug)
            return;
        Log.d(TAG,msg);
    }

    public static void e(String msg){
        if (!debug)
            return;
        Log.e(TAG,msg);
    }

    public static void i(String msg){
        if (!debug)
            return;
        Log.i(TAG,msg);
    }

    public static void d(String TAG,String msg){
        if (!debug)
            return;
        Log.d(TAG,msg);
    }

    public static void e(String TAG,String msg){
        if (!debug)
            return;
        Log.e(TAG,msg);
    }

    public static void i(String TAG,String msg){
        if (!debug)
            return;
        Log.i(TAG,msg);
    }

}
