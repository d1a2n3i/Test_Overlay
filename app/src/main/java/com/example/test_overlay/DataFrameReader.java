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

    int nextFrame = 3;//3 corresponds to the NextFrame Column on the DataFrame
    int elementText = 6;//6 corresponds to the NextFrame Column on the DataFrame
    /**
     * Returns the next frame of an Activity/Fragment by locating the fragment a button has been clicked on and finding its associated next frame
     * This is done by going through all fragments of an activity and through all the elements of those fragments until the button element has been found
     * This can be implemented with fragments later on to decrease the occurrence of false positives
     **/
    public String readAllDataFramesInFolder(Context context, String Activity, String buttonClicked) throws IOException {

        String [] dataFrames = context.getAssets().list(Activity);//List of all fragments in this Activity



        for(int i = 0; i < dataFrames.length; i++){

            CSVReader reader = new CSVReader(new InputStreamReader((context.getAssets().open(Activity + "/" +dataFrames[i])), Charset.forName("UTF-8")));

            List myEntries = reader.readAll();
            String [] words = null;
            Log.d("MY ENTRIES SIZE", String.valueOf(myEntries.size()));
            for(int row = 0; row < myEntries.size(); row++){//rows

                /**
                 * myEntries.get(row)
                 * word[columns]
                */
                words = (String[]) myEntries.get(row);//myEntries.get(row)

                if(words[elementText].equals(buttonClicked))//if buttonClicked matches
                {
                    if(!words[nextFrame].equals("")){
                        String fragment = words[nextFrame];
                        return fragment;
                    }

                }


            }


            Log.d("EVERYTHING IN DATAFRAME", String.valueOf(words[1]));//token [0] selects the zeroth element of the row

        }

        return "";

    }
}
