package com.htc.launcher.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.launcher.R;

import java.io.File;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class WallPaperAdapter extends RecyclerView.Adapter<WallPaperAdapter.MyViewHolder> {

    Context mContext;
    private int[] drawables;
    WallPaperOnCallBack wallPaperOnCallBack;
    File[] files;
    boolean isLocal = true;
    ExecutorService threadExecutor;
    Handler handler;

    public WallPaperAdapter(Context mContext, int[] drawables, ExecutorService threadExecutor, Handler handler) {
        this.mContext = mContext;
        this.drawables = drawables;
        this.threadExecutor = threadExecutor;
        this.handler = handler;
    }

    public WallPaperAdapter(Context mContext, File[] files, ExecutorService threadExecutor, Handler handler) {
        this.mContext = mContext;
        this.files = files;
        isLocal = false;
        this.threadExecutor = threadExecutor;
        this.handler = handler;
    }


    public void setWallPaperOnCallBack(WallPaperOnCallBack wallPaperOnCallBack) {
        this.wallPaperOnCallBack = wallPaperOnCallBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wallpaper_custom_item, null));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        if (isLocal) {
            threadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                   Drawable d = null;
                    if (drawables[i] != 0) {
                        d = mContext.getDrawable(drawables[i]);
                    }

                    Drawable finalD = d;
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myViewHolder.icon.setBackground(finalD);
                        }
                    });

                }
            });
        }else {
            threadExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = compressImageFromFile(files[i].getAbsolutePath());
                    BitmapDrawable drawable = new BitmapDrawable(bitmap);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            myViewHolder.icon.setBackground(drawable);
                        }
                    });
                }
            });

        }
        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (wallPaperOnCallBack != null){
                    if (isLocal)
                        wallPaperOnCallBack.WallPaperLocalChange(drawables[i]);
                    else wallPaperOnCallBack.WallPaperUsbChange(files[i]);
                }


            }
        });
    }

    private Bitmap compressImageFromFile(String srcPath) {

        BitmapFactory.Options newOpts = new BitmapFactory.Options();

        newOpts.inJustDecodeBounds = true;//只读边,不读内容

        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);


        newOpts.inJustDecodeBounds = false;

        int w = newOpts.outWidth;

        int h = newOpts.outHeight;

        float hh = 400f;//

        float ww = 300f;//

        int be = 1;

        if (w > h && w > ww) {

            be = (int) (newOpts.outWidth / ww);

        } else if (w < h && h > hh) {

            be = (int) (newOpts.outHeight / hh);

        }

        if (be <= 0)

            be = 1;

        newOpts.inSampleSize = be;//设置采样率


        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设

        newOpts.inPurgeable = true;// 同时设置才会有效

        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收


        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        //      return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩

        //其实是无效的,大家尽管尝试

        return bitmap;

    }


    @Override
    public int getItemCount() {
        if (isLocal) return drawables.length;
        else return files.length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView status;
        RelativeLayout rl_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            status = itemView.findViewById(R.id.status);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }


    public interface WallPaperOnCallBack {
        void WallPaperLocalChange(int resId);
        void WallPaperUsbChange(File file);
    }
}
