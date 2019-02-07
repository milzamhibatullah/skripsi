package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;
import com.v1.foundspecs.milzam.skripsiapp.Class.User;
import com.v1.foundspecs.milzam.skripsiapp.Interface.ApiMethodInterface;
import com.v1.foundspecs.milzam.skripsiapp.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    private Button ubah;
    private ArrayList<User> data;
    private TextView tvname,tvemail,tvaddress,tvponsel;
    private String name,email,address,ponsel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.appColor));
        getSupportActionBar().setTitle("Profile");
        initialize();
        if (preferences.getString(Constants.SETUPPROFILE,"").equals("yes")){
            setUpProfile();
        }

        setPrefs();
        setPreview();
        ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogConfirm();
            }
        });
    }

    private void setPreview() {
        tvname.setText(name);
        tvemail.setText(email);
        tvponsel.setText(ponsel);
        tvaddress.setText(address);
    }

    private void setPrefs(){

        name=preferences.getString(Constants.NAME,"");
        email=preferences.getString(Constants.EMAIL,"");
        ponsel=preferences.getString(Constants.PONSEL,"");
        address=preferences.getString(Constants.ADDRESS,"");
    }

    private void initialize() {
        preferences=getApplicationContext().getSharedPreferences("context",0);
        tvname=(TextView)findViewById(R.id.tvProfileName);
        tvemail=(TextView)findViewById(R.id.tvProfileEmail);
        tvponsel=(TextView)findViewById(R.id.tvProfilePonsel);
        tvaddress=(TextView)findViewById(R.id.tvProfileAddress);
        ubah=(Button)findViewById(R.id.btnUbahProfile);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setPrefs();
        setPreview();
    }

    private void dialogConfirm(){
        AlertDialog.Builder builder=new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Konfirmasi")
                .setMessage("Yakin anda ingin ubah profile ?")
                .setCancelable(false)
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Ubah", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gotoSettings();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void gotoSettings() {
    }

    private void setUpProfile(){
        final ProgressDialog progressDialog=new ProgressDialog(ProfileActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Menyesuaikan data profile");
        progressDialog.show();
        //interceptor untuk cek request dan response data;
       HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
       interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
       OkHttpClient.Builder httpClient=new OkHttpClient.Builder();
       httpClient.addInterceptor(interceptor);

       Retrofit retrofit=new Retrofit.Builder()
               .baseUrl(Constants.BASE_URL_API)
               .addConverterFactory(GsonConverterFactory.create())
               .client(httpClient.build())
               .build();

       ApiMethodInterface requset=retrofit.create(ApiMethodInterface.class);
       Call<ServerResponse> responseCall=requset.send("details","application/json",
               preferences.getString(Constants.TOKEN_TYPE,"")+" "+preferences.getString(Constants.TOKEN,"")
               );
       responseCall.enqueue(new Callback<ServerResponse>() {
           @Override
           public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
               ServerResponse getData=response.body();
               if (response.code()==200){
                   if (progressDialog.isShowing())
                       progressDialog.dismiss();
                   data=new ArrayList<>(Arrays.asList(getData.getUsers()));
                  SharedPreferences.Editor editor=preferences.edit();
                  for (int i=0;i<data.size();i++){

                      editor.putString(Constants.ADDRESS,data.get(i).getAddress());
                      editor.putString(Constants.NAME,data.get(i).getName());
                      editor.putString(Constants.PONSEL,data.get(i).getPhone_number());
                      editor.putString(Constants.CATEGORYID,data.get(i).getCategory_id());
                      editor.putString(Constants.ROLEID,data.get(i).getRole_id());
                  }
                   editor.putString(Constants.SETUPPROFILE,"no");
                   editor.commit();
                   editor.apply();

                   onPostResume();
               }
           }

           @Override
           public void onFailure(Call<ServerResponse> call, Throwable t) {
               if (progressDialog.isShowing())
                   progressDialog.dismiss();

               Toast.makeText(ProfileActivity.this,"ada masalah",Toast.LENGTH_LONG).show();
           }
       });
    }


}
