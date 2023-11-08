package com.comp90018.uninooks.service.background_app;

import static com.comp90018.uninooks.activities.FocusModeTimerActivity.isCurrentlyOnApp;

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
import com.comp90018.uninooks.models.background_app.BackgroundApp;
import com.comp90018.uninooks.models.background_app.UnwantedApp;
import com.comp90018.uninooks.receiver.FocusModeReceiver;

import java.util.ArrayList;
import java.util.List;

public class BackgroundAppService extends Service {

    private String TAG = "MyService";

    public static boolean isServiceRunning = false;

    private String currentPackageName = null;

    ArrayList<BackgroundApp> backgroundApps = new ArrayList<>();

    private Handler handler;

    private boolean isUsingEntertainmentApp = false;

    private static final int CHECK_INTERVAL = 30000;

    public static boolean NOTIFICATION_STATUS = false;

    public static boolean getNotificationPermission(){
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
        Log.d("BackgroundAppService", "I'm here!!");
        startCheckingInBackgroundTime();

        return START_STICKY;
    }

    private boolean isEntertainmentApp(String packageName) {
        for (UnwantedApp app : UnwantedApp.values()) {
            if(packageName.contains(app.name)) {
               return true;
            }
        }
        return false;
    }


    private void startCheckingInBackgroundTime() {
        // 1 second
        handler.postDelayed(appRunnable, CHECK_INTERVAL);
    }

    private final Runnable appRunnable = new Runnable() {
        @Override
        public void run() {
            UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            long thirtySecondAgo = currentTime - (1000 * 30);  // currently using now

            backgroundApps = new ArrayList<>();

//            List<UsageStats> HistoryUsedAppList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, oneSecondAgo, currentTime);

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                for (UsageStats historyApp : HistoryUsedAppList) {
//
//                    if (historyApp.getLastTimeVisible() > oneSecondAgo) {
//
//                        BackgroundApp newBackgroundApp = new BackgroundApp();
//                        newBackgroundApp.setLastTimeUsed(historyApp.getTotalTimeVisible());
//                        newBackgroundApp.setPackageName(historyApp.getPackageName());
//
//                        backgroundApps.add(newBackgroundApp);
//                    }
//                }
//            }
//
//            else {
//                for (UsageStats historyApp : HistoryUsedAppList) {
//                    if (historyApp.getLastTimeUsed() > oneSecondAgo) {
//
//                        BackgroundApp newBackgroundApp = new BackgroundApp();
//                        newBackgroundApp.setLastTimeUsed(historyApp.getLastTimeUsed());
//                        newBackgroundApp.setPackageName(historyApp.getPackageName());
//
//                        backgroundApps.add(newBackgroundApp);
//
//                    }
//                }
//            }
            UsageEvents usageEvents = usageStatsManager.queryEvents(thirtySecondAgo, currentTime); // Check the last 30 seconds

            UsageEvents.Event event = new UsageEvents.Event();
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    currentPackageName = event.getPackageName();
                }
            }

            boolean isBreakFromLoop = false;
            isUsingEntertainmentApp = false;
//            for (BackgroundApp backgroundApp : backgroundApps) {
//
//                String foregroundPackageName = backgroundApp.getPackageName();
//                Log.d("BackgroundAppService", foregroundPackageName);
//
//                // Check if the foreground app is an "entertainment app."
//                if (isEntertainmentApp(foregroundPackageName) ) {
//                    // The foreground app is an entertainment app.
//                    //backgroundStartTime = System.currentTimeMillis();
//                    Log.d("BackgroundAppService", "Opened!");
//
//                    isUsingEntertainmentApp = true;
//                    sendNotification();
//                    handler.postDelayed(appRunnable, CHECK_INTERVAL);
//                    isBreakFromLoop = true;
//                    break;
//                }
//            }
            if (nonNull(currentPackageName) && isEntertainmentApp(currentPackageName) ) {
                // The foreground app is an entertainment app.
                //backgroundStartTime = System.currentTimeMillis();
                Log.d("BackgroundAppService", "Opened!");

                isUsingEntertainmentApp = true;
                sendNotification();
                handler.postDelayed(appRunnable, CHECK_INTERVAL);
                isBreakFromLoop = true;
            }
            if (!isBreakFromLoop && isScreenOn()) {
                Log.d("BackgroundAppService", "Not opened");
                sendNotification();
                handler.postDelayed(appRunnable, CHECK_INTERVAL);
            }
        }
    };



    private void sendNotification() {
        Log.d("BackgroundAppService", "I'm notifying!!");

        if (isUsingEntertainmentApp) {
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            String channelId = "uninooks";
            String channelName = "Uninooks Notification";

            Intent intent = new Intent(this, FocusModeTimerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.setAction(Intent.ACTION_MAIN);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            Notification.Builder builder = new Notification.Builder(this, channelId);

            builder.setSmallIcon(R.drawable.logo_uninook)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_uninook))
                    .setContentTitle("We detected a distraction!")
                    .setContentText("You are using another app while in Focus Mode. Please go back to studying.")
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

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

        builder.setSmallIcon(R.drawable.logo_uninook)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_uninook))
                .setContentTitle("Focus Mode is enabled.")
                .setContentText("Timer is currently running.")
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pendingIntent);

        return builder.build();
    }

    private boolean isScreenOn() {
        PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        return powerManager.isInteractive();
    }

    @Override
    public void onDestroy() {
        Log.d("BackgroundAppService", "Back to Foreground.");
        handler.removeCallbacks(appRunnable);

        isServiceRunning = false;
        stopForeground(true);

        if (isCurrentlyOnApp) {
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
