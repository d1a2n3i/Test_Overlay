package com.example.test_overlay;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BddFunctions {

    public void bddMap(ArrayList<ArrayList<String>> allData){

        String strActivity = "I am on ";
        String strClicked = "When I tap ";
        String strType = "And I type ";

        if(!allData.isEmpty()) {

            String background = initializeBackground(allData.get(0).get(0));
            appendToBdd(background);
            String activity = "";

            for(int i = 0; i < allData.size();i++) {
                //Extracts the activity name by tokenizing and grabbing the last part of the xpath name
                StringTokenizer tokenizer;

                if(i < allData.size()){

                    tokenizer = new StringTokenizer(allData.get(i).get(0), ".");
                    while (tokenizer.hasMoreTokens()) {
                        activity = tokenizer.nextToken();
                    }

                }
                //Formats the BDD file
                for(int j = 0; j < allData.get(i).size();j++)
                {
                    String buttonName = allData.get(i).get(j);

                    if(activity!="") {

                        if (j == 0)
                            appendToBdd(strActivity + activity + "\n");
                        else if (allData.get(i).get(j).contains("android.widget.EditText")
                                && allData.get(i).get(j).contains(",")) { //Formating keyboard clicks

                            appendToBdd(strType +
                                    buttonName.substring(buttonName.indexOf("[") + 1, buttonName.indexOf("]"))
                                    + "\n");

                        } else {
                            appendToBdd(strClicked +
                                    buttonName.substring(buttonName.indexOf("[") + 1, buttonName.indexOf("]"))//removes
                                    + "\n");
                        }

                    }
                }
            }
        }

    }

    //Initializes the Background state of the device
    public String initializeBackground(String firstData){
        String background;
        Log.d("STRING FIRST DATA", firstData);
        Log.d("STRING FIRST DATA", String.valueOf(firstData.equals( "com.ecobee.athenamobile.ui.login.LoginActivity")));

        if(firstData.equals( "com.ecobee.athenamobile.ui.login.LoginActivity")){
            background = "    Background: User should be logged out\n" +
                    "      Given Driver is initialized\n" +
                    "      And User is logged out\n";

        }
        else if (firstData.contains("com.ecobee.athenamobile")){
            background = "  Background: Driver setup\n" +
                    "    Given Driver is initialized\n" +
                    "    And User is \"logged in\"\n" +
                    "    And App has location permissions\n";
        }
        else
            background = "Not on ecobee app\n";

        return background;
    }

    public String createScenario(){
        String scenario = "  Scenario: Enter Text Here Manually";

        return scenario;
    }

    //appends a line to the bdd file
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

    //Clears the BDD file
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
