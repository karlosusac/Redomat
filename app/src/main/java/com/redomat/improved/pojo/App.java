package com.redomat.improved.pojo;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {

    //Notification channels
    public static final String notificationChannel = "Notifikacije";


    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

    private void createNotificationChannels() {
        //Check if android version is Oreo/higher or Lower
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    "Notifikacije",
                    "Notifikacije",
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription("Notifikacije za cijelu aplikaciju Redomat");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
}
