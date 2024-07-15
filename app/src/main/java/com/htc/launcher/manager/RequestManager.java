package com.htc.launcher.manager;

import com.htc.launcher.utils.LogUtils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RequestManager {

    private static final RequestManager requestManager = new RequestManager();

    public static RequestManager getInstance(){
        return requestManager;
    }

    public  Call getData(String url, Callback callback){
        LogUtils.d("url="+url);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);
        Call call = okHttpClient.newCall(requestBuilder.build());
        call.enqueue(callback);
        return call;
    }
}
