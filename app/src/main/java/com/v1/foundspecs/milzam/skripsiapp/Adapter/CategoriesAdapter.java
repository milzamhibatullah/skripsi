package com.v1.foundspecs.milzam.skripsiapp.Adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.v1.foundspecs.milzam.skripsiapp.Class.Category;
import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.R;


import java.util.ArrayList;

/**
 * Created by Milzam on 10/9/2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private static final String TAG = "Adapter.Info";
    private ArrayList<Category> categories;
    private CallbackInterface callback;
    private View view;
    private Uri fileUri;
    private Context context;
    private static final int type_image=1;

    public interface CallbackInterface{
        void onCategorySelection(int position, String kategori_id);
    }


    public CategoriesAdapter(ArrayList<Category> categories, Context context){
        this.categories=categories;
        this.context=context;

        try{
            callback = (CallbackInterface) context;
        }catch(ClassCastException ex){
            //.. should log the error or throw and exception
            Log.e("MyAdapter","Must implement the CallbackInterface in the Activity", ex);
        }

    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Picasso.with(view.getContext()).load(Constants.BASE_URL_RESOURCE+categories.get(position).getIcon())
                .noFade().error(R.drawable.cantload).skipMemoryCache()
                .into(holder.img);
        holder.name.setText(categories.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback !=null){
                    callback.onCategorySelection(position,categories.get(position).getId());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private ImageView img;
        public ViewHolder(final View itemView) {
            super(itemView);
            name=(TextView)itemView.findViewById(R.id.name_category);
            img= (ImageView) itemView.findViewById(R.id.img_category);

        }
    }


}
