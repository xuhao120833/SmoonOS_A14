package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;
import android.view.View;

import com.htc.luminaos.utils.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class UsbDeviceReceiver extends BroadcastReceiver {

    HashMap<String, UsbDevice> deviceHashMap;

    UsbDeviceCallBack callBack;

    private static String TAG = "UsbDeviceReceiver";

    private static String USB_ROOT = "/mnt/media_rw";

    public UsbDeviceReceiver(UsbDeviceCallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d(TAG, "UsbDevice收到广播");

            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(intent.getAction())) { // U盘插入且挂载完毕

                Log.d(TAG, " 有USB设备插入挂载成功");

                Utils.hasUsbDevice = true;
                callBack.UsbDeviceChange();

            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) { //U盘拔出卸载完毕

                Log.d(TAG, " 有USB设备拔出卸载");

                Utils.hasUsbDevice = false;
//                deviceHashMap = ((UsbManager) context.getSystemService(Context.USB_SERVICE)).getDeviceList();
//
//                for (Map.Entry entry : deviceHashMap.entrySet()) { //遍历所有设备
//                    if (entry != null) {
//                        Log.d(TAG,"还有USB设备" +entry.getKey()+entry.getValue());
//                        Utils.hasUsbDevice = true;
//                    }
//                }

                Thread.sleep(1000);

                File usbRoot = new File(USB_ROOT);//检测mnt/media_rw目录下是否还有其它U盘
                File[] pfiles = usbRoot.listFiles();
                if (pfiles != null&&pfiles.length>0) {
                    Log.d(TAG, "还有USB设备"+pfiles.length);
                    Utils.hasUsbDevice = true;//有就继续保持USB状态栏的显示
                    for (File tmp : pfiles) {
                        Log.d(TAG, "还有USB设备zzz"+tmp.getAbsolutePath());
                    }
                }

                if (!Utils.hasUsbDevice) {
                    Log.d(TAG, "USB设备已经全部拔出");
                    callBack.UsbDeviceChange();
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
