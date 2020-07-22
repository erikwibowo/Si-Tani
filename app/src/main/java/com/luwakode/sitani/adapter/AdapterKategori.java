package com.luwakode.sitani.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luwakode.sitani.R;
import com.luwakode.sitani.model.Modelkategori;

import java.util.List;

public class AdapterKategori extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Modelkategori> item;

    public AdapterKategori(Activity activity, List<Modelkategori> item) {
        this.activity = activity;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int location) {
        return item.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_kategori, null);

        TextView pendidikan = (TextView) convertView.findViewById(R.id.kategori);

        Modelkategori data;
        data = item.get(position);

        pendidikan.setText(data.getNama());

        return convertView;
    }
}
