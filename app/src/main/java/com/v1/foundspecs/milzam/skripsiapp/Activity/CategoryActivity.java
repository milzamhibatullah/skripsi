package com.v1.foundspecs.milzam.skripsiapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.v1.foundspecs.milzam.skripsiapp.Adapter.CategoriesAdapter;
import com.v1.foundspecs.milzam.skripsiapp.Class.Category;
import com.v1.foundspecs.milzam.skripsiapp.Class.Constants;
import com.v1.foundspecs.milzam.skripsiapp.Class.ServerResponse;
import com.v1.foundspecs.milzam.skripsiapp.Interface.ApiMethodInterface;
import com.v1.foundspecs.milzam.skripsiapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CategoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,CategoriesAdapter.CallbackInterface {
    private static final String TAG ="info" ;
    private ArrayList<Category> data;
    private String kategori_id;
    private static final int type_image=1;

    private Uri fileUri=null;

    private CategoriesAdapter adapter;
    private SharedPreferences preferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private LinearLayout layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        initialize();
    }

    private void initialize() {
        preferences=getApplicationContext().getSharedPreferences("context",0);
        getSupportActionBar().setTitle(getResources().getString(R.string.pilihKategori));
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.color.appColor));
        recyclerView=(RecyclerView)findViewById(R.id.recyclerCategory);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipeCategory);
        layout=(LinearLayout)findViewById(R.id.linearCategory);
        setSwipeRefreshCustom();
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(layoutManager);
        onRefresh();
    }

    private void setSwipeRefreshCustom() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_green_dark),
                getResources().getColor(android.R.color.holo_red_dark),
                getResources().getColor(android.R.color.holo_blue_dark),
                getResources().getColor(android.R.color.holo_orange_dark));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            if (resultCode==RESULT_OK){
                System.out.println("linknya "+fileUri.toString());
                moveToUpload(true,kategori_id);
            }else if (resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(),"Canceled Image Capture",Toast.LENGTH_LONG)
                        .show();
            }
        }
    }

    private void moveToUpload(boolean isImage, String kategori_id) {
        String filePath=fileUri.getPath();
        Intent intent=new Intent(CategoryActivity.this,UploadActivity.class);
        intent.putExtra("category_id",kategori_id);
        intent.putExtra("pathFile",compressImage(filePath));
        intent.putExtra("isImage",isImage);
        startActivity(intent);
    }

    private String compressImage(String filePath){
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);
        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);
        options.inJustDecodeBounds = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
            //  load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;


        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename=filePath;
        try {
            out=new FileOutputStream(filename);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG,80,out);
            out.flush();
            out.close();
        }catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filename;

    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height/ (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;      }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    private void loadCategory(){
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...please wait");
        mProgressDialog.show();
        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder httpClient=new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor);
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(Constants.BASE_URL_API)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
        ApiMethodInterface request= retrofit.create(ApiMethodInterface.class);
        Call<ServerResponse> response=request.send("category",
                "application/json",
                preferences.getString(Constants.TOKEN_TYPE,"")+" "+preferences.getString(Constants.TOKEN,""));
        response.enqueue(new Callback<ServerResponse>() {
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response) {
                ServerResponse getInfo=response.body();
                if (response.code()==200){

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    data = new ArrayList<>(Arrays.asList(getInfo.getCategories()));
                    adapter=new CategoriesAdapter(data,CategoryActivity.this);
                    recyclerView.setAdapter(adapter);

                }else if (response.code()==504)    {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Snackbar.make(layout,"Cek koneksi internet",Snackbar.LENGTH_LONG) .show();
                }else if (response.code()==401) {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                   // refresh_token();

                }else {
                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                    Snackbar.make(layout,"Server Error",Snackbar.LENGTH_LONG) .show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Snackbar.make(layout,"Server Error",Snackbar.LENGTH_LONG) .show();
            }
        });

    }

    @Override
    public void onRefresh() {
        loadCategory();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCategorySelection(int position, String kategori_id) {
        this.kategori_id=kategori_id;
        if (CategoryActivity.this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            //open default camera
            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            fileUri =   getOutputMediaFileUri(type_image);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
            //start the image capture intent
            this.startActivityForResult(intent,100);
        }else {
            Log.e("CAMERA","Not Supported");
            Toast.makeText(CategoryActivity.this, "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //mengembalikan gambar
    private File getOutputMediaFile(int type) {
        DateFormat df = new SimpleDateFormat("ddMMyyyy-HH:mm");
        String date = df.format(Calendar.getInstance().getTime());
        // lokasi sdcard external
        File mediaStorageDirectuory = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), Constants.IMAGE_DIRECTORY);

        // Create the storage directory if it does not exist
        if (!mediaStorageDirectuory.exists()) {
            if (!mediaStorageDirectuory.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        +Constants.IMAGE_DIRECTORY + " directory");
                return null;
            }
        }

        // membuat nama
        File mediaFile;
        if (type == type_image) {
            mediaFile = new File(mediaStorageDirectuory.getPath() + File.separator
                    +date.toString().trim()+".jpg");
        }  else {
            return null;
        }

        return mediaFile;
    }
}
