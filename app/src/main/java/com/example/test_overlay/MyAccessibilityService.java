/*
    This is where all the accessibility information is captured, stored and formatted
 */

package com.example.test_overlay;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import static android.content.ContentValues.TAG;



public class MyAccessibilityService extends AccessibilityService {

    /**
     * Act = Activity
     * firstFrame: Very first page recorded is stored here
     * format: Act0
     *
     * singleList: Keeps track of events on an activity An activity and all of the events that have occured on that activity is stored here
     * it is cleared and started anew when a new activity is opened
     * format: [Act0, eventOnAct0, eventOnAct0... eventOnAct0]
     *
     * allData: Stores all singleLists before it get overwritten
     * format:     [[Act0, eventOnAct0, ... eventOnAct0],[Act1 eventOnAct1, ... eventOnAct1],[Act n eventOnAct n, ... eventOnAct n]]
    */



    private BddFunctions bddFunctions = new BddFunctions();
    private String firstFrame;
    private ArrayList<String> singleList = new ArrayList<String>();
    private ArrayList<ArrayList<String>> allData = new ArrayList<ArrayList<String>>();

    //All Variables below are stored in SharedPrefrences and are used across the other 2 activities
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    boolean generateFiles = false;
    boolean resetData = false;
    String recordInfo;

    DataFrameReader dataFrameReader = new DataFrameReader();


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
        startActivity(pm.getLaunchIntentForPackage("com.ecobee.athenamobile"));//start ecobee app on accesibilty event connection
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

       // Log.d("All Events", String.valueOf(event)+ "\n\n");

       // Log.d("Source", event.getSource());
        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        recordInfo = preferences.getString("recordInfo","null");
        editor = preferences.edit();

        final int eventType = event.getEventType();
        //all the seperate events that are tracked
        switch(eventType) {
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                /**
                 * When an EditText field is selected it is appended here
                 */

                if(recordInfo.equals("True")) {

                    if (singleList.isEmpty()) {
                        singleList.add(firstFrame);//firstFrame is manually tracked adds to single list
                    }


                    singleList.add(String.valueOf(event.getClassName())+ event.getText());

                    Toast.makeText(MyAccessibilityService.this, "Focoused: " + event.getClassName(), Toast.LENGTH_SHORT).show();

                    Log.d("SingleL textfiled click", String.valueOf(singleList));
                }
                break;


            case AccessibilityEvent.TYPE_VIEW_CONTEXT_CLICKED:

                break;

            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                /**
                 * All text clicks are appended to singleList and formatted into one input
                 * These events are a--ejded to singleList
                 */
                if(recordInfo.equals("True")) {

                    if (singleList.isEmpty()) {
                        singleList.add(firstFrame);//firstFrame is added to singleList if it is empty
                    }


                    singleList.add(String.valueOf(event.getClassName())+ event.getText());

                    for(int i = 0; i < singleList.size(); i++){

                        if(singleList.get(i).contains("android.widget.EditText")
                                &&singleList.get(i).contains(",")){
                            if(i>0 && singleList.get(i-1).contains(",")){
                                Log.d("Typing to be removed",singleList.get(i-1));
                                singleList.remove(i-1);
                            }
                        }
                    }
                    Toast.makeText(MyAccessibilityService.this, "Clicked: " + event.getText(), Toast.LENGTH_SHORT).show();

                    Log.d("SingleList typing ", String.valueOf(singleList));
                }
                break;

            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                /**
                 * All button clicks are recorded here
                 * These events are appended to singleList
                 */
                if(recordInfo.equals("True")) {

                    if(singleList.isEmpty()) {
                        singleList.add(firstFrame);//firstFrame is added to singleList if it is empty
                    }

                    if(!event.getText().toString().contains("[]"))
                        singleList.add(String.valueOf(event.getClassName())+ event.getText());
                    else
                        singleList.add(event.getClassName() +"[" +event.getContentDescription()+"]");

                    try {
                        //Log.v("Not","Permission is revoked");

                        Log.d("FRAGMENT????",
                                dataFrameReader.readAllDataFramesInFolder(MyAccessibilityService.this,
                                        singleList.get(0), singleList.get(singleList.size()-1)));
                    } catch (IOException e) {
                        Log.v("Not",String.valueOf(e));

                        Log.d("NO YOU cant", e + "Sike you thot");
                    }

                    Log.d("SingleList on Btn Click", String.valueOf(singleList));
                    Toast.makeText(MyAccessibilityService.this, "Button: " + singleList.get(singleList.size()-1), Toast.LENGTH_SHORT).show();

                }

                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                /**
                 * All window changes are recorded here
                 * singleList is added to the allData and then cleared to store new button events on the new activity
                 */

                firstFrame = String.valueOf(event.getClassName());//First frame is tracked before the recording process starts

                if(recordInfo.equals("True")) { //Everytime a new activity pops up singleList is added to all Data and then singleList is cleared

                    Log.d("FirstFrame",firstFrame);
                    if(!singleList.isEmpty()){
                        allData.add(new ArrayList<>(singleList));
                        Log.d("SingleL on ScreenChange", String.valueOf(singleList));

                        singleList.clear();
                        singleList.add(firstFrame);
                    }

                    Toast.makeText(MyAccessibilityService.this, "Page: " + event.getClassName(), Toast.LENGTH_SHORT).show();

                }else{

                }
                break;

        }

        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }

        try {
            File gpxfile = new File(root, "format.txt");
            FileWriter writer = null;
            writer = new FileWriter(gpxfile);
            writer.append(String.valueOf(allData));
            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        resetData = preferences.getBoolean("resetData", false);

        //Resets the BDD, NOTE:  only reset on an Accessiblity event
        if(resetData == true) {

            clearStoredData(allData, singleList);

        }

        //Generates the BDD File, NOTE:  only generates a BDD file on an Accessiblity event
        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        generateFiles = preferences.getBoolean("generateFiles", false);
        if(generateFiles == true){

            generateBdd(allData,singleList);

        }else {

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

    public void generateBdd(ArrayList<ArrayList<String>> allData, ArrayList<String> singleList){
        Log.d("Before ArrayList", String.valueOf(allData));
        Log.d("Before SingleList", String.valueOf(singleList));


        if(allData.size()>0 &&!allData.get(allData.size()-1).equals(singleList)) {
            allData.add(new ArrayList<>(singleList));
            singleList.clear();
        }
        else if(allData.size() == 0 && singleList.size() > 0){
            allData.add(new ArrayList<>(singleList));
            singleList.clear();
        }

        Log.d("After ArrayList", String.valueOf(allData));
        Log.d("After SingleList", String.valueOf(singleList));

        bddFunctions.clearBdd("bdd.txt");
        bddFunctions.bddMap(allData);
        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        editor = preferences.edit();
        generateFiles = false;
        editor.putBoolean("generateFiles",generateFiles);
        editor.apply();
        Toast.makeText(MyAccessibilityService.this, "GENRATED",Toast.LENGTH_LONG);

    }

    public void clearStoredData(ArrayList<ArrayList<String>> allData, ArrayList<String> singleList){

        bddFunctions.clearBdd("bdd.txt");
        bddFunctions.clearBdd("format.txt");
        allData.clear();
        singleList.clear();
        singleList.add(firstFrame);

        preferences = getSharedPreferences("SavedData",MODE_PRIVATE);
        editor = preferences.edit();
        resetData = false;
        editor.putBoolean("resetData",resetData);
        editor.apply();
    }


}