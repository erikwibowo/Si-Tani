package com.luwakode.sitani.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.luwakode.sitani.R;
import com.luwakode.sitani.adapter.AdapterTanggapan;
import com.luwakode.sitani.model.ModelKonsultasi;
import com.luwakode.sitani.model.ModelTanggapan;
import com.luwakode.sitani.server.ServerApi;
import com.luwakode.sitani.util.AppController;
import com.luwakode.sitani.util.PrefManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class DetailKonsultasi extends BaseActivity {

    TextView judul, tanggal, pertanyaan, txt_kosong, kategori;
    Bundle bundle;

    AdapterTanggapan mAdapter;
    List<ModelTanggapan> mItems;
    RecyclerView recycle_komentar;
    RelativeLayout layout_koneksi, layout_kosong;
    ProgressBar progress;

    LinearLayout input_tanggapan;
    EditText txt_isi_tanggapan;
    FloatingActionButton fab_kirim;

    PrefManager prefManager;
    AlertDialog dialog, dialog_hapus;
    String id_user, id_konsultasi;
    CardView card_foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_konsultasi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detail Konsultasi");

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
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_hapus_konsultasi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_hapus:
                //action here
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailKonsultasi.this);
                builder.setMessage("Apakah anda yakin akan menghapus konsultasi ini?")
                        .setCancelable(false)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                hapus_laporan();
                            }
                        })
                        .setNegativeButton("Tidak", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void findView() {
        judul = findViewById(R.id.judul);
        tanggal = findViewById(R.id.waktu);
        pertanyaan = findViewById(R.id.pertanyaan);
        kategori = findViewById(R.id.kategori);

        txt_kosong = findViewById(R.id.txt_kosong);

        recycle_komentar = findViewById(R.id.recycle_komentar);
        layout_koneksi = findViewById(R.id.layout_koneksi);
        layout_kosong = findViewById(R.id.layout_kosong);
        progress = findViewById(R.id.progress_tanggapan);

        input_tanggapan = findViewById(R.id.input_tanggapan);
        txt_isi_tanggapan = findViewById(R.id.txt_isi_tanggapan);
        fab_kirim = findViewById(R.id.fab_kirim);
    }

    @Override
    public void initView() {
        prefManager = new PrefManager(getApplicationContext());
        if (prefManager.getLoginStatus()) {
            input_tanggapan.setVisibility(View.VISIBLE);
        }else{
            input_tanggapan.setVisibility(View.GONE);
        }

        id_user = prefManager.getIdUser();
        bundle = getIntent().getExtras();
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Sedang mengirim tanggapan...")
                .setCancelable(false)
                .build();
        dialog_hapus = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Sedang menghapus konsultasi...")
                .setCancelable(false)
                .build();

        judul.setText(bundle.getString("judul"));
        tanggal.setText(bundle.getString("tanggal"));
        pertanyaan.setText(bundle.getString("pertanyaan"));
        kategori.setText(bundle.getString("kategori"));
        id_konsultasi = bundle.getString("id_konsultasi");

        mItems = new ArrayList<>();
        mAdapter = new AdapterTanggapan(mItems, getApplicationContext());
        recycle_komentar.setAdapter(mAdapter);
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

        fab_kirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_isi_tanggapan.getText().toString().isEmpty()){
                    txt_isi_tanggapan.setError("Tidak dapat mengirimkan tanggapan kosong");
                }else{
                    kirim_tanggapan(id_konsultasi, txt_isi_tanggapan.getText().toString());
                }
            }
        });
    }

    private void set_loading(){
        progress.setVisibility(View.VISIBLE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
        recycle_komentar.setVisibility(View.GONE);
    }

    private void load_success(){
        progress.setVisibility(View.GONE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.GONE);
        recycle_komentar.setVisibility(View.VISIBLE);
    }

    private void load_fail(){
        progress.setVisibility(View.GONE);
        layout_koneksi.setVisibility(View.VISIBLE);
        layout_kosong.setVisibility(View.GONE);
        recycle_komentar.setVisibility(View.GONE);
    }

    private void load_empty(){
        progress.setVisibility(View.GONE);
        layout_koneksi.setVisibility(View.GONE);
        layout_kosong.setVisibility(View.VISIBLE);
        recycle_komentar.setVisibility(View.GONE);
        txt_kosong.setText("Belum ada tanggapan.");
    }

    private void getData(){
        set_loading();
        JsonArrayRequest requestData = new JsonArrayRequest(Request.Method.POST, ServerApi.tanggapan+id_konsultasi, null,
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
                                    ModelTanggapan md = new ModelTanggapan();
                                    md.setId_konsultasi(data.getString("id_konsultasi"));
                                    md.setWaktu(data.getString("waktu"));
                                    md.setIsi(data.getString("isi"));
                                    md.setNama(data.getString("nama"));
                                    mItems.add(md);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            mAdapter.notifyDataSetChanged();
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

    private void kirim_tanggapan(final String id_laporan, final String isi){
        dialog.show();
        // Tag biasanya digunakan ketika ingin membatalkan request volley
        String tag_string_req = "req_kirim_aduan";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerApi.kirim_tanggapan, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("volley", "kirim aduan Response: " + response.toString());
                dialog.dismiss();
                try
                {
                    JSONObject data = new JSONObject(response);
                    String code = data.getString("status");
                    // ngecek node error dari api
                    if (code.equals("1")) {
                        mItems.clear();
                        getData();
                        txt_isi_tanggapan.setText("");
                    }else if(code.equals("0")) {
                        // terjadi error dan tampilkan pesan error dari API
//                        dialog.hide();
                        snackBar("Gagal menfirimkan tanggapan", R.color.error);
                    }else{
                        snackBar("Sepertinya ada yang salah dengan koneksi anda", R.color.error);
                    }
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley", "Login Error: " + error.getMessage());
                dialog.dismiss();
                //cek error timeout, noconnection dan network error
                if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                    snackBar("Sepertinya ada yang salah dengan koneksi anda", R.color.error);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // kirim parameter ke server
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_konsultasi", id_konsultasi);
                params.put("isi", isi);

                return params;
            }
        };
        // menggunakan fungsi volley adrequest yang kita taro di appcontroller
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void snackBar(String pesan, int warna){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), pesan, Snackbar.LENGTH_LONG)
                .setAction("Action", null);
        View sbView = snackbar.getView();
        sbView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), warna));
        snackbar.show();
    }

    private void hapus_laporan(){
        dialog_hapus.show();
        JsonObjectRequest requestData = new JsonObjectRequest(Request.Method.POST, ServerApi.hapus_konsultasi+id_konsultasi, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("volley", "response : "+response.toString());
                        try {
                            if (response.getString("status").equals("1")){
                                finish();
                            }else{
                                snackBar("Laporan gagal dihapus", R.color.error);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            snackBar("Laporan gagal dihapus", R.color.error);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("volley", "error : "+error.getMessage());
                        if ( error instanceof TimeoutError || error instanceof NoConnectionError ||error instanceof NetworkError) {
                            snackBar("Periksa koneksi internet anda", R.color.error);
                        }else{
                            snackBar("Periksa koneksi internet anda", R.color.error);
                        }
                    }
                });
        AppController.getInstance().addToRequestQueue(requestData);
    }
}