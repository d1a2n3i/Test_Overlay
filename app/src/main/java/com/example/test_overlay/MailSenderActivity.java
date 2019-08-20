package com.example.test_overlay;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class MailSenderActivity extends AppCompatActivity {

    private EditText subject;
    private EditText body;
    private String [] emails = {"dani.a@ecobee.com"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_mail);

        subject = findViewById(R.id.subject);
        body = findViewById(R.id.body);

        final Button send = (Button) this.findViewById(R.id.Send);
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");
        if (!root.exists()) {
            root.mkdirs();
        }

        final File gpxfile = new File(root, "bdd.txt");
        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Thread sender = new Thread(new Runnable() {
                    @Override
                    public void run() {

                        for(int i = 0; i < emails.length; i++){
                            try {
                                GMailSender sender = new GMailSender("uslessemailtest@gmail.com", "password123@");

                                sender.sendMail(String.valueOf(subject.getText()),
                                        String.valueOf(body.getText()),
                                        "uslessemailtest@gmail.com",
                                        emails[i], gpxfile);
                            } catch (Exception e) {
                                Log.e("mylog", "Error: " + e.getMessage());
                            }
                        }

                    }
                });
                sender.start();

            }
        });

    }

}