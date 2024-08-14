package com.htc.luminaos.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.luminaos.R;
import com.htc.luminaos.utils.Contants;
import com.htc.luminaos.utils.ShareUtil;
import com.htc.luminaos.widget.FocusKeepRecyclerView;

import java.io.File;
import java.util.concurrent.ExecutorService;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Author:
 * Date:
 * Description:
 */
public class WallPaperAdapter extends RecyclerView.Adapter<WallPaperAdapter.MyViewHolder> implements View.OnFocusChangeListener  {

    Context mContext;
    private int[] drawables;
    WallPaperOnCallBack wallPaperOnCallBack;
    File[] files;
    boolean isLocal = true;
    ExecutorService threadExecutor;
    Handler handler;

    FocusKeepRecyclerView focusKeepRecyclerView;

    private int selectpostion = -1;
    private static String TAG = "WallPaperAdapter";

    public WallPaperAdapter(Context mContext, int[] drawables, ExecutorService threadExecutor, Handler handler) {
        this.mContext = mContext;
        this.drawables = drawables;
        this.threadExecutor = threadExecutor;
        this.handler = handler;
        selectpostion = readShared();
    }

    public WallPaperAdapter(Context mContext, File[] files, ExecutorService threadExecutor, Handler handler) {
        this.mContext = mContext;
        this.files = files;
        isLocal = false;
        this.threadExecutor = threadExecutor;
        this.handler = handler;
        selectpostion = readShared();
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
        if(i == selectpostion){
            myViewHolder.check.setVisibility(View.VISIBLE);
            myViewHolder.check.setImageResource(R.drawable.check_correct);
        }

        myViewHolder.rl_item.setOnFocusChangeListener(this);

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

                //xuhao add
                int position = myViewHolder.getAdapterPosition();
                myViewHolder.check.setImageResource(R.drawable.check_correct);
                myViewHolder.check.setVisibility(View.VISIBLE);
                if (selectpostion != -1) {
                    Message message = handler.obtainMessage(Contants.RESET_CHECK);
                    message.arg1 = selectpostion;  // 将 selectpostion 作为消息的 arg1 传递
                    handler.sendMessage(message);
                }
                //写入数据库
                writeShared(position);
                selectpostion =readShared();
                Log.d(TAG," 当前点击的位置是 "+position);
                //xuhao

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
        FrameLayout rl_item;

        ImageView check;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            check = itemView.findViewById(R.id.check);
            status = itemView.findViewById(R.id.status);
            rl_item = itemView.findViewById(R.id.rl_item);
        }
    }


    public interface WallPaperOnCallBack {
        void WallPaperLocalChange(int resId);
        void WallPaperUsbChange(File file);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);

        Log.d(TAG," 放大图片 " +position);
        ImageView check = (ImageView)v.findViewById(R.id.check);
        AnimationSet animationSet = new AnimationSet(true);
        v.bringToFront();
        if (hasFocus) {
//            if(position != selectpostion) {
//                check.setVisibility(View.VISIBLE);
//            }
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                    1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            scaleAnimation.setDuration(150);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        } else {
//            if(position != selectpostion) {
//                check.setImageResource(R.drawable.check_no);
//                check.setVisibility(View.GONE);
//            }
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                    1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animationSet.addAnimation(scaleAnimation);
            scaleAnimation.setDuration(150);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
        }
    }

    private void writeShared (int postion) {

        SharedPreferences sharedPreferences = ShareUtil.getInstans(mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Contants.SelectWallpaper, postion);
        editor.apply();

    }

    private int readShared() {
        SharedPreferences sharedPreferences = ShareUtil.getInstans(mContext);
        return sharedPreferences.getInt(Contants.SelectWallpaper, -1); // -1 是默认值，当没有找到该键时返回
    }


}
