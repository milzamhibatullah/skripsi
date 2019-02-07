package com.v1.foundspecs.milzam.skripsiapp.Fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.v1.foundspecs.milzam.skripsiapp.Activity.LoginRegisterActivity;
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

public class FragmentResetPassword extends Fragment {
    private View view;
    private EditText password,konfirmpassword;
    private SharedPreferences preferences;
    private Button button;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_reset_password,container,false);
        init(view);
        return view;
    }

    private void init(View view) {
        preferences=getActivity().getSharedPreferences("context",0);
        password=(EditText)view.findViewById(R.id.newPassword);
        konfirmpassword=(EditText)view.findViewById(R.id.konfirmPassword);
        button=(Button)view.findViewById(R.id.btnResetPassword);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().isEmpty() || konfirmpassword.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(),"inputan tidak boleh kosong",Toast.LENGTH_SHORT).show();
                }else if (password.getText().toString().equals(konfirmpassword.getText().toString())){
                    sendData(password.getText().toString());

                }else {
                    Toast.makeText(getActivity(),"Password tidak sesuai",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendData(String password) {
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
        Call<ServerResponse> responseCall=request.reset("reset-password",
                preferences.getString(Constants.EMAIL,""),
                password);
        responseCall.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                if (response.code()==200){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    dialogSuccess();
                }else if (response.code()==401){
                    Toast.makeText(getActivity(),"Email sudah terdaftar",Toast.LENGTH_LONG).show();
                }else{
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

    private void dialogSuccess() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle("Berhasil")
                .setMessage("Password anda berhasil diubah")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        gotoLogin();
                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void gotoLogin() {
        Intent intent=new Intent(getActivity(), LoginRegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getActivity().startActivity(intent);
        getActivity().finish();
    }
}
