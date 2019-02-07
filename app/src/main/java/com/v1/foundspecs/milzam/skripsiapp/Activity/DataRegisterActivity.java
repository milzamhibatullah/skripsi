package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Fragment.FragmentDataRegister;
import com.v1.foundspecs.milzam.skripsiapp.Fragment.FragmentLogin;
import com.v1.foundspecs.milzam.skripsiapp.Fragment.FragmentResetPassword;
import com.v1.foundspecs.milzam.skripsiapp.R;

public class DataRegisterActivity extends AppCompatActivity {
    private Fragment fragment;
    private SharedPreferences preferences;
    private Intent intent;
    private FragmentTransaction ft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_register);
        getSupportActionBar().hide();
        preferences=getApplicationContext().getSharedPreferences("context",0);
        intent=getIntent();
        Uri data=intent.getData();
        if (data!=null){
        if (data.getQueryParameter("mode").toString().equals("reset")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(Constants.EMAIL, intent.getData().getQueryParameter("email"));
            editor.apply();

            callFragmentResetPassword();
        }}else{
            if (getIntent().getStringExtra("mode").equals("register")){
                callFragmentDataRegister();
            }
        }

    }

    private void callFragmentResetPassword() {
        fragment=new FragmentResetPassword();
        ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.frameDataRegister,fragment);
        ft.commit();
    }

    private void callFragmentDataRegister() {
        fragment=new FragmentDataRegister();
        ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.frameDataRegister,fragment);
        ft.commit();
    }
}
