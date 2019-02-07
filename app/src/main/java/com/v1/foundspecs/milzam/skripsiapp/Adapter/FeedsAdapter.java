package com.v1.foundspecs.milzam.skripsiapp.Adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.Feeds;
import com.v1.foundspecs.milzam.skripsiapp.R;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Milzam on 10/8/2017.
 */

public class FeedsAdapter extends RecyclerView.Adapter<FeedsAdapter.ViewHolder> {
    private ArrayList<Feeds> feedses;
    private Date tanggal = null;
    private Uri fileUri;
    private static final int type_image=1;

    private View view;

    public FeedsAdapter(ArrayList<Feeds> feeds){
        feedses=feeds;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_feeds,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        SimpleDateFormat month_date = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try {
            tanggal=sdf.parse(feedses.get(position).getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String month_name=month_date.format(tanggal);

        holder.description.setText(feedses.get(position).getDescription());
        holder.description.setMaxLines(8);

        holder.date.setText(month_name);
        Picasso.with(view.getContext()).load(Constants.BASE_URL_RESOURCE+feedses.get(position).getImage())
                .noFade().error(R.drawable.cantload).into(holder.img, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.GONE);
            }
        });
     //   holder.name.setText(feedses.get(position).getUser_name());

    }

    @Override
    public int getItemCount() {
        return feedses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView description,name,date,readmore;
        private ProgressBar progressBar;
        private ImageView img;
        public ViewHolder(View itemView) {
            super(itemView);
            description=(TextView)itemView.findViewById(R.id.feedsDescription);
            //readmore=(TextView) itemView.findViewById(R.id.feedsReadmore);
            date=(TextView) itemView.findViewById(R.id.feedsDate);
            //name=(TextView) itemView.findViewById(R.id.feedsUserName);
            progressBar=(ProgressBar) itemView.findViewById(R.id.feedsProgress);
            img=(ImageView) itemView.findViewById(R.id.feedsImg);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
