package com.luwakode.sitani.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.snackbar.Snackbar;
import com.luwakode.sitani.R;
import com.luwakode.sitani.server.ServerApi;
import com.luwakode.sitani.util.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;

public class Daftar extends BaseActivity {

    EditText txtnama, txtusername, txtnohp, txtalamat, txtpassword;
    Button btndaftar;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
        setContentView(R.layout.activity_daftar);
        getSupportActionBar().hide();
        findView();
        initView();
        initListener();
    }

    @Override
    public void findView() {
        txtnama = findViewById(R.id.txtnama);
        txtusername = findViewById(R.id.txtusername);
        txtnohp = findViewById(R.id.txtnohp);
        txtalamat = findViewById(R.id.txtalamat);
        txtpassword = findViewById(R.id.txtpassword);

        btndaftar = findViewById(R.id.btndaftar);
    }

    @Override
    public void initView() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Sedang membuat akun...")
                .setCancelable(false)
                .build();
    }

    @Override
    public void initListener() {
        btndaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtnama.getText().toString().isEmpty()){
                    txtnama.setError("Nama Lengkap harus diisi");
                }else if (txtusername.getText().toString().isEmpty()){
                    txtusername.setError("Username harus diisi");
                }else if (txtnohp.getText().toString().isEmpty()){
                    txtnohp.setError("No. Telepon harus diisi");
                }else if (txtalamat.getText().toString().isEmpty()){
                    txtalamat.setError("Alamat Lengkap harus diisi");
                }else if (txtpassword.getText().toString().isEmpty()){
                    txtpassword.setError("Password Lengkap harus diisi");
                }else{
                    daftar(txtnama.getText().toString(), txtusername.getText().toString(), txtnohp.getText().toString(), txtalamat.getText().toString(), txtpassword.getText().toString());
                }
            }
        });
    }

    private void daftar(final String nama, final String username, final String nohp, final String alamat, final String password) {
        dialog.show();
        // Tag biasanya digunakan ketika ingin membatalkan request volley
        String tag_string_req = "req_login";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                ServerApi.daftar, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("volley", "Daftar Response: " + response.toString());

                try
                {
                    JSONObject data = new JSONObject(response);
                    String code = data.getString("status");
                    dialog.dismiss();
                    // ngecek node error dari api
                    if (code.equals("0")){
                        snackBar(data.getString("pesan"), R.color.error);
                    }else if (code.equals("1")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Daftar.this);
                        builder.setMessage("Akun telah berhasil  didahtarkan. Silahkan login ke akun anda.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }else{
                        snackBar("Sepertinya ada yang salah dengan koneksi anda", R.color.error);
                    }
                } catch (JSONException e) {
                    // JSON error
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
                params.put("nama", nama);
                params.put("no_hp", nohp);
                params.put("alamat", alamat);
                params.put("username", username);
                params.put("password", password);

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