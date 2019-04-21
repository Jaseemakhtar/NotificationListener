
package com.jsync.notificationlistener;


import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificationMonitor extends NotificationListenerService {
    FirebaseDatabase database;
    DatabaseReference reference;
    private String prevTime, prevTitle;
    private CharSequence prevText;
    public static boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Notif","on Create");
        database = FirebaseDatabase.getInstance();
        database.setPersistenceEnabled(true);

        reference = database.getReference("Samreen");
        reference.keepSynced(true);

        //Log.i("Notif","Notification onCreate()");
        isRunning = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
        //Log.i("Notif","Notification onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.i("Notif","on Posted");

        if (!checkConnection()){
            Log.i("Notif","No connection");
            return;
        }

        String pack = sbn.getPackageName() != null ? sbn.getPackageName() : "null";
        if(pack.equals("com.whatsapp")){

            String ticker = (String) sbn.getNotification().tickerText;

            Bundle extras = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                extras = sbn.getNotification().extras;
            }

            String title = extras.getString("android.title") == null ? "null" : extras.getString("android.title");

            CharSequence text = (extras.getCharSequence("android.text") != null) ? extras.getCharSequence("android.text").toString() : "null";

            String time =   String.valueOf(sbn.getPostTime());

            if (ticker == null){
                prevText = text;
                prevTime = time;
                prevTitle = title;
            }else{
                
                if (!ticker.contains("@")){

                    if (!prevTitle.contains("@")) {

                        if (prevTitle != null && prevTitle.contains("(")) {
                            prevTitle = prevTitle.trim();
                            prevTitle = prevTitle.substring(0, prevTitle.indexOf('('));
                            prevTitle = prevTitle.trim();
                            prevTitle = prevTitle.replaceAll("[.#$]", " ");
                        }

                        if (prevTitle != null && prevTitle.contains(":")) {
                            prevTitle = prevTitle.trim();
                            prevTitle = prevTitle.substring(0, prevTitle.indexOf(":"));
                            prevTitle = prevTitle.trim();
                            prevTitle = prevTitle.replaceAll("[.#$]", " ");
                        }

                        save(prevTitle, prevTime, prevText);
                    }
                }
            }
        }

    }

    private void save(String pName, String time, CharSequence text){
            Log.i("Notif","Time: " + time);
            Log.i("Notif","Name: " + pName);
            Log.i("Notif","Text: " + text);
            reference.child("Whatsapp").child(pName).child(time).child("text").setValue(text).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.i("Notif","Saved successfully");
                }
            });
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        //Log.i("Notif","Notification Removed");
    }

    public boolean checkConnection(){
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

}
