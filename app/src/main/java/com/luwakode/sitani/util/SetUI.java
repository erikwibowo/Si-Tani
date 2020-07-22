package com.luwakode.sitani.util;

import android.app.Activity;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

public class SetUI {
    public void StatusBarTransparent(Window window){
        // membuat transparan notifikasi
        if (Build.VERSION.SDK_INT >= 21) {
            window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }

    public void FullScreenActivity(Window window){
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void snackBar(int pesan, int color, View view, Activity activity){
        Snackbar snackbar = Snackbar.make(view, pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(activity, color));
        snackbar.show();
    }
}
