package com.example.test_overlay;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

/**
 * The associated layout of this file is called send_mail.xml located in the res/layout folder
 */
public class MailSenderActivity extends AppCompatActivity {

    private EditText subject;
    private EditText body;
    private String senderEmail = "uslessemailtest@gmail.com";//throwaway email used to send bdd files
    private String [] recipients = {"dani.a@ecobee.com"};//Emails addresses the emails are sent too

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_mail);

        subject = findViewById(R.id.subject);
        body = findViewById(R.id.body);

        Button send = (Button) this.findViewById(R.id.Send);

        //Opens the file to be sent
        File root = new File(Environment.getExternalStorageDirectory(), "Notes");

        if (!root.exists()) {
            root.mkdirs();
        }

        final File gpxfile = new File(root, "bdd.txt"); //name of the file sent

        //Sends the gpxfile and the custom subject and body to all recipients on click
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Thread sender = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        for(int i = 0; i < recipients.length; i++){

                            try {

                                GMailSender sender = new GMailSender(senderEmail, "password123@");//GmailSender only works when a gmail account with lowered security is used
                                sender.sendMail(String.valueOf(subject.getText()),
                                        String.valueOf(body.getText()),
                                        senderEmail,
                                        recipients[i], gpxfile);

                            } catch (Exception e) {

                                Log.e("mylog", "Error: " + e.getMessage());

                            }
                        }
                    }
                });
                sender.start();
                Toast.makeText(MailSenderActivity.this, "Email Sent", Toast.LENGTH_SHORT).show();
            }
        });
    }
}