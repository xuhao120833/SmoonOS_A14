package com.htc.smoonos.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.htc.smoonos.MyApplication;
import com.htc.smoonos.R;
import com.htc.smoonos.utils.Utils;

import androidx.annotation.Nullable;

/**
 * Author:
 * Date:
 * Description:
 */
public class BaseMainActivity extends Activity implements View.OnClickListener, View.OnHoverListener, View.OnFocusChangeListener {

    private static String TAG = "BaseMainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        try {
            setWallPaper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    public void setWallPaper() {
        if (MyApplication.mainDrawable != null) {
            ViewGroup relativeLayout = findViewById(R.id.rl_main);
            if (relativeLayout != null)
                relativeLayout.setBackground(MyApplication.mainDrawable);
        } else {
            ViewGroup relativeLayout = findViewById(R.id.rl_main);
            if (relativeLayout != null) {
                relativeLayout.setBackground(Utils.drawables.get(0));
            }
        }
    }

    public void setWallPaper(int resId) {
        if (MyApplication.mainDrawable != null) {
            RelativeLayout relativeLayout = findViewById(R.id.rl_main);
            if (relativeLayout != null)
                relativeLayout.setBackground(MyApplication.mainDrawable);
        } else if (resId != -1) {
            RelativeLayout relativeLayout = findViewById(R.id.rl_main);
            if (relativeLayout != null)
                relativeLayout.setBackgroundResource(resId);
        }
    }

    public void setWallPaper(Drawable drawable) {
        RelativeLayout relativeLayout = findViewById(R.id.rl_main);
        if (relativeLayout != null)
            relativeLayout.setBackground(drawable);
    }


    @Override
    public void onClick(View v) {

    }

    public void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void startNewActivity(String packageName, String activity) {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(packageName, activity));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
            if (v.getId() == R.id.rl_muqi_icon4) {
                Log.d(TAG," rl_muqi_icon4获取到焦点");
                // 获取 rl_muqi_icon4 的父级布局
                ViewGroup rl_muqi_icon4 = (ViewGroup) v.getParent();
                // 通过 parentLayout 查找 muqi_text4
                TextView muqiText4 = rl_muqi_icon4.findViewById(R.id.muqi_text4);
                if (muqiText4 != null) {
                    muqiText4.setSelected(true); // 启动跑马灯效果
                }
            }
        } else {
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.10f, 1.0f,
                    1.10f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f);
            animationSet.addAnimation(scaleAnimation);
            scaleAnimation.setDuration(150);
            animationSet.setFillAfter(true);
            v.startAnimation(animationSet);
            if (v.getId() == R.id.rl_muqi_icon4) {
                Log.d(TAG," rl_muqi_icon4获取到焦点");
                // 获取 rl_muqi_icon4 的父级布局
                ViewGroup rl_muqi_icon4 = (ViewGroup) v.getParent();
                // 通过 parentLayout 查找 muqi_text4
                TextView muqiText4 = rl_muqi_icon4.findViewById(R.id.muqi_text4);
                if (muqiText4 != null) {
                    muqiText4.setSelected(false); // 关闭跑马灯效果
                }
            }
        }
    }

    @Override
    public boolean onHover(View v, MotionEvent event) {
        int what = event.getAction();
        switch (what) {
            case MotionEvent.ACTION_HOVER_ENTER: // 鼠标进入view
                v.requestFocus();
                break;
            case MotionEvent.ACTION_HOVER_MOVE: // 鼠标在view上
                break;
            case MotionEvent.ACTION_HOVER_EXIT: // 鼠标离开view
                break;
        }
        return false;
    }
}