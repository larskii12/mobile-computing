package com.comp90018.uninooks.receiver;

import static com.comp90018.uninooks.activities.FocusModeTimerActivity.isRunning;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.activities.FocusModeTimerActivity;
import com.comp90018.uninooks.worker.FocusModeWorker;

public class FocusModeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("FocusModeReceiver", "onReceived called");

        if (!isRunning) {
            Intent i = new Intent(context, FocusModeTimerActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.setAction(Intent.ACTION_MAIN);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            String channelName = "Uninooks Notification";

            Notification.Builder builder;

            NotificationChannel channel = new NotificationChannel("launcher", channelName, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.enableLights(true);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(context, "launcher");

            builder.setSmallIcon(getNotificationSmallIcon())
                    .setContentTitle("Times up! ")
                    .setContentText("You have finished your study time. You can start your break time!")
                    .setVisibility(Notification.VISIBILITY_PUBLIC)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(1, builder.build());
        }

        // We are starting MyService via a worker and not directly because since Android 7
        // (but officially since Lollipop!), any process called by a BroadcastReceiver
        // (only manifest-declared receiver) is run at low priority and hence eventually
        // killed by Android.
        WorkManager workManager = WorkManager.getInstance(context);
        OneTimeWorkRequest startServiceRequest = new OneTimeWorkRequest.Builder(FocusModeWorker.class)
                .build();
        workManager.enqueue(startServiceRequest);
    }
    private int getNotificationSmallIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_logo_uninooks : R.drawable.ic_launcher_foreground;
    }
}
