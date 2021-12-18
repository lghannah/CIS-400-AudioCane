package com.example.groupproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.chaquo.python.PyObject;
import com.chaquo.python.android.AndroidPlatform;
import com.chaquo.python.Python;

import okhttp3.OkHttpClient;

import com.getvoice.speech.restapi.common.DemoException;
import com.getvoice.speech.restapi.ttsdemo.TtsMain;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {
    private String TAG = "Developer--------";
    private OkHttpClient okHttpClient;
    private byte[] bytes;
    private Python py;

    private ImageView imageView;
    private TextView txt;

    void initPython() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        py = Python.getInstance();
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPython();
//        init all the items
        Button btn = findViewById(R.id.btn);
        Button btn_stop = findViewById(R.id.btn_stop);
        Button btn_image = findViewById(R.id.btn_image);
        imageView = findViewById(R.id.image);
        txt = findViewById(R.id.txt);
        //okhttp
        okHttpClient = new OkHttpClient();

        MediaPlayer player = new MediaPlayer();
//      when click the get text button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, "onClickButton");
                if (photoURI != null) {

                    //HTTP communication
                    new Thread() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void run() {
                            String filePath = getApplicationContext().getFilesDir().getAbsolutePath();
                            PyObject res = py.getModule("hello").callAttr("run", getExternalFilesDir(".cache").getAbsolutePath());
                            String s = res.toJava(String.class);
                            Log.e(TAG, s);
                            txt.setText(s);
                            TtsMain ttsMain = new TtsMain();

                            try {
                                ttsMain.text = s;
                                //get the mp3

                                bytes = ttsMain.run();
                                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                String timeString = df.format(new Date());
                                File file = new File(filePath + "result.mp3");
                                FileOutputStream os = new FileOutputStream(file);
                                os.write(bytes);
                                os.close();

                                //play the audio
                                player.setDataSource(filePath + "result.mp3");
                                player.prepare();
                            } catch (IOException | DemoException | JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }.start();
                    btn.setEnabled(true);
                }
            }
        });
//        when click the playvoice button
        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.start();
                    player.setLooping(true);
                }
            }
        });


        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    player.pause();
                } else {
                    player.start();
                    player.setLooping(true);
                }
            }
        });

        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPermissionGranted()) {
                    openCameraAndSavePhoto();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            int deniedCode = 0;

            // Add only permission which are denied
            for (int in : grantResults) {
                if (in == PackageManager.PERMISSION_DENIED) {
                    deniedCode++;
                }
            }

            // Check if all permission granted
            if (deniedCode == 0) {
                // Proceed
                openCameraAndSavePhoto();
            } else {
                if (isPermissionGranted())
                    openCameraAndSavePhoto();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                imageView.setImageURI(photoURI);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                photoURI = null;
            }
        }
    }


    private int REQUEST_TAKE_PHOTO = 101;
    private Uri photoURI = null;

    private Uri getImageFileUri() {
        try {
            File storageDir = getExternalFilesDir(".cache");
            File mFile = new File(storageDir, "faces.jpg");
            if (mFile.exists()) {
                mFile.delete();
            }
            mFile.createNewFile();
            return FileProvider.getUriForFile(
                    MainActivity.this,
                    "com.example.groupproject.fileprovider",
                    mFile
            );
        } catch (Exception e) {
            // Error occurred while creating the File
            e.printStackTrace();
            return null;
        }
    }

    private void resetImageView() {
        imageView.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.test));
        txt.setText("The correspond text");
    }

    private void openCameraAndSavePhoto() {
        photoURI = getImageFileUri();
        if (photoURI != null)
            resetImageView();
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

    }

    private static final int REQUEST_PERMISSION = 786;
    private final List<String> permissionsNeeded = new ArrayList<>();
    private final List<String> permissionsAll = Arrays.asList(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
    );
    private final List<String> permissionsDenied = new ArrayList<>();

    @RequiresApi(Build.VERSION_CODES.M)
    private Boolean checkPermission(
            String permission
    ) {
        if (ContextCompat.checkSelfPermission(
                this,
                permission
        ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        } else if (shouldShowRequestPermissionRationale(permission)
        ) {
            permissionsDenied.add(permission);
            return true;
        }
        return false;
    }

    private Boolean isPermissionGranted() {
        permissionsNeeded.clear();
        permissionsDenied.clear();
        for (String per : permissionsAll) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!checkPermission(
                        per
                )
                ) permissionsNeeded.add(per);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    permissionsNeeded.toArray(new String[permissionsNeeded.size()]),
                    REQUEST_PERMISSION
            );
            return false;
        } else if (!permissionsDenied.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Permissions Required")
                    .setMessage("Allow all permission at [Setting] > [permissions]")
                    .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("Settings", (dialogInterface, i) -> {
                        Intent intent = new Intent(
                                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null)
                        );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }).setCancelable(true).create().show();
            return false;

        }

        return true;
    }
}