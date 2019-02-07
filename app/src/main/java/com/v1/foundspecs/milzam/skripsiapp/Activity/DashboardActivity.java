package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.v1.foundspecs.milzam.skripsiapp.Adapter.FeedsAdapter;
import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.Feeds;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;
import com.v1.foundspecs.milzam.skripsiapp.Interface.ApiMethodInterface;
import com.v1.foundspecs.milzam.skripsiapp.Interface.Refresh_Token;
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

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SwipeRefreshLayout.OnRefreshListener,Refresh_Token {
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private FeedsAdapter adapter;
    private ArrayList<Feeds> data;
    private View header;
    private TextView email;
    private Boolean logoutStatus=false;
    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layout;

    private DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initialize();
    }

    private void initialize() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        preferences=getApplicationContext().getSharedPreferences("context",0);
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.appColor));
        getSupportActionBar().setTitle("Sipemaku");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        recyclerView=(RecyclerView)findViewById(R.id.recyclerDashboard);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeDashboard);
        layout=(LinearLayout)findViewById(R.id.linearDashboard);
        SwipeRefreshLayoutSetting();
        header=navigationView.getHeaderView(0);

        email=(TextView) header.findViewById(R.id.tvDashboardEmail);
        setAccountDrawer();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        onRefresh();

    }


    private void SwipeRefreshLayoutSetting(){
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));
    }


    private void setAccountDrawer() {
        email.setText(preferences.getString(Constants.EMAIL,""));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    private void loadFeeds(){
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(logging);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        ApiMethodInterface request=retrofit.create(ApiMethodInterface.class);
        Call<ServerResponse> response=request.send("feeds",
                "application/json",
                preferences.getString(Constants.TOKEN_TYPE,"")+" "+preferences.getString(Constants.TOKEN,""));
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();

                if (response.code()==200){

                    data = new ArrayList<>(Arrays.asList(getInfo.getFeeds()));
                    if (data.size()<1){
                        Snackbar.make(layout,"Belum ada data",Snackbar.LENGTH_LONG).show();
                    }else {
                        adapter=new FeedsAdapter(data);
                        recyclerView.setAdapter(adapter);
                    }

                }else if (response.code()==401){
                  //  refresh_token(false);
                }else {

                    Snackbar.make(layout,"ServerError",Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {


                Snackbar.make(layout,"ServerError",Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.addReport) {
            Intent intent=new Intent(DashboardActivity.this,CategoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_slideshow) {
            Intent intent=new Intent(DashboardActivity.this,LaporanDiterimaActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent=new Intent(DashboardActivity.this,ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.logout) {
            AlertDialog.Builder builder=new AlertDialog.Builder(DashboardActivity.this);
            builder.setTitle(getResources().getString(R.string.konfirm));
            builder.setMessage(getResources().getString(R.string.logoutConfirm))
                    .setCancelable(false)
                    .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    logoutStatus=true;
                    logoutApp();
                }
            });
            AlertDialog dialog=builder.create();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutApp() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...please wait");
        mProgressDialog.show();
        HttpLoggingInterceptor logging=new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient= new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        ApiMethodInterface request= retrofit.create(ApiMethodInterface.class);
        Call<ServerResponse> response=request.send("logout",
                "application/json",
                preferences.getString(Constants.TOKEN_TYPE,"")+" "+preferences.getString(Constants.TOKEN,""));
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();
                if (response.code()==200){
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.clear();
                    editor.commit();
                    editor.apply();
                    goToLogin();
                }else if (response.code()==401){
                    refresh_token();
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();
                }else if (response.code()==504){
                    Toast.makeText(DashboardActivity.this,"Cek koneksi internet",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(DashboardActivity.this,"Server Maintenance",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this,"Server Maintenance",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToLogin() {
        Intent intent=new Intent(DashboardActivity.this,LoginRegisterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRefresh() {
        loadFeeds();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        onRefresh();
    }

    @Override
    public void refresh_token() {
        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(logging);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        ApiMethodInterface request= retrofit.create(ApiMethodInterface.class);
        Call<ServerResponse> response=request.refresh("refresh",preferences.getString(Constants.REFRESH_TOKEN,""));
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();
                if (response.code()==200){
                    if (logoutStatus.equals(false)){
                        SharedPreferences.Editor editor=preferences.edit();
                        editor.putString(Constants.TOKEN,getInfo.getAccess_token());
                        editor.putString(Constants.REFRESH_TOKEN,getInfo.getRefresh_token());
                        editor.putString(Constants.TOKEN_TYPE,getInfo.getToken_type());
                        editor.commit();
                        editor.apply();
                        onPostResume();;
                    }else{
                        logoutApp();
                    }

                }else if (response.code()==401){
                    login();
                }else if (response.code()==504){
                    Toast.makeText(DashboardActivity.this,"Cek koneksi internet anda",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(DashboardActivity.this,"Server Maintenance",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this,"Server Maintenance",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void login() {
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor();

        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder client=new OkHttpClient.Builder();
        client.addInterceptor(loggingInterceptor);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();

        ApiMethodInterface request=retrofit.create(ApiMethodInterface.class);
        Call<ServerResponse> response=request.login(Constants.LOGIN,preferences.getString(Constants.EMAIL,""),
                                        preferences.getString(Constants.PASSWORD,""));
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();
                if (response.code()==200){
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString(Constants.TOKEN,getInfo.getAccess_token());
                    editor.putString(Constants.REFRESH_TOKEN,getInfo.getRefresh_token());
                    editor.putString(Constants.TOKEN_TYPE,getInfo.getToken_type());
                    editor.commit();
                    editor.apply();
                    onPostResume();
                }else if (response.code()==504){
                    Toast.makeText(DashboardActivity.this,"Cek Koneksi Internet Anda",Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(DashboardActivity.this,"Server Maintenance",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                Toast.makeText(DashboardActivity.this,"Server Maintenance",Toast.LENGTH_LONG).show();
            }
        });
    }
}
