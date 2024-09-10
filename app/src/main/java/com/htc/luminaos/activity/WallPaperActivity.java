package com.htc.luminaos.activity;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.storage.StorageVolume;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.htc.luminaos.MyApplication;
import com.htc.luminaos.R;
import com.htc.luminaos.adapter.WallPaperAdapter;
import com.htc.luminaos.databinding.ActivityWallPaperBinding;
import com.htc.luminaos.utils.BlurImageView;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.DialogUtils;
import com.htc.luminaos.utils.LogUtils;
import com.htc.luminaos.utils.StorageUtils;
import com.htc.luminaos.widget.FocusKeepRecyclerView;
import com.htc.luminaos.widget.SpacesItemDecoration;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WallPaperActivity extends BaseActivity {

    private ActivityWallPaperBinding wallPaperBinding;
    private ArrayList<File> file_toArray = new ArrayList<>();

    ExecutorService singer = Executors.newSingleThreadExecutor();
    ExecutorService threadExecutor = Executors.newFixedThreadPool(5);

    private Dialog switchDialog =null;

    long curTime = 0;

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case Contants.PICTURE_NULL:
                    wallPaperBinding.folderResult.setBackgroundResource(R.drawable.folder_x);
                    break;
                case Contants.PICTURE_RESULT:
                    if (msg.obj!= null){
                        File[] files =(File[]) msg.obj;
                        WallPaperAdapter wallPaperAdapter = new WallPaperAdapter(WallPaperActivity.this,files,threadExecutor,handler);
                        wallPaperAdapter.setWallPaperOnCallBack(onCallBack);
                        wallPaperBinding.wallpaperRv.setAdapter(wallPaperAdapter);
                    }
                    wallPaperBinding.folderResult.setVisibility(View.GONE);
                    wallPaperBinding.wallpaperRv.setVisibility(View.VISIBLE);
                    break;
                case Contants.PICTURE_FIND:
                    wallPaperBinding.folderResult.setVisibility(View.VISIBLE);
                    wallPaperBinding.folderResult.setBackgroundResource(R.drawable.folder);
                    wallPaperBinding.wallpaperRv.setVisibility(View.GONE);
                    break;
                case Contants.DISSMISS_DIALOG:
                    if (switchDialog!=null && switchDialog.isShowing())
                        switchDialog.dismiss();

                    setWallPaper();
                    break;
                case Contants.RESET_CHECK:
                    int receivedPosition = msg.arg1;
                    FocusKeepRecyclerView.ViewHolder viewHolder = wallPaperBinding.wallpaperRv.findViewHolderForAdapterPosition(receivedPosition);
                    if (viewHolder != null) {
                        View itemView = viewHolder.itemView;
                        ImageView check = itemView.findViewById(R.id.check);
                        check.setImageResource(R.drawable.check_no);
                        check.setVisibility(View.GONE);
                    }
                    break;

            }

            return false;
        }
    });

    BroadcastReceiver mediaReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d("hzj","aciton "+intent.getAction());
            if (Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction())
                    || Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())
                    || Intent.ACTION_MEDIA_BAD_REMOVAL.equals(intent.getAction())
                    || Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction())){
                if (System.currentTimeMillis() -curTime<300)
                    return;

                curTime = System.currentTimeMillis();
                StorageVolume storage = (StorageVolume)intent.getParcelableExtra(
                        StorageVolume.EXTRA_STORAGE_VOLUME);
                String path =storage.getPath();
                if (isExternalStoragePath(path)){
                    if (wallPaperBinding.usbItem.isSelected())
                        loadUSB();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wallPaperBinding = ActivityWallPaperBinding.inflate(LayoutInflater.from(this));
        setContentView(wallPaperBinding.getRoot());
        initView();
        initData();
    }

    private void initView(){
        wallPaperBinding.localItem.setOnClickListener(this);
        wallPaperBinding.usbItem.setOnClickListener(this);
//        GridLayoutManager layoutManager = new GridLayoutManager(this,6);//原生是6列
        GridLayoutManager layoutManager = new GridLayoutManager(this,3);
        wallPaperBinding.wallpaperRv.setLayoutManager(layoutManager);
        wallPaperBinding.wallpaperRv.addItemDecoration(new SpacesItemDecoration(SpacesItemDecoration.pxAdapter(22.5F),SpacesItemDecoration.pxAdapter(22.5F),SpacesItemDecoration.pxAdapter(10),SpacesItemDecoration.pxAdapter(10)));


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addDataScheme("file");
        registerReceiver(mediaReceiver,intentFilter);
    }

    private void initData(){
        wallPaperBinding.localItem.setSelected(true);
        wallPaperBinding.usbItem.setSelected(false);
        loadLocal();
    }

    private void loadLocal(){
        WallPaperAdapter wallPaperAdapter = new WallPaperAdapter(getApplicationContext(), Contants.drawables,threadExecutor,handler);
        wallPaperAdapter.setHasStableIds(true);
        wallPaperAdapter.setWallPaperOnCallBack(onCallBack);
        wallPaperBinding.wallpaperRv.setAdapter(wallPaperAdapter);
        wallPaperBinding.wallpaperRv.setVisibility(View.VISIBLE);
        wallPaperBinding.folderResult.setVisibility(View.GONE);
    }

    public boolean isExternalStoragePath(String path) {
        if (path.equals("/storage/emulated/0")) {
            return false;
        }
        return true;
    }

    WallPaperAdapter.WallPaperOnCallBack onCallBack = new WallPaperAdapter.WallPaperOnCallBack() {
        @Override
        public void WallPaperLocalChange(int resId) {
            switchDialog = DialogUtils.createLoadingDialog(WallPaperActivity.this,getString(R.string.switch_wallpaper_tips));
            switchDialog.show();
            threadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    CopyResIdToSd(resId);
                    CopyResIdToSd(BlurImageView.BoxBlurFilter(WallPaperActivity.this,resId));
                    if (new File(Contants.WALLPAPER_MAIN).exists())
                        MyApplication.mainDrawable =new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN));
                    if (new File(Contants.WALLPAPER_OTHER).exists())
                        MyApplication.otherDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_OTHER));
                    handler.sendEmptyMessage(Contants.DISSMISS_DIALOG);
                }
            });
        }

        @Override
        public void WallPaperUsbChange(File file) {
            switchDialog = DialogUtils.createLoadingDialog(WallPaperActivity.this,getString(R.string.switch_wallpaper_tips));
            switchDialog.show();
            threadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    CopyFileToSd(file);
                    CopyFileToSd(BlurImageView.BoxBlurFilter(BitmapFactory.decodeFile(file.getAbsolutePath())));
                    if (new File(Contants.WALLPAPER_MAIN).exists())
                        MyApplication.mainDrawable =new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_MAIN));
                    if (new File(Contants.WALLPAPER_OTHER).exists())
                        MyApplication.otherDrawable = new BitmapDrawable(BitmapFactory.decodeFile(Contants.WALLPAPER_OTHER));
                    handler.sendEmptyMessage(Contants.DISSMISS_DIALOG);
                }
            });
        }
    };

    private void CopyResIdToSd(int resId){
        File file =new File(Contants.WALLPAPER_DIR);
        if (!file.exists())
            file.mkdir();


        InputStream inputStream = getResources().openRawResource(resId);
        try {
            File file1 = new File(Contants.WALLPAPER_MAIN);
            if (file1.exists())
                file1.delete();

            FileOutputStream fileOutputStream = new FileOutputStream(file1);

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, bytesRead);
            }
            fileOutputStream.flush();
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void CopyResIdToSd(Bitmap bitmap){
        File file1 =new File(Contants.WALLPAPER_DIR);
        if (!file1.exists())
            file1.mkdir();

        File file=new File(Contants.WALLPAPER_OTHER);//将要保存图片的路径
        if (file.exists())
            file.delete();
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void CopyFileToSd(File file){
        File file1 =new File(Contants.WALLPAPER_DIR);
        if (!file1.exists())
            file1.mkdir();


        try {
            File file2 = new File(Contants.WALLPAPER_MAIN);
            if (file2.exists())
                file2.delete();

            FileInputStream fileInputStream = new FileInputStream(file);
            FileOutputStream fileOutputStream = new FileOutputStream(file2);

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buf)) != -1) {
                fileOutputStream.write(buf, 0, bytesRead);
            }
            fileOutputStream.flush();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void CopyFileToSd(Bitmap bitmap){
        File file1 =new File(Contants.WALLPAPER_DIR);
        if (!file1.exists())
            file1.mkdir();

        File file=new File(Contants.WALLPAPER_OTHER);//将要保存图片的路径
        if (file.exists())
            file.delete();
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUSB(){
        handler.sendEmptyMessage(Contants.PICTURE_FIND);
        List<String> listPaths = StorageUtils.getUSBPaths(this);
        file_toArray.clear();
        File[] fileList = null;
        if (listPaths.size()>0){
            for (int i=0;i<listPaths.size();i++){
                File file = new File(listPaths.get(i));
                if (file.canRead()){
                    file_toArray.addAll(Arrays.asList(file.listFiles(pictureFilter)));
                }
            }
            fileList =  file_toArray.toArray(new File[0]);
            if (fileList.length==0) {
                handler.sendEmptyMessage(Contants.PICTURE_NULL);
            } else{
                Message message = handler.obtainMessage();
                message.what= Contants.PICTURE_RESULT;
                message.obj = fileList;
                handler.sendMessage(message);
            }
        }else {
            handler.sendEmptyMessage(Contants.PICTURE_NULL);
        }
    }

    public FileFilter pictureFilter = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            // TODO Auto-generated method stub
            // keep all needed files
            try {
                if (pathname.isDirectory()) {
                    /*filesNum++;
                    return true;*/
                    File[] files = pathname.listFiles(pictureFilter);
                    if (files!=null && files.length>0)
                        file_toArray.addAll(Arrays.asList(files));
                }
            } catch (SecurityException e) {
                e.printStackTrace();
                return false;
            }

            String name = pathname.getAbsolutePath();
            if (isPictureFile(name)) {
                return true;
            }

            return false;
        }
    };


    public static boolean isPictureFile(String path) {
        try {
            String ext = path.substring(path.lastIndexOf(".") + 1);
            if (ext.equalsIgnoreCase("png") || ext.equalsIgnoreCase("jpeg")
                    || ext.equalsIgnoreCase("jpg")
                    || ext.equalsIgnoreCase("bmp") || ext.equalsIgnoreCase("jfif")
                    || ext.equalsIgnoreCase("tiff")|| ext.equalsIgnoreCase("webp")) {
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.usb_item:
                if (wallPaperBinding.usbItem.isSelected())
                    break;

                wallPaperBinding.localItem.setSelected(false);
                wallPaperBinding.usbItem.setSelected(true);

                singer.execute(new Runnable() {
                    @Override
                    public void run() {
                        loadUSB();
                    }
                });
                break;
            case R.id.local_item:
                if (wallPaperBinding.localItem.isSelected())
                    break;

                wallPaperBinding.localItem.setSelected(true);
                wallPaperBinding.usbItem.setSelected(false);
                loadLocal();
                break;
        }
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(mediaReceiver);

        if (!singer.isShutdown()) {
            singer.shutdown();
            singer.shutdownNow();
        }

        if (!threadExecutor.isShutdown()) {
            threadExecutor.shutdown();
            threadExecutor.shutdownNow();
        }
        super.onDestroy();
    }
}