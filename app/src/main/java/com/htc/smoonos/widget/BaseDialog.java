package com.htc.smoonos.widget;

import android.app.Dialog;
import android.content.Context;
import android.widget.RelativeLayout;

import com.htc.smoonos.MyApplication;
import com.htc.smoonos.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author:
 * Date:
 * Description:
 */
public class BaseDialog extends Dialog {
    public BaseDialog(@NonNull Context context) {
        super(context);
    }

    public BaseDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected BaseDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onStart() {
        setWallPaper();
        super.onStart();
    }

    public void setWallPaper(){
        if (MyApplication.mainDrawable!=null){
            RelativeLayout relativeLayout = Objects.requireNonNull(getWindow()).getDecorView().findViewById(R.id.rl_main);
            if (relativeLayout!=null)
                relativeLayout.setBackground(MyApplication.mainDrawable);
        }
    }
}
