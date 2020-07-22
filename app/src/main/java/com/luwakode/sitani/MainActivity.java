package com.luwakode.sitani;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

import com.luwakode.sitani.activity.BaseActivity;
import com.luwakode.sitani.activity.Konsultasi;
import com.luwakode.sitani.activity.Profil;
import com.luwakode.sitani.activity.WebView;
import com.luwakode.sitani.adapter.SliderAdapterExample;
import com.luwakode.sitani.model.ModelSlider;
import com.luwakode.sitani.server.ServerApi;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    SliderView sliderView;
    SliderAdapterExample adapter;
    List<ModelSlider> mItems;

    CardView card_artikel, card_pupuk, card_hama, card_harga, card_konsultasi, card_profil;

    private int[] slider = {
            R.drawable.slider1,
            R.drawable.slider2,
            R.drawable.slider3
    };

    private String[] judul = {
            "Ayo menanam buah",
            "Ayo menanam sayur",
            "Ayo bercocok tanam"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        findView();
        initView();
        initListener();
        load_slider();
    }

    @Override
    public void findView() {
        sliderView = findViewById(R.id.imageSlider);
        card_artikel = findViewById(R.id.card_artikel);
        card_pupuk = findViewById(R.id.card_pupuk);
        card_hama = findViewById(R.id.card_hama);
        card_harga = findViewById(R.id.card_harga);
        card_konsultasi = findViewById(R.id.card_konsultasi);
        card_profil = findViewById(R.id.card_profil);
    }

    @Override
    public void initView() {
        mItems = new ArrayList<>();
        adapter = new SliderAdapterExample(mItems, getApplicationContext());
        sliderView.setSliderAdapter(adapter);
        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setCircularHandlerEnabled(true);
        sliderView.startAutoCycle();
    }

    @Override
    public void initListener() {
        card_artikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WebView.class);
                i.putExtra("title", "Artikel");
                i.putExtra("url", ServerApi.artikel);
                startActivity(i);
            }
        });
        card_pupuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WebView.class);
                i.putExtra("title", "Pupuk");
                i.putExtra("url", ServerApi.pupuk);
                startActivity(i);
            }
        });
        card_hama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WebView.class);
                i.putExtra("title", "Hama");
                i.putExtra("url", ServerApi.hama);
                startActivity(i);
            }
        });
        card_harga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, WebView.class);
                i.putExtra("title", "Harga");
                i.putExtra("url", ServerApi.harga);
                startActivity(i);
            }
        });
        card_konsultasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Konsultasi.class);
                startActivity(i);
            }
        });
        card_profil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Profil.class);
                startActivity(i);
            }
        });
    }

    private void load_slider(){
        adapter.setCount(slider.length);
        for (int i = 0; i < slider.length; i++){
            ModelSlider mdslider = new ModelSlider();
            mdslider.setSlider(slider[i]);
            mdslider.setCaption(judul[i]);
            mItems.add(mdslider);
        }
        adapter.notifyDataSetChanged();
    }
}