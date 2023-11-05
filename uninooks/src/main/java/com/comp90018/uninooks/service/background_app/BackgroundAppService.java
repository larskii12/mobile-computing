package com.comp90018.uninooks.service.background_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;

import com.comp90018.uninooks.R;

public class BackgroundAppService extends Service {

    private Handler handler;
    private long backgroundStartTime = 0;

    private int checkInterval = 1000;

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();
        backgroundStartTime = System.currentTimeMillis();
        Log.d("BackgroundAppSevice", "I'm here!!");
        startCheckingInBackgroundTime();
    }


    private void startCheckingInBackgroundTime() {
        // 1 second
        handler.postDelayed(timerRunnable, checkInterval);
    }

    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long backgroundTime = System.currentTimeMillis() - backgroundStartTime;
            if (backgroundTime > 15000) { // 15 seconds
                sendNotification();
            } else {
                handler.postDelayed(timerRunnable, checkInterval);
            }
        }
    };

    private void sendNotification() {
        Log.d("BackgroundAppSevice", "I'm notifying!!");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "uninooks";
        String channelName = "Uninooks Notification";

        Notification.Builder builder;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        builder = new Notification.Builder(this, channelId);

        builder.setSmallIcon(R.drawable.logo_uninook)
                .setContentTitle("Still in Study Mode!")
                .setContentText("Timer is still running. You should get back to studying.");

        notificationManager.notify(1, builder.build());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
