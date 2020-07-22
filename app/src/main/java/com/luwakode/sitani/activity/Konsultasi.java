package com.luwakode.sitani.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.luwakode.sitani.R;
import com.luwakode.sitani.adapter.AdapterKonsultasi;
import com.luwakode.sitani.model.ModelKonsultasi;
import com.luwakode.sitani.server.ServerApi;
import com.luwakode.sitani.util.AppController;
import com.luwakode.sitani.util.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Konsultasi extends BaseActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    AdapterKonsultasi mAdapter;
    List<ModelKonsultasi> mItems;
    RecyclerView recycle;
    RelativeLayout layout_koneksi, layout_kosong, layout_akun;
    ShimmerFrameLayout shimmer;
    FloatingActionButton fab;

    Button btn_masuk, btn_daftar;

    PrefManager prefManager;
    String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konsultasi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Konsultasi");

        findView();
        initView();
        initListener();
        getData();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        login_view();
    }

    @Override
    public void findView() {
        recycle = findViewById(R.id.recycle_konsultasi);
        swipeRefreshLayout = findViewById(R.id.swipe);
        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_kosong = findViewById(R.id.layout_kosong);
        layout_akun = findViewById(R.id.layout_akun);
        shimmer = findViewById(R.id.shimmer);
        fab = findViewById(R.id.fab);

        btn_masuk = findViewById(R.id.btn_masuk);
        btn_daftar = findViewById(R.id.btn_daftar);
    }

    @Override
    public void initView() {
        mItems = new ArrayList<>();
        mAdapter = new AdapterKonsultasi(mItems, getApplicationContext());
        recycle.setAdapter(mAdapter);

        prefManager = new PrefManager(this);
        login_view();
    }

    @Override
    public void initListener() {
        layout_koneksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        layout_kosong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Konsultasi.this, BuatKonsultasi.class);
                startActivity(i);
            }
        });
        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
            }
        });
        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Daftar.class);
                startActivity(i);
            }
        });
    }

    private void login_view(){
        prefManager = new PrefManager(getApplicationContext());
        if (prefManager.getLoginStatus()) {
            id_user = prefManager.getIdUser();
            layout_akun.setVisibility(View.GONE);
            getData();
            fab.show();
        }else{
            layout_akun.setVisibility(View.VISIBLE);
            shimmer.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            layout_koneksi.setVisibility(View.GONE);
            layout_kosong.setVisibility(View.GONE);
            recycle.setVisibility(View.GONE);
            shimmer.stopShimmer();
            fab.hide();
        }
    }

    private void set_loading(){
        shimmer.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
        recycle.setVisibility(View.GONE);
        shimmer.startShimmer();
    }

    private void load_success(){
        shimmer.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
        recycle.setVisibility(View.VISIBLE);
        shimmer.stopShimmer();
    }

    private void load_fail(){
        shimmer.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        layout_koneksi.setVisibility(View.VISIBLE);
        layout_kosong.setVisibility(View.GONE);
        recycle.setVisibility(View.GONE);
        shimmer.stopShimmer();
    }

    private void load_empty(){
        shimmer.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.VISIBLE);
        recycle.setVisibility(View.GONE);
        shimmer.stopShimmer();
    }

    private void getData(){
        set_loading();
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.GET, ServerApi.konsultasi+id_user, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("volley", "response : "+response.toString());
                        if (response.length() > 0){
                            load_success();
                            mItems.clear();
                            for (int i = 0; i< response.length(); i++){
                                try {
                                    JSONObject data = response.getJSONObject(i);
                                    ModelKonsultasi md = new ModelKonsultasi();
                                    md.setId_kategori(data.getString("id_kategori"));
                                    md.setId_konsultasi(data.getString("id_konsultasi"));
                                    md.setId_user(data.getString("id_user"));
                                    md.setWaktu(data.getString("waktu"));
                                    md.setJudul(data.getString("judul"));
                                    md.setPertanyaan(data.getString("pertanyaan"));
                                    md.setNama(data.getString("nama"));
                                    md.setTanggapan(data.getString("tanggapan"));
                                    mItems.add(md);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    load_fail();
                                }

                            }
                            mAdapter.notifyDataSetChanged();
                            load_success();
                        }else{
                            load_empty();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley", "error : "+error.getMessage());
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            load_fail();
                        }else{
                            load_fail();
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }
}