package com.v1.foundspecs.milzam.skripsiapp.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.v1.foundspecs.milzam.skripsiapp.Class.Complaint;
import com.v1.foundspecs.milzam.skripsiapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Milzam on 11/23/2017.
 */

public class LaporanAdapter extends RecyclerView.Adapter<LaporanAdapter.ViewHolder> {
    private View view;
    private ArrayList<Complaint>data;

    public LaporanAdapter(ArrayList<Complaint>data){
        this.data=data;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_riwayat,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tanggal.setText(data.get(position).getCreated_at());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tanggal;
        public ViewHolder(View itemView) {
            super(itemView);
            tanggal=(TextView)itemView.findViewById(R.id.tvRiwayatDate);
        }
    }
}
