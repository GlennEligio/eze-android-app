package com.eze;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class EZEApp extends Application {
    
    public static final String REQUEST_NOTIFICATION_CHANNEL = "com.eze.requestNotificationChannel";
    public static final String REQUEST_NOTIFICATION_GROUP = "com.eze.requestNotificationGroup";

    @Override
    public void onCreate() {
        super.onCreate();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel eze_notifications = new NotificationChannel(
                    REQUEST_NOTIFICATION_CHANNEL,
                    "EZE Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            eze_notifications.setDescription("EZE Incoming Request");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(eze_notifications);
        }
    }
}
