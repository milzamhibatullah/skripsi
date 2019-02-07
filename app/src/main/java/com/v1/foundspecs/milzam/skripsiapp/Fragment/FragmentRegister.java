package com.v1.foundspecs.milzam.skripsiapp.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.v1.foundspecs.milzam.skripsiapp.Activity.DataRegisterActivity;
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
 * Created by Milzam on 10/27/2017.
 */

public class FragmentRegister extends Fragment {
    private View view;
    private SharedPreferences preferences;
    private Button daftar;
    private EditText email;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_register,container,false);
        initview(view);
        daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check();
            }
        });
        return view;
    }

    private void initview(View view) {
        email=(EditText)view.findViewById(R.id.etRegisterEmail);
        daftar=(Button)view.findViewById(R.id.btnRegister) ;
        preferences=getActivity().getSharedPreferences("context",0);
    }

    private void check() {
        if (email.getText().toString().isEmpty()){
            Toast.makeText(getActivity(),"Inputan tidak boleh kosong",Toast.LENGTH_SHORT).show();
        }else {
            sendData();
        }
    }

    private void sendData(){
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("check email available...");
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
        Call<ServerResponse> responseCall=requset.checkEmail("check-email",
                email.getText().toString());
        responseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.code()==200){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString(Constants.EMAIL,email.getText().toString());
                    editor.commit();
                    editor.apply();

                    gotoDataRegister();
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

    private void gotoDataRegister(){
        Intent intent=new Intent(getActivity(), DataRegisterActivity.class);
        intent.putExtra("mode","register");
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
