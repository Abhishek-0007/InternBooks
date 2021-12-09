package com.example.internbookchor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.common.api.internal.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity  {

    ApiInterface apiInterface;

    ImageView imageView;
    private TextView longit, latit;
    EasyWayLocation easyWayLocation;
    Button b, u;
    private String path;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    LocationManager locationManager;
    GoogleApiClient mGoogleApiClient;
    LocationListener locationListener;
    private static final int REQUEST_LOCATION = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longit = findViewById(R.id.longi);
        latit = findViewById(R.id.lat);


        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

        b = (Button) findViewById(R.id.btnSelectPhoto);
        u = (Button) findViewById(R.id.btnUploadPhoto);
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        imageView = findViewById(R.id.viewImage);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage(MainActivity.this);
            }
        });

        u.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(path.isEmpty())
                    {
                        Toast.makeText(MainActivity.this, "Please select image first", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        uploadImage();
                        Toast.makeText(MainActivity.this, "Uploaded successfully!", Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

//    image("https://bc-img.s3.ap-south-2.amazonaws.com");
//        longitude("213.11");

    public void uploadImage()
    {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Bookchor> call = apiInterface.uploadImage(path);
        call.enqueue(new Callback<Bookchor>() {
            @Override
            public void onResponse(Call<Bookchor> call, Response<Bookchor> response) {
                Bookchor bookchor = response.body();

                Log.d("@@Server Response: ",""+bookchor.getImagePath());
                Log.d("@@Server Response: ",""+path);
            }

            @Override
            public void onFailure(Call<Bookchor> call, Throwable t) {

            }
        });
    }
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void image(String image) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        //pass it like this

        Call<Bookchor> call = apiInterface.imageUpload(image);

        call.enqueue(new Callback<Bookchor>() {
            @Override
            public void onResponse(Call<Bookchor> call, Response<Bookchor> response) {
                Bookchor bookchor = response.body();


                Log.e("@@Resp: ", response.body().getImagePath());
            }

            @Override
            public void onFailure(Call<Bookchor> call, Throwable t) {

            }
        });
    }

    public void longitude(double longi) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Bookchor> call = apiInterface.longUpload(longi);

        call.enqueue(new Callback<Bookchor>() {
            @Override
            public void onResponse(Call<Bookchor> call, Response<Bookchor> response) {


                Log.e("@@Resp: ", response.body().getLongitude());
            }

            @Override
            public void onFailure(Call<Bookchor> call, Throwable t) {

            }
        });
    }

    public void latitude(double lat) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<Bookchor> call = apiInterface.latUpload(lat);

        call.enqueue(new Callback<Bookchor>() {
            @Override
            public void onResponse(Call<Bookchor> call, Response<Bookchor> response) {

                Log.e("@@Resp: ", response.body().getLongitude());
            }

            @Override
            public void onFailure(Call<Bookchor> call, Throwable t) {

            }
        });
    }


    private void chooseImage(Context context) {
        final CharSequence[] optionsMenu = {"Take Photo", "Choose from Gallery", "Exit"}; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (optionsMenu[i].equals("Take Photo")) {
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (optionsMenu[i].equals("Choose from Gallery")) {
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                } else if (optionsMenu[i].equals("Exit")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        path = getImageUri(this, selectedImage).toString();
                        //ImageResizer.reduceBitmapSize(selectedImage, 75000);
                        imageView.setImageBitmap(selectedImage);
                    }
                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();
                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                                cursor.close();
                            }
                        }
                    }
                    break;
            }
        }
    }

    }