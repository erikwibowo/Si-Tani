package com.luwakode.sitani.activity;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {
    public abstract void findView();
    public abstract void initView();
    public abstract void initListener();
}
