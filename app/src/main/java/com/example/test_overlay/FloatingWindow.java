package com.example.test_overlay;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.RequiresApi;



public class FloatingWindow extends Service {

    private WindowManager wm;

    private ImageButton recordButton;
    private ImageButton resetDataButton;
    private Button generateTestButton;

    /**
     * SharedPreferences are used to store small pieces of data across teh entire application
     * Using Keys to access the stored information across activities
     */
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private String recordInfo;
    boolean generateFiles = false;
    boolean resetData = false;

    GestureDetector gestureDetector;

    int height = Resources.getSystem().getDisplayMetrics().heightPixels;
    int width = Resources.getSystem().getDisplayMetrics().widthPixels;
    int image = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(){
        super.onCreate();

        /**
         * Formating the button layout on screen
         */
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        else
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;


        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        recordButton = new ImageButton(this);
        generateTestButton = new Button(this);
        resetDataButton = new ImageButton(this);

        LinearLayout.LayoutParams llParameters = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        WindowManager.LayoutParams parameters = new WindowManager.LayoutParams
                (150,150, LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        recordButton.setBackgroundResource(R.drawable.video_play_icon_png_1463536);
        recordButton.setLayoutParams(llParameters);

        Log.d("WIDTH",String.valueOf(width/2));
        parameters.x=-width/2;
        parameters.y=0;
        parameters.gravity = Gravity.NO_GRAVITY;

        wm.addView(recordButton,parameters);
        gestureDetector = new GestureDetector(this, new SingleTapConfirm());

        recordButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(gestureDetector.onTouchEvent(event)){

                    //All touch events occur here

                    preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
                    recordInfo = preferences.getString("recordInfo","null");//Record info
                    editor = preferences.edit();

                    Log.d("Before",recordInfo);



                    if (image == 0) {

                        recordButton.setBackgroundResource(R.drawable.stop_song_red_512);
                        image = 1;
                        recordInfo = "True";//Recording starts this Variable is used in MyAccessibilityService
                        editor.putString("recordInfo", recordInfo);
                        editor.apply();
                    }
                    else {

                        recordButton.setBackgroundResource(R.drawable.video_play_icon_png_1463536);
                        image = 0;
                        recordInfo = "False";//Recording starts this Variable is used in MyAccessibilityService
                        editor.putString("recordInfo", recordInfo);
                        editor.apply();

                    }

                    Log.d("After", recordInfo);


                }
                else {

                    moveButton(view,event,150,150);
                    return true;
                }
                return false;
            }
        });


        generateTestButton.setText("Generate Test");
        generateTestButton.setLayoutParams(llParameters);

        parameters = new WindowManager.LayoutParams
                (200,200, LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        parameters.x=width/2;
        parameters.y=0;
        parameters.gravity = Gravity.NO_GRAVITY;

        generateTestButton.setOnTouchListener(new View.OnTouchListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if(gestureDetector.onTouchEvent(event)){

                    preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
                    editor = preferences.edit();
                    generateFiles = true;
                    editor.putBoolean("generateFiles",generateFiles);
                    editor.apply();

                    Toast.makeText(FloatingWindow.this,"File Generated", Toast.LENGTH_SHORT).show();
                }
                else {
                    moveButton(view,event,200,200);
                    return true;
                }

                return false;
            }
        });


        wm.addView(generateTestButton,parameters);

        parameters = new WindowManager.LayoutParams
                (150,150, LAYOUT_FLAG,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);

        resetDataButton.setBackgroundResource(R.drawable.reset);
        resetDataButton.setLayoutParams(llParameters);

        parameters.x=-width/2;
        parameters.y=-300;
        parameters.gravity = Gravity.NO_GRAVITY;

        wm.addView(resetDataButton,parameters);

        resetDataButton.setOnTouchListener(new View.OnTouchListener() {

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if(gestureDetector.onTouchEvent(event)){
                    preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
                    editor = preferences.edit();
                    resetData = true;
                    editor.putBoolean("resetData",resetData);
                    editor.apply();
                    Toast.makeText(FloatingWindow.this,"Stored Data Reset  ", Toast.LENGTH_SHORT).show();

                }
                else {
                    moveButton(view,event,150,150);
                    return true;
                }

                return false;
            }
        });
    }

    //Removes all of the Overlays
    public void onDestroy(){
        super.onDestroy();

        wm.removeViewImmediate(recordButton);
        wm.removeViewImmediate(generateTestButton);
        wm.removeViewImmediate(resetDataButton);
    }

    //Confirms a tap
    private class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    //Moves the buttons
    public void moveButton(View view, MotionEvent event, int btnWidth, int btnHeight){

        if(event.getAction() == MotionEvent.ACTION_MOVE){

            int LAYOUT_FLAG;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            else
                LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;

            WindowManager.LayoutParams parameters = new WindowManager.LayoutParams
                    (btnWidth,btnHeight, LAYOUT_FLAG,
                            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);


            //get the touch location

            parameters.x = (int) event.getRawX()-width/2;
            parameters.y = (int) event.getRawY()-height/2;
            wm.updateViewLayout(view, parameters);

        }
    }
}
