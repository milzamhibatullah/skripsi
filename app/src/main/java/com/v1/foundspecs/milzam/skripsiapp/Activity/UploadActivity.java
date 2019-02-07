package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerRequest;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;
import com.v1.foundspecs.milzam.skripsiapp.Interface.ApiMethodInterface;
import com.v1.foundspecs.milzam.skripsiapp.R;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UploadActivity extends AppCompatActivity implements com.google.android.gms.location.LocationListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    GoogleApiClient gac;
    Location locate;
    LocationRequest locationRequest;
    private SharedPreferences preferences;
    private ImageView img;
    private Boolean status=false;
    private Button upload;
    private double longi=0,latt=0;
    final String TAG = "GPS";
    private long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private String category_id,alamat,kota,kecamatan,keterangan;
    private EditText ket;
    private String filePath=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.appColor));
        getSupportActionBar().setTitle("Pengaduan");
        checkGoogleService();
        if (!isLocationEnabled())
            showAllert();
        initializeGac();
        initalize();
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ket.getText().toString().isEmpty()){
                    Toast.makeText(UploadActivity.this,"Keterangan tidak boleh kosong",Toast.LENGTH_LONG).show();
                }else {
                    AlertDialog.Builder builder=new AlertDialog.Builder(UploadActivity.this);
                    builder.setTitle("Konfirmasi")
                            .setMessage("Apakah data yang diinputkan sudah benar")
                            .setCancelable(false)
                            .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    keterangan=ket.getText().toString();
                                    getLocationInfo();
                                    if (status.equals(false)){
                                        Toast.makeText(UploadActivity.this,"null data",Toast.LENGTH_SHORT).show();}
                                    else {
                                        sendData();
                                    }
                                }
                            });
                    AlertDialog dialog=builder.create();
                    dialog.show();


                }

            }
        });
    }

    private void getLocationInfo() {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(locate.getLatitude(), locate.getLongitude(), 1);
            if (addresses.size()<=0){
                status=false;

            }else {

                alamat = addresses.get(0).getAddressLine(0);
                kecamatan=addresses.get(0).getLocality();
                kota=addresses.get(0).getAdminArea();
                latt=locate.getLatitude();
                longi=locate.getLongitude();
                status=true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initalize() {
        preferences=getApplicationContext().getSharedPreferences("context",0);
        img=(ImageView)findViewById(R.id.imgUpload);
        ket=(EditText)findViewById(R.id.etKetUpload);
        upload=(Button) findViewById(R.id.btnUpload);
        getIntentItem();
    }

    private void getIntentItem() {
        Intent i = getIntent();
        filePath = i.getStringExtra("pathFile");
        category_id = i.getStringExtra("category_id");
        boolean isImage = i.getBooleanExtra("isImage", true);
        if (filePath != null) {
            displayImage(isImage);
        } else {
            Toast.makeText(getApplicationContext(), "Sorry, File Path is Missing", Toast.LENGTH_LONG)
                    .show();
        }
    }

    private void displayImage(boolean isImage) {
        if (isImage){
            img.setVisibility(View.VISIBLE);
            Bitmap bitma,scaled;
            bitma= BitmapFactory.decodeFile(filePath);
            int fixedSize=(int)(bitma.getHeight()*(512.0/bitma.getWidth()));
            scaled=Bitmap.createScaledBitmap(bitma,512,fixedSize,true);
            img.setImageBitmap(scaled);
        }
    }

    private void initializeGac() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        gac = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private boolean checkGoogleService() {
        final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS){
            if (apiAvailability.isUserResolvableError(resultCode)){
                apiAvailability.getErrorDialog(this,resultCode,PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            }else {
                Log.d(TAG,"this device not supported");
                finish();
            }
            return false;
        }
        Log.d(TAG, "This device is supported.");
        return true;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
    private void showAllert(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Locations Settings is set to 'Off'.\nPlease Enable Location to " +
                        "use this app")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                        Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {

                    }
                });
        dialog.show();
    }



    private void sendData(){
        final ProgressDialog progressDialog=new ProgressDialog(UploadActivity.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle("Loading");
        progressDialog.setMessage("pleasewaite....");
        progressDialog.show();
        System.out.println("ALAMAT" +alamat);
        HttpLoggingInterceptor loggingInterceptor=new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient=new OkHttpClient.Builder();
        httpClient.addInterceptor(loggingInterceptor);

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        File file=new File(filePath);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part image=MultipartBody.Part.createFormData("image",file.getName(),requestBody);
        RequestBody id_kategori=RequestBody.create(MediaType.parse("text/plain"),category_id);
        RequestBody lattitude=RequestBody.create(MediaType.parse("text/plain"),String.valueOf(latt));
        RequestBody longitude=RequestBody.create(MediaType.parse("text/plain"),String.valueOf(longi));
        RequestBody address=RequestBody.create(MediaType.parse("text/plain"),alamat.toString());
        RequestBody city=RequestBody.create(MediaType.parse("text/plain"),kota.toString());
        RequestBody subdistrict=RequestBody.create(MediaType.parse("text/plain"),kecamatan.toString());
        RequestBody description=RequestBody.create(MediaType.parse("text/plain"),keterangan.toString());

        ApiMethodInterface request=retrofit.create(ApiMethodInterface.class);
        Call<ServerResponse> response=request.uploadImage("complaint","application/json"
                                                            ,preferences.getString(Constants.TOKEN_TYPE,"")+" "
                                                                +preferences.getString(Constants.TOKEN,"")
                                                            ,image,id_kategori,address,city,subdistrict,longitude,lattitude
                                                            ,description);
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();
                if (response.code()==200){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();

                    AlertDialog.Builder builder=new AlertDialog.Builder(UploadActivity.this);
                    builder.setTitle("Berhasil")
                            .setMessage("Pengaduan anda berhasil dilaporkan")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    gotoDashboard();
                                }
                            });
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }else if (response.code()==401){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this,"Unauthorized",Toast.LENGTH_LONG).show();
                }else if (response.code()==504){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this,"Cek koneksi internet",Toast.LENGTH_LONG).show();
                }else if (response.code()==400){
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this,"Ada yang salah",Toast.LENGTH_LONG).show();
                }else {
                    if (progressDialog.isShowing())
                        progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this,"server Error",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                    Toast.makeText(UploadActivity.this,"server Error",Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    protected void onStart() {
        gac.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        gac.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location !=null){
            Log.d(TAG, "updateLocation");
            updateLocation(location);
        }
    }

    private void updateLocation(Location loc){
        this.locate=loc;

    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        Log.d(TAG, "onConnected");

        Location ll = LocationServices.FusedLocationApi.getLastLocation(gac);
        Log.d(TAG, "LastLocation: " + (ll == null ? "NO LastLocation" : ll.toString()));

        LocationServices.FusedLocationApi.requestLocationUpdates(gac, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(UploadActivity.this, "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
        Log.d("DDD", connectionResult.toString());
    }

    private void gotoDashboard(){
        gac.disconnect();
        Intent intent =new Intent(UploadActivity.this,DashboardActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
        this.finish();
    }
}
