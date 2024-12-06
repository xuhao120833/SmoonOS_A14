package com.htc.smoonos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;
import android.view.View;

import com.htc.smoonos.R;
import com.htc.smoonos.activity.MainActivity;
import com.htc.smoonos.utils.Utils;

import java.util.HashMap;
import java.util.List;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

public class UsbDeviceReceiver extends BroadcastReceiver {

    HashMap<String, UsbDevice> deviceHashMap;

    UsbDeviceCallBack callBack;

    MainActivity activity;

    private static String TAG = "UsbDeviceReceiver";

    private static String USB_ROOT = "/mnt/media_rw";

    public UsbDeviceReceiver(UsbDeviceCallBack callBack) {
        this.callBack = callBack;
        activity = (MainActivity) callBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Log.d(TAG, "UsbDeviceReceiver UsbDevice收到广播 intent.getAction() " + intent.getAction());
            if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())) { // U盘插入且挂载完毕
                StorageVolume storageVolume = (StorageVolume) intent.getParcelableExtra(StorageVolume.EXTRA_STORAGE_VOLUME);
                String path = storageVolume.getPath();
                Log.d(TAG, "UsbDeviceReceiver storageVolume.getPath()" + path);
                if(isExternalStoragePath(path)) {
                    Utils.hasUsbDevice = true;
                    Utils.usbDevicesNumber++;
//                    if(activity.customBinding.rlUsbConnect.getVisibility() == View.GONE) {
//                        callBack.UsbDeviceChange();
//                    }
                    if(!isImageViewShowingDrawable(activity,activity.customBinding.muqiUsb, R.drawable.muqi_usb_green)) {
                        callBack.UsbDeviceChange();
                    }
                    Log.d(TAG, "UsbDeviceReceiver 有USB设备插入挂载成功 显示U盘图标" + Utils.usbDevicesNumber);
                }
            } else if (Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())
                    || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(intent.getAction())) { //U盘拔出卸载完毕
                if (Utils.usbDevicesNumber > 1) {
                    Utils.usbDevicesNumber--;
                    Utils.hasUsbDevice = true;
                } else if (Utils.usbDevicesNumber == 1) {
                    Utils.usbDevicesNumber = 0;
                    Utils.hasUsbDevice = false;
                }
                int usbCount = countUsbDevices(context);
                Log.d(TAG, "UsbDeviceReceiver countUsbDevices "+usbCount);
                if (usbCount==0){
                    Utils.hasUsbDevice = false;
                }
                if (!Utils.hasUsbDevice) {
                    Log.d(TAG, "UsbDeviceReceiver USB设备已经全部拔出 隐藏U盘图标");
                    callBack.UsbDeviceChange();
                }
                Log.d(TAG, "UsbDeviceReceiver 有USB设备拔出卸载 " + Utils.usbDevicesNumber);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int countUsbDevices(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumes = storageManager.getStorageVolumes();
        int usbCount = 0;

        for (StorageVolume volume : volumes) {
            if (volume.isRemovable()) {
                usbCount++;
            }
        }
        Log.d(TAG, "checkUsb  开机检测到 "+usbCount+" 个U盘");
        return usbCount;
    }

    public boolean isExternalStoragePath(String path) {
        if(path.equals("/storage/emulated/0")){
            return false;
        }
        return true;
    }

    // 判断 ImageView 显示的图片是否为指定资源
    public boolean isImageViewShowingDrawable(Context context, ImageView imageView, int drawableResId) {
        // 获取当前 ImageView 显示的 Drawable
        Drawable currentDrawable = imageView.getDrawable();
        // 获取目标 Drawable
        Drawable targetDrawable = ContextCompat.getDrawable(context, drawableResId);

        // 比较两者的 ConstantState（如果 ConstantState 相同，则代表是同一个资源）
        if (currentDrawable != null && targetDrawable != null) {
            boolean same = currentDrawable.getConstantState().equals(targetDrawable.getConstantState());
            Log.d(TAG," customBinding.muqiUsb 的对比结果");
            return same;
        }
        return false;
    }

}
