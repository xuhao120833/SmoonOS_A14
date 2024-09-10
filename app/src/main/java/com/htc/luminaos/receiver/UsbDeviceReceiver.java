package com.htc.luminaos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
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
                Utils.hasUsbDevice = true;
                callBack.UsbDeviceChange();
                Utils.usbDevicesNumber++;
                Log.d(TAG, " 有USB设备插入挂载成功 " + Utils.usbDevicesNumber);
            } else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(intent.getAction())) { //U盘拔出卸载完毕
                if (Utils.usbDevicesNumber > 1) {
                    Utils.usbDevicesNumber--;
                    Utils.hasUsbDevice = true;
                } else if (Utils.usbDevicesNumber == 1) {
                    Utils.usbDevicesNumber = 0;
                    Utils.hasUsbDevice = false;
                }
                if (!Utils.hasUsbDevice) {
                    Log.d(TAG, "USB设备已经全部拔出");
                    callBack.UsbDeviceChange();
                }
                Log.d(TAG, "有USB设备拔出卸载 " + Utils.usbDevicesNumber);

                //方案3 插入两个U盘，拔出一个就会走没有U盘的判断，不准确
//                // 获取 UsbManager 实例
//                UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
//                // 获取所有连接的 USB 设备
//                HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
//
//                // 遍历当前所有连接的 USB 设备
//                for (UsbDevice device : deviceList.values()) {
//                    // 检查设备的类别是否为 U 盘（存储类设备）
//                    Log.d(TAG, "device.getDeviceClass(): " + device.getDeviceClass());
//                    if (device.getDeviceClass() == UsbConstants.USB_CLASS_MASS_STORAGE) {
//                        Log.d(TAG, "当前连接的U盘设备: " + device.getDeviceName());
//                        Utils.hasUsbDevice = true;
//                        break; // 如果发现有 U 盘设备，直接退出遍历
//                    }
//                }
//
//                // 判断是否还有 U 盘连接
//                if (!Utils.hasUsbDevice) {
//                    Log.d(TAG, "所有U盘设备已经拔出");
//                    callBack.UsbDeviceChange();
//                }

//方案1 拔出所有U盘了，entry != null 还成立，不准确
//                deviceHashMap = ((UsbManager) context.getSystemService(Context.USB_SERVICE)).getDeviceList();

//                for (Map.Entry entry : deviceHashMap.entrySet()) { //遍历所有设备
//                    if (entry != null) {
//                        Log.d(TAG,"还有USB设备" +entry.getKey()+entry.getValue());
//                        Utils.hasUsbDevice = true;
//                    }
//                }


//方案2 不准确，拔出U盘，还会识别成有U盘
//                Thread.sleep(1000);
//
//                File usbRoot = new File(USB_ROOT);//检测mnt/media_rw目录下是否还有其它U盘
//                File[] pfiles = usbRoot.listFiles();
//                if (pfiles != null&&pfiles.length>0) {
//                    Log.d(TAG, "还有USB设备"+pfiles.length);
//                    Utils.hasUsbDevice = true;//有就继续保持USB状态栏的显示
//                    for (File tmp : pfiles) {
//                        Log.d(TAG, "还有USB设备zzz"+tmp.getAbsolutePath());
//                    }
//                }
//
//                if (!Utils.hasUsbDevice) {
//                    Log.d(TAG, "USB设备已经全部拔出");
//                    callBack.UsbDeviceChange();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
