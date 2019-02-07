package com.v1.foundspecs.milzam.skripsiapp.Fragment;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.v1.foundspecs.milzam.skripsiapp.Activity.DashboardActivity;
import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;
import com.v1.foundspecs.milzam.skripsiapp.Interface.ApiMethodInterface;
import com.v1.foundspecs.milzam.skripsiapp.R;

import java.util.zip.Inflater;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Milzam on 11/22/2017.
 */

public class FragmentDataRegister extends Fragment{
    private View view;
    private EditText nama,pasword,ponsel,alamat;
    private SharedPreferences preferences;
    private Button next;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_data_register,container,false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        preferences=getActivity().getSharedPreferences("context",0);
        nama=(EditText)view.findViewById(R.id.dataNama);
        pasword=(EditText)view.findViewById(R.id.dataPassword);
        ponsel=(EditText)view.findViewById(R.id.dataPonsel);
        alamat=(EditText)view.findViewById(R.id.dataAlamat);
        next=(Button)view.findViewById(R.id.btnDaftar) ;
        ponsel.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(13)
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nama.getText().toString().isEmpty() ||
                        pasword.getText().toString().isEmpty() ||
                        ponsel.getText().toString().isEmpty() ||
                        alamat.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"Inputan tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }else {
                    sendData(nama.getText().toString(),pasword.getText().toString(),ponsel.getText().toString(),alamat.getText().toString());
                }
            }
        });
    }

    private void sendData(String nama,String password,String ponsel,String alamat){
        final ProgressDialog progressDialog=new ProgressDialog(getActivity());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading, please wait...");
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

        ApiMethodInterface request=retrofit.create(ApiMethodInterface.class);
        Call<ServerResponse> responseCall=request.register(
               "register",preferences.getString(Constants.EMAIL,"")
                ,nama,password,ponsel,alamat
        );

        responseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();
                if (response.code()==200){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    String loggedin="loggedIn";
                    String setup="yes";
                    SharedPreferences.Editor editor=preferences.edit();
                    editor.putString(Constants.isLoggedIn,loggedin);
                    editor.putString(Constants.SETUPPROFILE,setup);
                    editor.putString(Constants.TOKEN_TYPE,getInfo.getToken_type());
                    editor.putString(Constants.TOKEN,getInfo.getAccess_token());
                    editor.putString(Constants.REFRESH_TOKEN,getInfo.getRefresh_token());
                    editor.commit();
                    editor.apply();

                    gotoDashboard();
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

    private void gotoDashboard() {
        Intent intent=new Intent(getActivity(), DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
