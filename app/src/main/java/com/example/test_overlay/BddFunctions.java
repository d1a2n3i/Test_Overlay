package com.example.test_overlay;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class BddFunctions {

    //adds line to BDD
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

    //Initializes the Background state of the device
    public String initializeBackground(String firstData){
        String background;
        Log.d("STRING FIRST DATA", firstData);
        Log.d("STRING FIRST DATA", String.valueOf(firstData.equals( "com.ecobee.athenamobile.ui.login.LoginActivity")));

        if(firstData.equals( "com.ecobee.athenamobile.ui.login.LoginActivity")){
            background = "    Background: User should be logged out\n" +
                    "      Given Driver is initialized\n" +
                    "      And User is logged out\n\n";

        }
        else if (firstData.contains("com.ecobee.athenamobile")){
            background = "  Background: Driver setup\n" +
                    "    Given Driver is initialized\n" +
                    "    And User is \"logged in\"\n" +
                    "    And App has location permissions\n\n";
        }
        else
            background = "Not on ecobee app\n";

        return background;
    }

    //Creates Scenario for BDD
    public String createScenario(){
        String scenario = "  Scenario: Test_Overlay Test\n";

        return scenario;
    }

    public void bddMap(ArrayList<ArrayList<String>> allData){

        String strActivity = "      I am on ";
        String strClicked = "      When I tap ";
        String strType = "      And I type ";
        String strLastActivity = "      Then I am on ";

        if(!allData.isEmpty()) {

            String background = initializeBackground(allData.get(0).get(0));
            appendToBdd(background);
            String activity = "";

            appendToBdd(createScenario());

            for(int i = 0; i < allData.size();i++) {
                //Extracts the activity name by tokenizing and grabbing the last part of the xpath name
                ArrayList<String> singleList = allData.get(i);
                StringTokenizer tokenizer;

                if(i < allData.size()){

                    tokenizer = new StringTokenizer(allData.get(i).get(0), ".");
                    while (tokenizer.hasMoreTokens()) {
                        activity = tokenizer.nextToken();//Formats Activity, extracts all  of the irreleavent package information ie) com.ecobee.athenamobile.LoginActivity -> LoginActivity
                    }

                }
                /**
                 * Formats all data into a readable string that excludes all irrelevant information
                 * allData:
                 * format:[[Act0, eventOnAct0, ... eventOnAct0],[Act1 eventOnAct1, ... eventOnAct1],[Act n eventOnAct n, ... eventOnAct n]]
                 */
                for(int j = 0; j < singleList.size();j++)
                {
                    String buttonName = singleList.get(j);

                    if(activity!="") {

                        if (j == 0 && (singleList.size() > 1||i < allData.size()-1))
                            appendToBdd(strActivity + activity + "\n");//append activity
                        else if (j!=0 && singleList.get(j).contains("android.widget.EditText")
                                && allData.get(i).get(j).contains(",")) { //Formating keyboard clicks android.widget.EditText[hello, hello] -> hello

                            appendToBdd(strType +
                                    buttonName.substring(buttonName.indexOf("[") + 1, buttonName.indexOf("]"))//Formating keyboard clicks android.widget.EditText[hello, hello] -> hello
                                    + "\n");

                        } else if (j != 0){
                            Log.v("WHY DID YOU CRASH", String.valueOf(allData));
                            Log.v("Button Name", buttonName);
                            appendToBdd(strClicked +
                                    buttonName.substring(buttonName.indexOf("[") + 1, buttonName.indexOf("]"))//Formating button clicks android.widget.button[Account] -> Account
                                    + "\n");
                        }

                    }
                }
            }

            appendToBdd(strLastActivity + activity  + "\n");// last line of test
        }

    }





}
