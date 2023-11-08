package com.comp90018.uninooks.worker;

import static com.comp90018.uninooks.activities.FocusModeTimerActivity.isCurrentlyOnApp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.comp90018.uninooks.service.background_app.BackgroundAppService;

public class FocusModeWorker extends Worker {
    private final Context context;
    private final String TAG = "MyWorker";

    public FocusModeWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "doWork called for: " + this.getId());
        Log.d(TAG, "Service Running: " + BackgroundAppService.isServiceRunning);
        if (!BackgroundAppService.isServiceRunning && !isCurrentlyOnApp) {
            Log.d(TAG, "starting service from doWork");
            Intent intent = new Intent(this.context, BackgroundAppService.class);
            ContextCompat.startForegroundService(context, intent);
        }
        return Result.success();
    }

    @Override
    public void onStopped() {
        Log.d(TAG, "onStopped called for: " + this.getId());
        super.onStopped();
    }
}
