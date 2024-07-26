package com.htc.launcher.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.htc.launcher.R;
import com.htc.launcher.activity.AppFavoritesActivity;
import com.htc.launcher.entry.ShortInfoBean;
import com.htc.launcher.utils.AppUtils;
import com.htc.launcher.utils.Utils;
import com.htc.launcher.view.MyCircleImageView;

import java.util.ArrayList;

/**
 * Author:
 * Date:
 * Description:
 */
public class ShortcutsAdapterCustom extends RecyclerView.Adapter<ShortcutsAdapterCustom.MyViewHolder> implements View.OnFocusChangeListener {

    Context mContext;
    private ArrayList<ShortInfoBean> short_list;
    ItemCallBack itemCallBack;

    int number = -1;

    public ShortcutsAdapterCustom(Context mContext, ArrayList<ShortInfoBean> short_list) {
        this.mContext = mContext;
        this.short_list = short_list;
    }

    public void setItemCallBack(ItemCallBack itemCallBack) {
        this.itemCallBack = itemCallBack;
    }

    public ItemCallBack getItemCallBack() {
        return itemCallBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        MyViewHolder myViewHolder = new MyViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.shortcuts_item_custom, null));

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
        if (i < short_list.size() && short_list.get(i).getAppname() != null && i > 0) {
            myViewHolder.icon.setImageDrawable(short_list.get(i).getAppicon());

        } else if (i < short_list.size() && i > 0) {

            myViewHolder.icon.setImageResource(getAppIcon(short_list.get(i).getPackageName()));
        } else if (i == 0) {

            myViewHolder.icon.setImageDrawable(short_list.get(i).getAppicon());

        }

        myViewHolder.rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCallBack != null)
                    itemCallBack.onItemClick(i);
            }
        });
        myViewHolder.rl_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AppUtils.startNewActivity(mContext, AppFavoritesActivity.class);
                return true;
            }
        });

        myViewHolder.rl_item.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                synchronized (Utils.class) {
                    if (hasFocus) {
                        Log.d("触发焦点获取", " 画白圈");
                        MyCircleImageView myCircleImageView = (MyCircleImageView) v.findViewById(R.id.icon);
                        myCircleImageView.hasFocus = true;
                        myCircleImageView.invalidate();

                    } else {
                        Log.d("触发焦点获取", " 恢复默认");
                        MyCircleImageView myCircleImageView = (MyCircleImageView) v.findViewById(R.id.icon);
                        myCircleImageView.hasFocus = false;
                        myCircleImageView.invalidate();
                    }
                }

                AnimationSet animationSet = new AnimationSet(true);
                v.bringToFront();
                if (hasFocus) {
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                            1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    scaleAnimation.setDuration(150);
                    animationSet.addAnimation(scaleAnimation);
                    animationSet.setFillAfter(true);
                    ViewCompat.setTranslationZ(v, 10f);
                    v.startAnimation(animationSet);
                } else {
                    ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                            1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                            Animation.RELATIVE_TO_SELF, 0.5f);
                    animationSet.addAnimation(scaleAnimation);
                    scaleAnimation.setDuration(150);
                    animationSet.setFillAfter(true);
                    ViewCompat.setTranslationZ(v, 0f);
                    v.startAnimation(animationSet);
                }


            }
        });

    }


    private String getAppName(String pkg) {
        switch (pkg) {
            case "com.netflix.ninja":
                return "Netflix";
            case "com.disney.disneyplus":
                return "Disney+";
            case "com.google.android.youtube.tv":
                return "Youtube";
            case "com.chrome.beta":
                return "Chrome";
            case "com.amazon.avod.thirdpartyclient":
                return "Prime Video";
            case "net.cj.cjhv.gs.tving":
                return "TVING";
            case "com.wbd.stream":
                return "HBO Max";
            case "com.frograms.wplay":
                return "WATCHA";
            case "in.startv.hotstar.dplus":
                return "Hotstar";
            case "com.jio.media.ondemand":
                return "JioCinema";
            case "jp.happyon.android":
                return "Hulu";
            case "tv.abema":
                return "ABEMA";
            default:
                return "APK";
        }
    }

    private int getAppIcon(String pkg) {
        switch (pkg) {
            case "com.netflix.ninja":
                return R.drawable.netflix;
            case "com.disney.disneyplus":
                return R.drawable.disney;
            case "com.google.android.youtube.tv":
                return R.drawable.youtube;
            case "com.chrome.beta":
                return R.drawable.chrome;
            case "com.amazon.avod.thirdpartyclient":
                return R.drawable.primevideo;
            case "net.cj.cjhv.gs.tving":
                return R.drawable.tving;
            case "com.wbd.stream":
                return R.drawable.max;
            case "com.frograms.wplay":
                return R.drawable.watcha;
            case "in.startv.hotstar.dplus":
                return R.drawable.hotstar;
            case "com.jio.media.ondemand":
                return R.drawable.jio_cinema;
            case "jp.happyon.android":
                return R.drawable.hulu;
            case "tv.abema":
                return R.drawable.abema;
            default:
                return R.mipmap.ic_launcher_round;
        }
    }

    @Override
    public int getItemCount() {
        if (short_list.size() < 8) {
            return short_list.size() + 1;
        } else {
            return short_list.size();
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    public interface ItemCallBack {
        void onItemClick(int i);
    }


    static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnFocusChangeListener {
        MyCircleImageView icon;
        RelativeLayout rl_item;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
//            name = itemView.findViewById(R.id.name);
            rl_item = itemView.findViewById(R.id.rl_item);
            icon = itemView.findViewById(R.id.icon);

            icon.rl_item = rl_item;

//            rl_item.setOnFocusChangeListener(this);
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            AnimationSet animationSet = new AnimationSet(true);
            v.bringToFront();
            if (hasFocus) {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 1.10f,
                        1.0f, 1.10f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                scaleAnimation.setDuration(150);
                animationSet.addAnimation(scaleAnimation);
                animationSet.setFillAfter(true);
                v.startAnimation(animationSet);
            } else {
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                        1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f);
                animationSet.addAnimation(scaleAnimation);
                scaleAnimation.setDuration(150);
                animationSet.setFillAfter(true);
                v.startAnimation(animationSet);
            }
        }

    }

    //Drawable 放大成160x160
    public Drawable resizeDrawable(Context context, Drawable image, int width, int height) {
        if (image == null) {
            return null;
        }
        Bitmap bitmap = drawableToBitamp(image);
        return getScaledDrawable(bitmap, 1.5f);

    }

    public Bitmap drawableToBitamp(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private Drawable getScaledDrawable(Bitmap bitmap, float scale) {
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth,
                bitmapHeight, matrix, true);
        return new BitmapDrawable(resizeBitmap);
    }


}
