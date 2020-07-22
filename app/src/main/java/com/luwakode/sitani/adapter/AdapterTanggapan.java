package com.luwakode.sitani.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.luwakode.sitani.R;
import com.luwakode.sitani.model.ModelTanggapan;

import java.util.List;

public class AdapterTanggapan extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<ModelTanggapan> mItems;

    public AdapterTanggapan(List<ModelTanggapan> mItems, Context context) {
        this.mItems = mItems;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_tanggapan, parent, false);
        return new HolderData(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final ModelTanggapan md = mItems.get(position);
        final HolderData holderData = (HolderData) holder;
        holderData.nama.setText(md.getNama());
        holderData.isi.setText(md.getIsi());
        holderData.tanggal.setText(md.getWaktu());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    private class HolderData extends RecyclerView.ViewHolder {
        public TextView judul, isi, tanggal, nama;

        public HolderData(View view) {
            super(view);
            judul = (TextView) view.findViewById(R.id.judul);
            tanggal = (TextView) view.findViewById(R.id.waktu);
            isi = (TextView) view.findViewById(R.id.isi);
            nama = (TextView) view.findViewById(R.id.nama);
        }
    }
}
