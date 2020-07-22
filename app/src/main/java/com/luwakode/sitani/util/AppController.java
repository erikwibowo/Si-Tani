package com.luwakode.sitani.util;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class AppController extends Application {
    private static final String TAG = AppController.class.getSimpleName();
    private static AppController instance;
    RequestQueue mRequestQueque;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

    }

    public static synchronized AppController getInstance(){
        return instance;
    }

    private RequestQueue getmRequestQueque(){
        if (mRequestQueque == null){
            mRequestQueque = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueque;
    }

    public <T> void addToRequestQueue (Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getmRequestQueque().add(req);
    }

    public <T> void addToRequestQueue (Request<T> req){
        req.setTag(TAG);
        getmRequestQueque().add(req);
    }

    public void cancelAllRequest(Object req){
        if (mRequestQueque != null){
            mRequestQueque.cancelAll(req);
        }
    }
}
