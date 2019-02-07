package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.content.DialogInterface;
import android.location.Address;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.v1.foundspecs.milzam.skripsiapp.R;

public class SettingsActivity extends AppCompatActivity {
    private Button ubah;
    private EditText name,ponsel,email,address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setTitle("Ubah Profile");
        initialize();
        setPreview();
        ubah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInput();
            }
        });
    }


    private void initialize() {
        name=(EditText)findViewById(R.id.etSettingsName);
        ponsel=(EditText)findViewById(R.id.etSettingsPonsel);
        email=(EditText)findViewById(R.id.etSettingsEmail);
        address=(EditText)findViewById(R.id.etSettingsAddress);
        ubah=(Button) findViewById(R.id.btnSettings);
    }
    private void setPreview() {
        name.setText(getIntent().getStringExtra("name"));
        ponsel.setText(getIntent().getStringExtra("ponsel"));
        email.setText(getIntent().getStringExtra("email"));
        address.setText(getIntent().getStringExtra("address"));
    }
    private void checkInput(){
        if (    name.getText().toString().isEmpty() ||
                ponsel.getText().toString().isEmpty() ||
                email.getText().toString().isEmpty() ||
                address.getText().toString().isEmpty()){
            Toast.makeText(SettingsActivity.this,"Inputan tidak boleh kosong",Toast.LENGTH_LONG);
        }else {
            AlertDialog.Builder builder=new AlertDialog.Builder(SettingsActivity.this);
            builder.setTitle("Konfirmasi")
                    .setMessage("Profile akan diubah ?")
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
                            sendData(name.getText().toString(),ponsel.getText().toString(),
                                    email.getText().toString(),address.getText().toString());
                        }
                    });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
    }

    private void sendData(String name, String ponsel, String email, String address) {

    }
}
