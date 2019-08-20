package com.example.test_overlay;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;

import java.util.List;
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;

public class MyAccessibilityService extends AccessibilityService {

    ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();
    ArrayList<String> singleList = new ArrayList<String>();
    String firstFrame;
    boolean generateFiles = false;
    boolean resetData = false;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String recordInfo;
    List<String> storeActivity = new ArrayList<>();
    List<String> buttonInfo = new ArrayList<>();

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG,"Service Connected");

        final PackageManager pm = getPackageManager();
        super.onServiceConnected();

       // Configure these here for compatibility with API 13 and below.
        AccessibilityServiceInfo config = new AccessibilityServiceInfo();
        config.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        config.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        if (Build.VERSION.SDK_INT >= 16)
            config.flags = AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;

        setServiceInfo(config);

        startActivity(pm.getLaunchIntentForPackage("com.ecobee.athenamobile"));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo source = event.getSource();

     //   Log.d("TYPE_ALL ",String.valueOf(event));
      //  Log.d("SOURCE ", String.valueOf(source));
       // Log.d("SOURCE PARENT ", String.valueOf(source.getParent()));
        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        recordInfo = preferences.getString("recordInfo","null");
        editor = preferences.edit();


        final int eventType = event.getEventType();

        switch(eventType) {

            case AccessibilityEvent.TYPES_ALL_MASK:
                //Log.d("TYPE_ALL_MASK",String.valueOf(event.getClassName()));
                break;
            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:
                //Log.d("TYPE_VIEW_CONTEXT_CLICK",String.valueOf(event.getClassName()));
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                //Log.d("TYPE_VIEW_TEXT_CHANGED",String.valueOf(event.getClassName()));
            case AccessibilityEvent.TYPE_VIEW_CLICKED:

                preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
                recordInfo = preferences.getString("recordInfo","null");
                editor = preferences.edit();

                if(recordInfo.equals("True")) {

                    if(singleList.isEmpty()) {
                        singleList.add(firstFrame);//firstFrame is manually tracked adds to single list
                    }



                    singleList.add(String.valueOf(event.getClassName())+ event.getText());

                    int count = 0;
                    for(int i = 0; i < singleList.size(); i++){

                        if(singleList.get(i).contains("android.widget.EditText")
                                &&singleList.get(i).contains(",")){
                            count++;
                            if(count == 2){
                                singleList.remove(i-1);
                            }
                        }
                    }

                    Log.d("SingleList on Btn Click", String.valueOf(singleList));

                    buttonInfo.add(String.valueOf(event.getClassName())+ event.getText());
                    editor.putString("buttonInfo", String.valueOf(buttonInfo));
                    editor.apply();
                    Toast.makeText(MyAccessibilityService.this, "Button: " + event.getClassName() + event.getText(), Toast.LENGTH_SHORT).show();


                }
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:

                firstFrame = String.valueOf(event.getClassName());

                if(recordInfo.equals("True")) {

                    if(!singleList.isEmpty()){
                        allData.add(new ArrayList<>(singleList));
                        Log.d("SingleL on ScreenChange", String.valueOf(singleList));

                        singleList.clear();
                        singleList.add(firstFrame);
                    }
                    preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
                    recordInfo = preferences.getString("recordInfo","null");
                    editor = preferences.edit();

                    storeActivity.add( String.valueOf(event.getClassName()));
                    editor.putString("storeActivity", String.valueOf(storeActivity));
                    editor.apply();

                    Log.d("Tag", String.valueOf(storeActivity));
                    Toast.makeText(MyAccessibilityService.this, "Page: " + event.getClassName(), Toast.LENGTH_SHORT).show();

                }else{

                }
                break;

        }
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }

        File gpxfile = new File(root, "format.txt");
        FileWriter writer = null;
        try {
            writer = new FileWriter(gpxfile);
            writer.append(String.valueOf(allData));
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        resetData = preferences.getBoolean("resetData", false);

        if(resetData == true) {
            clearBdd("bdd.txt");
            clearBdd("format.txt");
            allData.clear();
            singleList.clear();
            singleList.add(firstFrame);

            preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
            editor = preferences.edit();
            resetData = false;
            editor.putBoolean("resetData",resetData);
            editor.apply();
        }
        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        generateFiles = preferences.getBoolean("generateFiles", false);
        if(generateFiles == true && !allData.isEmpty()){
            Log.d("Before ArrayList", String.valueOf(allData));
            Log.d("Before SingleList", String.valueOf(singleList));

            if(!allData.get(allData.size()-1).equals(singleList)) {
                allData.add(new ArrayList<>(singleList));
                singleList.clear();
            }

            Log.d("After ArrayList", String.valueOf(allData));
            Log.d("After SingleList", String.valueOf(singleList));

            clearBdd("bdd.txt");
            bddMap(allData);
            preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
            editor = preferences.edit();
            generateFiles = false;
            editor.putBoolean("generateFiles",generateFiles);
            editor.apply();
        }else{
            Toast.makeText(MyAccessibilityService.this, "File Empty",Toast.LENGTH_LONG);

        }

    }


    @Override
    public void onInterrupt() {
        Log.d(TAG,"Accessibility Interrupted" );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"Service Destroyed");
    }

    public void bddMap(ArrayList<ArrayList<String>> allData){
        String strActivity = "I am on ";
        String strClicked = "When I tap ";
        String strType = "And I type";
        if(!this.allData.isEmpty()) {

            String background = initializeBackground(allData.get(0).get(0));
            appendToBdd(background);
            String activity = "";

            for(int i = 0; i < allData.size();i++)
            {
                StringTokenizer tokenizer = new StringTokenizer(allData.get(i).get(0),".");
                while (tokenizer.hasMoreTokens())
                {
                    activity = tokenizer.nextToken();
                }



                for(int j = 0; j < allData.get(i).size();j++)
                {
                    String buttonName = allData.get(i).get(j);

                    if(j == 0)
                        appendToBdd(strActivity +activity + "\n");
                    else if(allData.get(i).get(j).contains("android.widget.EditText")
                            &&allData.get(i).get(j).contains(",")){

                        appendToBdd(strType +
                                buttonName.substring(buttonName.indexOf("[") + 1, buttonName.indexOf("]"))
                                + "\n");

                    }else {
                        appendToBdd(strClicked +
                                buttonName.substring(buttonName.indexOf("[") + 1, buttonName.indexOf("]"))
                                + "\n");
                    }
                }
            }
        }

    }

    public String initializeBackground(String firstData){
        String background;
        Log.d("STRING FIRST DATA", firstData);
        Log.d("STRING FIRST DATA", String.valueOf(firstData.equals( "com.ecobee.athenamobile.ui.login.LoginActivity")));

        if(firstData.equals( "com.ecobee.athenamobile.ui.login.LoginActivity")){
            background = "    Background: User should be logged out\n" +
                    "      Given Driver is initialized\n" +
                    "      And User is logged out\n";

        }
        else {
            background = "  Background: Driver setup\n" +
                    "    Given Driver is initialized\n" +
                    "    And User is \"logged in\"\n" +
                    "    And App has location permissions\n";
        }

        return background;
    }

    public String createScenario(){
        String scenario = "  Scenario: Enter Text Here Manually";

        return scenario;
    }

    public void appendToBdd(String data){
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }

        File gpxfile = new File(root, "bdd.txt");

        FileWriter writer = null;
        try {
            writer = new FileWriter(gpxfile, true);
            writer.append(data);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearBdd(String file){
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }

        File gpxfile = new File(root, file);

        FileWriter writer = null;
        try {
            writer = new FileWriter(gpxfile);
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}