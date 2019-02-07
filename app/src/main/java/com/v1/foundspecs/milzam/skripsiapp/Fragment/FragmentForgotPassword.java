package com.v1.foundspecs.milzam.skripsiapp.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;
import com.v1.foundspecs.milzam.skripsiapp.Interface.ApiMethodInterface;
import com.v1.foundspecs.milzam.skripsiapp.R;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Milzam on 11/16/2017.
 */

public class FragmentForgotPassword extends Fragment {
    private View    view;
    private EditText email;
    private Button send;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view    =   inflater.inflate(R.layout.fragment_forgot_password,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        email=(EditText)view.findViewById(R.id.etForgotPasswordEmail);
        send=(Button)view.findViewById(R.id.btnSendVerication);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"inputan tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }else {
                    sendData(email.getText().toString());
                }
            }
        });
    }

    private void sendData(String email) {
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("request link,please wait...");
        progressDialog.show();

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
        Call<ServerResponse> responseCall=requset.checkEmail("request-link",
                email);
        responseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();
                if (response.code()==200){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();


                    if (getInfo.isSuccess()==true){
                        Toast.makeText(getActivity(),"Cek email anda", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getActivity(),"Email anda tidak terdaftar", Toast.LENGTH_LONG).show();
                    }
                }else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });
    }
}
