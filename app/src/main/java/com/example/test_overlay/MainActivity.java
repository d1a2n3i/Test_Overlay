package com.example.test_overlay;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity{


    private Button permissionBtn;
    private Button displayOverlayBtn;
    private Button emailActivityBtn;

    private int display = 0;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String recordInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        final PackageManager pm = getPackageManager();
        String apkName = "com.ecobee.athenamobile";
        String fullPath = "data/user/0" + "/" + apkName;
        Log.d("FULL PATH", fullPath);


        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        editor = preferences.edit();
        recordInfo = "False";
        editor.putString("recordInfo",recordInfo);
        editor.apply();


        permissionBtn =  (Button) findViewById(R.id.permission);
        permissionBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivity(intent);//gets permission


            }

        });

        displayOverlayBtn = (Button) findViewById(R.id.display_overlay);
        displayOverlayBtn.setText("Display Overlay");
        displayOverlayBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (!Settings.canDrawOverlays(MainActivity.this)) {

                    Intent intent = new Intent (Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivityForResult(intent, 1);//gets permission
                }
                else {
                    if(display==0) {

                        startService(new Intent(MainActivity.this, FloatingWindow.class));
                        displayOverlayBtn.setText("Remove Overlay");
                        display = 1;

                        recordInfo = "False";
                        editor.putString("recordInfo",recordInfo);
                        editor.apply();

                    }else if (display==1){

                        stopService(new Intent(MainActivity.this, FloatingWindow.class));
                        displayOverlayBtn.setText("Display Overlay");
                        display = 0;

                        recordInfo = "False";
                        editor.putString("recordInfo",recordInfo);
                        editor.apply();

                    }
                }

            }
        });
        emailActivityBtn = (Button) findViewById(R.id.send_activity);

        emailActivityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MailSenderActivity.class);
                startActivity(intent);
                Log.d("Clicked","clicked");
            }
        });


    }

}
