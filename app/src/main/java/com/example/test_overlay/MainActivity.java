/*

    This Activity is the front page of the app
 */

package com.example.test_overlay;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;

/**
 * MainActivity is the front page of the App it corresponding xml file is activity_main in res/layout
 *
 * Functionality: Allows users to grant the required permissions at the click of a button and also launches the overlay and
 * MailSender activity
 */

public class MainActivity extends AppCompatActivity{


    private Button permissionBtn;
    private Button storePermBtn;
    private Button displayOverlayBtn;
    private Button emailActivityBtn;

    private int display = 0;

    /**
     * SharedPreferences are used to store small pieces of data across teh entire application
     * Using Keys to access the stored information across activities
     */
    private SharedPreferences preferences;//Get info from shared preferences
    private SharedPreferences.Editor editor;//Store info to shared preferences
    String recordInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final PackageManager pm = getPackageManager();
        String apkName = "com.ecobee.athenamobile";
        String fullPath = "data/user/0" + "/" + apkName;
        Log.d("FULL PATH", fullPath);

        //recordInfo starts and stops the recording process when the floating play button is clicked
        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        editor = preferences.edit();
        recordInfo = "False";
        editor.putString("recordInfo",recordInfo);
        editor.apply();


        permissionBtn = findViewById(R.id.permission);
        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                Intent intent = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);//Launches the Activity to grant Accessibility services

            }

        });

        storePermBtn =findViewById(R.id.storage_permission);
        storePermBtn.setOnClickListener(
                new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {

                                Toast.makeText(MainActivity.this,
                                        "Permission is granted", Toast.LENGTH_SHORT).show();

                            } else {

                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);//Get Storage Permission
                            }
                        }
                        else { //permission is automatically granted on sdk<23 upon installation

                            Log.v("Grant","Permission is already granted");
                        }

                    }
                });

        final DataFrameReader dataFrameReader = new DataFrameReader();

        displayOverlayBtn = findViewById(R.id.display_overlay);
        displayOverlayBtn.setText("Display Overlay");

        displayOverlayBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Title");

                if (!Settings.canDrawOverlays(MainActivity.this)) {

                    Intent intent = new Intent (Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivityForResult(intent, 1);//Starts Activity to get teh Overlay permission if the permission has not been granted yet
                }
                else {
                    //recordInfo is always set to false when the display is turned off or on
                    if(display==0) {

                        startService(new Intent(MainActivity.this, FloatingWindow.class));//Starts the overlay Service
                        displayOverlayBtn.setText("Remove Overlay");
                        display = 1;

                        recordInfo = "False"; //sets recordInfo to false whenn
                        editor.putString("recordInfo",recordInfo);
                        editor.apply();

                    }else if (display==1){

                        stopService(new Intent(MainActivity.this, FloatingWindow.class));//Stops the overlay Service
                        displayOverlayBtn.setText("Display Overlay");
                        display = 0;


                        recordInfo = "False";//Recording State set to false
                        editor.putString("recordInfo",recordInfo);
                        editor.apply();

                    }

                }

            }
        });

        //Navigates to the MailSenderActivity
        emailActivityBtn = (Button) findViewById(R.id.send_activity);

        emailActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, MailSenderActivity.class);

                startActivity(intent);//Navigates to the MailSenderActivity

            }
        });
    }
}
