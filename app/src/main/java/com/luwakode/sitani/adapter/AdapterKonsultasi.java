package com.luwakode.sitani.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luwakode.sitani.R;
import com.luwakode.sitani.activity.DetailKonsultasi;
import com.luwakode.sitani.model.ModelKonsultasi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class AdapterKonsultasi extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ModelKonsultasi> mItems;

    public AdapterKonsultasi(List<ModelKonsultasi> mItems, Context context) {
        this.mItems = mItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_konsultasi, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ModelKonsultasi md = mItems.get(position);
        final HolderData holderData = (HolderData) holder;
        holderData.judul.setText(md.getJudul());
        holderData.nama.setText(md.getNama());
        holderData.tanggal.setText(md.getWaktu()+" . "+md.getTanggapan()+" tanggapan");

        if (md.getPertanyaan().length() > 200){
            holderData.pertanyaan.setText(md.getPertanyaan().substring(0, 200)+" ...");
        }else{
            holderData.pertanyaan.setText(md.getPertanyaan());
        }

        holderData.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DetailKonsultasi.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                i.putExtra("id_konsultasi", md.getId_konsultasi());
                i.putExtra("judul", md.getJudul());
                i.putExtra("waktu", md.getWaktu());
                i.putExtra("pertanyaan", md.getPertanyaan());
                i.putExtra("kategori", md.getNama());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class HolderData extends RecyclerView.ViewHolder {
        public TextView judul, pertanyaan, tanggal, nama;
        public CardView card;
        public ImageView foto;

        public HolderData(View view) {
            super(view);
            judul = (TextView) view.findViewById(R.id.judul);
            tanggal = (TextView) view.findViewById(R.id.tanggal);
            pertanyaan = (TextView) view.findViewById(R.id.pertanyaan);
            nama = (TextView) view.findViewById(R.id.nama);
            card = (CardView) view.findViewById(R.id.card);
        }
    }
}
