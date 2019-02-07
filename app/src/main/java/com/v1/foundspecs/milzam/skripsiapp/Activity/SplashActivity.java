package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.R;

public class SplashActivity extends AppCompatActivity {
    private SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setHideBarandFullScreen();
        preferences=getApplicationContext().getSharedPreferences("context",0);
        gotoLoginRegister();
    }

    private void gotoLoginRegister() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (preferences.getString(Constants.isLoggedIn,"").equals("loggedIn")){
                    Intent intent=new Intent(SplashActivity.this,DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent=new Intent(SplashActivity.this,LoginRegisterActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },3000);
    }

    private void setHideBarandFullScreen() {
        if (Build.VERSION.SDK_INT>Build.VERSION_CODES.JELLY_BEAN){

            int uiHide= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    |   View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |   View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |   View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    |   View.SYSTEM_UI_FLAG_LOW_PROFILE
                    |   View.SYSTEM_UI_FLAG_FULLSCREEN;
            getWindow().getDecorView().setSystemUiVisibility(uiHide);
            getSupportActionBar().hide();
        }else{

        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        MultiDex.install(this);
    }
}

