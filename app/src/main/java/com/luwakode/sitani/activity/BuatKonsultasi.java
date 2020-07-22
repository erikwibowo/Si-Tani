package com.luwakode.sitani.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.luwakode.sitani.R;
import com.luwakode.sitani.adapter.AdapterKategori;
import com.luwakode.sitani.model.Modelkategori;
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

public class BuatKonsultasi extends BaseActivity {

    Spinner spinner_kategori;
    AlertDialog dialog;
    AdapterKategori adapter;
    List<Modelkategori> listKategori = new ArrayList<Modelkategori>();

    EditText txtjudul, txtpertanyaan;
    Button btnkirim;
    String id_user, jenis, id_kategori;
    PrefManager prefManager;
    TextView txt_hasil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buat_konsultasi);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Buat Konsultasi");

        findView();
        initView();
        initListener();
        callData();
    }


    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void findView() {
        txt_hasil = (TextView) findViewById(R.id.txt_hasil);
        spinner_kategori = (Spinner) findViewById(R.id.spinner_kategori);
        txtjudul = findViewById(R.id.txtjudul);
        txtpertanyaan = findViewById(R.id.txtpertanyaan);
        btnkirim = findViewById(R.id.btnkirim);
    }

    @Override
    public void initView() {
        prefManager = new PrefManager(getApplicationContext());
        adapter = new AdapterKategori(BuatKonsultasi.this, listKategori);
        spinner_kategori.setAdapter(adapter);
        id_user = prefManager.getIdUser();
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Sedang mengirim laporan...")
                .setCancelable(false)
                .build();
    }

    @Override
    public void initListener() {
        spinner_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                txt_hasil.setText(listKategori.get(i).getId_kategori());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnkirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtjudul.getText().toString().isEmpty()){
                    txtjudul.setError("Judul harus diisi");
                }else if(txtpertanyaan.getText().toString().isEmpty()){
                    txtpertanyaan.setError("Pertanyaan harus diisi");
                }else{
                    kirim_laporan_tanpa_foto(id_user, txtjudul.getText().toString(), txtpertanyaan.getText().toString(), txt_hasil.getText().toString());
                }
            }
        });
    }

    private void callData() {
        listKategori.clear();
        JsonArrayRequest jArr = new JsonArrayRequest(ServerApi.kategori,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.e("Response", response.toString());

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);

                                Modelkategori item = new Modelkategori();

                                item.setId_kategori(obj.getString("id_kategori"));
                                item.setNama(obj.getString("nama"));

                                listKategori.add(item);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: " + error.getMessage());
                Toast.makeText(BuatKonsultasi.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jArr);
    }

    private void kirim_laporan_tanpa_foto(final String id_user, final String judul, final String pertanyaan, final String id_kategori){
        dialog.show();
        // Tag biasanya digunakan ketika ingin membatalkan request volley
        String tag_string_req = "req_kirim_aduan";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerApi.buat_konsultasi, new Response.Listener<String>() {

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
                        AlertDialog.Builder builder = new AlertDialog.Builder(BuatKonsultasi.this);
                        builder.setMessage("Terima kasih, laporan anda telah berhasil dikirimkan.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }else if(code.equals("0")) {
                        // terjadi error dan tampilkan pesan error dari API
//                        dialog.hide();
                        snackBar("Gagal menfirimkan laporan", R.color.error);
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
                params.put("id_user", id_user);
                params.put("id_kategori", id_kategori);
                params.put("judul", judul);
                params.put("pertanyaan", pertanyaan);

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
}