package com.htc.smoonos.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.htc.smoonos.activity.InitAngleActivity;

public class InitAngleReceiver extends BroadcastReceiver {
    private Context mContext;
    private static String TAG = "InitAngleReceiver";

    InitAngleReceiver(){

    }

    public InitAngleReceiver(Context context) {
        mContext = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG," 收到广播 "+intent.getAction());
        if(intent.getAction().equals("com.htc.INITANGLE")) {
            startNewActivity(InitAngleActivity.class);
        }
    }

    public void startNewActivity(Class<?> cls) {
        Intent intent = new Intent(mContext, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}