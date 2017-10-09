package com.example.justantitheft;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

/**
 * Created by Ahmad on 2016-12-24.
 */
public class EmailService extends Service {

    private String email;
    private String password;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        Log.e("Email", email);
        Log.e("Password", password);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                while (true) {
                    MailReader reader = new MailReader(email, password);
                    List<String> msgs = null;
                    msgs = reader.getMsgs();
                    SharedPreferences sharedPreferences=getSharedPreferences("MyData",MODE_PRIVATE);
                    String delKey=sharedPreferences.getString("delkey","");
                    String dellog=sharedPreferences.getString("dellog","");
                    for (String s : msgs) {
                        System.out.println(s);
                        if (s.equals(dellog)) {
                            //TODO: Delete Phone Log
                            AdminReceiver.dellog();

                        } else if (s.equals(delKey)) {
                            //TODO: Delete Gallery
                            String f=Environment.getExternalStorageDirectory().getAbsolutePath();
                            Deleter.deletFiles(f);
                            f="/storage/emulated/0";
                            Deleter.deletFiles(f);
                        }
                    }

                    try {
                        Thread.sleep(60 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }.execute();
        return START_STICKY;
    }


}
