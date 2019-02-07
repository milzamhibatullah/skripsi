package com.v1.foundspecs.milzam.skripsiapp.Fragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.v1.foundspecs.milzam.skripsiapp.Activity.DashboardActivity;
import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;
import com.v1.foundspecs.milzam.skripsiapp.Interface.ApiMethodInterface;
import com.v1.foundspecs.milzam.skripsiapp.Interface.Refresh_Token;
import com.v1.foundspecs.milzam.skripsiapp.R;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Milzam on 10/27/2017.
 */

public class FragmentLogin extends Fragment implements View.OnClickListener{
    private View view;
    private EditText email,password;
    private ProgressBar progressBar;
    private SharedPreferences preferences;
    private Button btnLogin,btnLupaPassword;
    private Fragment fragment;
    private FragmentTransaction ft;
    private TextView daftar;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_login,container,false);
        initialize(view);

        return view;
    }

    private void callFragmentRegister() {
        fragment=new FragmentRegister();
        ft=getActivity().getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        ft.addToBackStack(null);
        ft.replace(R.id.frameLoginRegister,fragment);
        ft.commit();
    }

    private void initialize(View view) {
        preferences=getActivity().getSharedPreferences("context",0);
        daftar=(TextView)view.findViewById(R.id.tvFragmentRegister);
        email=(EditText)view.findViewById(R.id.etFragmentEmail);
        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        password=(EditText)view.findViewById(R.id.etFragmentPassword);
        btnLogin=(Button) view.findViewById(R.id.btnLogin);
        btnLupaPassword=(Button) view.findViewById(R.id.btnLupapassword);
        daftar.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnLupaPassword.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnLogin:
                    checkLogin(email.getText().toString(),password.getText().toString());
                break;
            case R.id.tvFragmentRegister:
                callFragmentRegister();
                break;
            case R.id.btnLupapassword:
                callFragmentForgotPassword();;

        }
    }

    private void callFragmentForgotPassword() {
        fragment=new FragmentForgotPassword();
        ft=getActivity().getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        ft.addToBackStack(null);
        ft.replace(R.id.frameLoginRegister,fragment);
        ft.commit();

    }

    private void checkLogin(final String email, final String password) {
        progressBar.setVisibility(View.VISIBLE);
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
        ApiMethodInterface requestIterface=retrofit.create(ApiMethodInterface.class);
        final Call<ServerResponse> responseCall=requestIterface.login(Constants.LOGIN,email,password);
        responseCall.enqueue(new retrofit2.Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, retrofit2.Response<ServerResponse> response) {
                ServerResponse getResponse=response.body();

                if (response.code()==401){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"Cek username dan password anda",Toast.LENGTH_LONG).show();
                }else if (response.code()==400){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"Username dan password tidak boleh kosong",Toast.LENGTH_LONG).show();
                }else if (response.code()==504){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getActivity(),"Cek koneksi internet anda",Toast.LENGTH_LONG).show();
                }else if (response.code()==200){
                    String logged="loggedIn";
                    String setup="yes";
                    progressBar.setVisibility(View.GONE);
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString(Constants.isLoggedIn,logged);
                    editor.putString(Constants.EMAIL,email);
                    editor.putString(Constants.SETUPPROFILE,setup);
                    editor.putString(Constants.PASSWORD,password);
                    editor.putString(Constants.TOKEN,getResponse.getAccess_token());
                    editor.putString(Constants.REFRESH_TOKEN,getResponse.getRefresh_token());
                    editor.putString(Constants.TOKEN_TYPE,getResponse.getToken_type());
                    editor.commit();
                    editor.apply();

                   gotoDashboard();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(),"Server dalam maintenance",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void gotoDashboard() {
        Intent intent=new Intent(getActivity(), DashboardActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }




}
