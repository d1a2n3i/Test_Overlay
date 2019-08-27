package com.example.test_overlay;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;


public class DataFrameReader {

    public String readAllDataFramesInFolder(Context context, String Activity, String buttonClicked) throws IOException {
    /**

        **/

        String [] dataFrames = context.getAssets().list(Activity);//List of all fragments in this Activity
      //  BufferedReader reader ;

        String line = "";
        for(int i = 0; i < dataFrames.length; i++){

            CSVReader reader = new CSVReader(new InputStreamReader((context.getAssets().open(Activity + "/" +dataFrames[i])), Charset.forName("UTF-8")));

            List myEntries = reader.readAll();
            String [] words = null;
            Log.d("MY ENTRIES SIZE", String.valueOf(myEntries.size()));
            for(int j = 0; j < myEntries.size(); j++){//rows

                /*
                myEntries.get(row)
                word[columns]
                */
                words = (String[]) myEntries.get(i);//myEntries.get(row)

                if(words[6].equals(buttonClicked))//if buttonClicked matches
                {
                    if(!words[3].equals("")){
                        String fragment = words[3];
                        return fragment;
                    }

                }


            }


            Log.d("EVERYTHING IN DATAFRAME", String.valueOf(words[1]));//token [0] selects the zeroth element of the row

        }

        return "";

    }
}
