package com.luwakode.sitani.server;

public class ServerApi {
    private static final String site_url = "https://sitani.luwakode.com/api/";
    private static final String web_url = "https://sitani.luwakode.com/webview/#/app/";

    public static final String kategori = site_url+"kategori.php";
    public static final String login = site_url+"login.php";
    public static final String daftar = site_url+"buat-akun.php";
    public static final String kirim_tanggapan = site_url+"kirim-tanggapan.php";
    public static final String hapus_konsultasi = site_url+"hapus-konsultasi.php?id=";
    public static final String buat_konsultasi = site_url+"buat-konsultasi.php";
    public static final String profil = site_url+"profil.php?id=";
    public static final String konsultasi = site_url+"konsultasi.php?id=";
    public static final String tanggapan = site_url+"tanggapan.php?id=";

    public  static final String artikel = web_url+"artikel";
    public  static final String pupuk = web_url+"pupuk";
    public  static final String hama = web_url+"hama";
    public  static final String harga = web_url+"harga";
}
