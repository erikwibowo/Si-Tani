package com.luwakode.sitani.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.luwakode.sitani.R;
import com.luwakode.sitani.server.ServerApi;
import com.luwakode.sitani.util.AppController;
import com.luwakode.sitani.util.PrefManager;

import org.json.JSONException;
import org.json.JSONObject;

public class Profil extends BaseActivity {

    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout profil;
    RelativeLayout layout_koneksi, layout_kosong, layout_akun;
    ShimmerFrameLayout shimmer;
    TextView nama, email, alamat, nohp;
    String ft = null, id_user;
    PrefManager prefManager;
    Button btn_masuk, btn_daftar, btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profil");

        findView();
        initView();
        initListener();
        islogin();
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        islogin();
    }

    @Override
    public void findView() {
        swipeRefreshLayout = findViewById(R.id.swipe);
        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_kosong = findViewById(R.id.layout_kosong);
        layout_akun = findViewById(R.id.layout_akun);
        shimmer = findViewById(R.id.shimmer);
        profil = findViewById(R.id.profil);
        nama = findViewById(R.id.nama);
        email = findViewById(R.id.email);
        alamat = findViewById(R.id.alamat);
        nohp = findViewById(R.id.nohp);
        btn_masuk = findViewById(R.id.btn_masuk);
        btn_daftar = findViewById(R.id.btn_daftar);
        btn_logout = findViewById(R.id.btnlogout);
    }

    @Override
    public void initView() {

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

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Profil.this);
                builder.setMessage("Apakah anda yakon akan keluar?")
                        .setCancelable(false)
                        .setNegativeButton("Batal", null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                logout();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
    }

    private void set_loading(){
        shimmer.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
        profil.setVisibility(View.GONE);
        shimmer.startShimmer();
    }

    private void load_success(){
        shimmer.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
        profil.setVisibility(View.VISIBLE);
        shimmer.stopShimmer();
    }

    private void load_fail(){
        shimmer.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        layout_koneksi.setVisibility(View.VISIBLE);
        layout_kosong.setVisibility(View.GONE);
        profil.setVisibility(View.GONE);
        shimmer.stopShimmer();
    }

    private void load_empty(){
        shimmer.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(false);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.VISIBLE);
        profil.setVisibility(View.GONE);
        shimmer.stopShimmer();
    }

    private void islogin(){
        prefManager = new PrefManager(getApplicationContext());
        id_user = prefManager.getIdUser();
        if (prefManager.getLoginStatus()) {
            layout_akun.setVisibility(View.GONE);
            getData();
        }else{
            profil.setVisibility(View.GONE);
            layout_akun.setVisibility(View.VISIBLE);
        }
    }

    private void getData(){
        set_loading();
        JsonObjectRequest requestData = new JsonObjectRequest(Request.Method.GET, ServerApi.profil+id_user, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("volley", "response : "+response.getString("id_user"));
                            nama.setText(response.getString("nama"));
                            email.setText(response.getString("username"));
                            alamat.setText(response.getString("alamat"));
                            nohp.setText(response.getString("no_hp"));
                            load_success();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            load_fail();
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

    private void logout(){
        prefManager.setIdUser("");
        prefManager.setLoginStatus(false);
        islogin();
    }
}