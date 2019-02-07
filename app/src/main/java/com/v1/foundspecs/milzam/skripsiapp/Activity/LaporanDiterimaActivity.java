package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.v1.foundspecs.milzam.skripsiapp.Adapter.CategoriesAdapter;
import com.v1.foundspecs.milzam.skripsiapp.Adapter.LaporanAdapter;
import com.v1.foundspecs.milzam.skripsiapp.Class.Complaint;
import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;
import com.v1.foundspecs.milzam.skripsiapp.Interface.ApiMethodInterface;
import com.v1.foundspecs.milzam.skripsiapp.R;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LaporanDiterimaActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private LinearLayout linearLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private SharedPreferences preferences;
    private ArrayList<Complaint>data;
    private LaporanAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_laporan_diterima);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.appColor));
        getSupportActionBar().setTitle("Laporan Diterima");
        initView();
    }

    private void initView() {
        preferences=getApplicationContext().getSharedPreferences("context",0);
        linearLayout=(LinearLayout)findViewById(R.id.linearLaporanDiterima);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeLaporanDiterima);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerLaporanDiterima);
        setUpSwipe();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        onRefresh();
    }

    private void setUpSwipe() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));
    }

    @Override
    public void onRefresh() {
        loadData();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadData() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...please wait");
        mProgressDialog.show();
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient=new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        ApiMethodInterface request= retrofit.create(ApiMethodInterface.class);
        Call<ServerResponse> response=request.send("history",
                "application/json",
                preferences.getString(Constants.TOKEN_TYPE,"")+" "+preferences.getString(Constants.TOKEN,""));
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();
                if (response.code()==200){

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    data = new ArrayList<>(Arrays.asList(getInfo.getComplaints()));
                    if (data.size()<1){
                        Snackbar.make(linearLayout,"Belum ada data",Snackbar.LENGTH_LONG).show();
                    }else {
                        adapter=new LaporanAdapter(data);
                        recyclerView.setAdapter(adapter);
                    }


                }else if (response.code()==504)    {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Snackbar.make(linearLayout,"Cek koneksi internet",Snackbar.LENGTH_LONG) .show();
                }else if (response.code()==401) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    // refresh_token();

                }else {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Snackbar.make(linearLayout,"Server Error",Snackbar.LENGTH_LONG) .show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Snackbar.make(linearLayout,"Server Error",Snackbar.LENGTH_LONG) .show();
            }
        });
    }
}
