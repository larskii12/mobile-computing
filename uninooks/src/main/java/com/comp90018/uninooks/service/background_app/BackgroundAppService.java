package com.comp90018.uninooks.service.background_app;

import static com.comp90018.uninooks.activities.FocusModeTimerActivity.isRunning;
import static java.util.Objects.nonNull;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.activities.FocusModeTimerActivity;
import com.comp90018.uninooks.models.background_app.UnwantedApp;
import com.comp90018.uninooks.receiver.FocusModeReceiver;

public class BackgroundAppService extends Service {

    private static final int CHECK_INTERVAL = 30000;
    public static boolean isServiceRunning = false;
    public static boolean NOTIFICATION_STATUS = false;
    public static boolean USAGE_STATUS = false;
    private String currentPackageName = null;
    private Handler handler;
    private boolean isUsingEntertainmentApp = false;

    public static boolean getNotificationPermission() {
        return NOTIFICATION_STATUS;
    }

    public static void setNotificationPermission(boolean status) {
        NOTIFICATION_STATUS = status;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start a Foreground Service with a persistent notification
        Notification notification = createNotification();
        startForeground(1, notification);

        handler = new Handler();
        startCheckingInBackgroundTime();

        return START_STICKY;
    }

    private boolean isEntertainmentApp(String packageName) {
        for (UnwantedApp app : UnwantedApp.values()) {
            if (packageName.contains(app.name)) {
                return true;
            }
        }
        return false;
    }


    private void startCheckingInBackgroundTime() {
        // 30 seconds
        handler.postDelayed(appRunnable, CHECK_INTERVAL);
    }

    private void sendNotification() {
        Log.d("BackgroundAppService", "Notification Sent.");

        if (isUsingEntertainmentApp) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            String channelId = "uninooks";
            String channelName = "Uninooks Notification";

            Intent intent = new Intent(this, FocusModeTimerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setAction(Intent.ACTION_MAIN);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            Notification.Builder builder = new Notification.Builder(this, channelId);

            builder.setSmallIcon(getNotificationSmallIcon()).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_uninook)).setContentTitle("We detected a distraction!").setContentText("You are using another app while in Focus Mode. Please go back to studying.").setVisibility(Notification.VISIBILITY_PUBLIC).setAutoCancel(true).setContentIntent(pendingIntent);

            notificationManager.notify(2, builder.build());
        }
    }

    private Notification createNotification() {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        String channelId = "uninooks";
        String channelName = "Uninooks Notification";

        Intent intent = new Intent(this, FocusModeTimerActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.setAction(Intent.ACTION_MAIN);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Notification.Builder builder;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(channel);

        builder = new Notification.Builder(this, channelId);

        builder.setSmallIcon(getNotificationSmallIcon()).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_uninook)).setContentTitle("Focus Mode is enabled.").setContentText("Timer is currently running.").setVisibility(Notification.VISIBILITY_PUBLIC).setAutoCancel(true).setOngoing(true).setContentIntent(pendingIntent);

        return builder.build();
    }

    private boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return powerManager.isInteractive();
    }    private final Runnable appRunnable = new Runnable() {
        @Override
        public void run() {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            long thirtySecondAgo = currentTime - (1000 * 30);  // currently using now

            UsageEvents usageEvents = usageStatsManager.queryEvents(thirtySecondAgo, currentTime); // Check the last 30 seconds
            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    currentPackageName = event.getPackageName();
                }
            }

            isUsingEntertainmentApp = false;
            if (nonNull(currentPackageName) && isEntertainmentApp(currentPackageName)) {
                // The foreground app is an entertainment app.
                isUsingEntertainmentApp = true;
                sendNotification();
                handler.postDelayed(appRunnable, CHECK_INTERVAL);
            }
        }
    };

    private int getNotificationSmallIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_logo_uninooks : R.drawable.ic_launcher_foreground;
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(appRunnable);

        isServiceRunning = false;
        stopForeground(true);

        if (!isRunning) {
            // call MyReceiver which will restart this service via a worker
            Intent broadcastIntent = new Intent(this, FocusModeReceiver.class);
            sendBroadcast(broadcastIntent);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.deleteNotificationChannel("uninooks");
        }
        stopSelf();
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }




}
