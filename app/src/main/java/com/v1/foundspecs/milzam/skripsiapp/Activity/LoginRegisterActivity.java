package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.v1.foundspecs.milzam.skripsiapp.Fragment.FragmentLogin;
import com.v1.foundspecs.milzam.skripsiapp.R;

public class LoginRegisterActivity extends AppCompatActivity {
    private Fragment fragment;
    private FragmentTransaction ft;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        getSupportActionBar().hide();
        callFragmentLogin();
    }

    private void callFragmentLogin() {
        fragment=new FragmentLogin();
        ft=getFragmentManager().beginTransaction();
        ft.replace(R.id.frameLoginRegister,fragment);
        ft.commit();
    }


}
