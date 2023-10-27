package com.comp90018.uninooks.service.background_app;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Build;

import com.comp90018.uninooks.models.background_app.BackgroundApp;

import java.util.ArrayList;
import java.util.List;

public class BackgroundAppServiceImpl implements BackgroundAppService{

    Context context;
    ArrayList<BackgroundApp> backgroundApps = new ArrayList<>();

    public BackgroundAppServiceImpl(Context context){
        this.context = context;
    }
    @Override
    public ArrayList<BackgroundApp> getBackgroundApps(long duration) {

        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();

        List<UsageStats> HistoryUsedAppList = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - duration, currentTime);

        // Check android version, if less than 10 use the last time used, if higher than 10, use last time visible
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            for (UsageStats historyApp : HistoryUsedAppList) {
                if (historyApp.getLastTimeVisible() > currentTime - duration) {

                    BackgroundApp newBackgroundApp = new BackgroundApp();
                    newBackgroundApp.setLastTimeUsed(historyApp.getLastTimeVisible());
                    newBackgroundApp.setPackageName(historyApp.getPackageName());

                    backgroundApps.add(newBackgroundApp);
                }
            }
        }

        else {
            for (UsageStats historyApp : HistoryUsedAppList) {
                if (historyApp.getLastTimeUsed() > currentTime - duration) {

                    BackgroundApp newBackgroundApp = new BackgroundApp();
                    newBackgroundApp.setLastTimeUsed(historyApp.getLastTimeUsed());
                    newBackgroundApp.setPackageName(historyApp.getPackageName());

                    backgroundApps.add(newBackgroundApp);

                }
            }
        }
        return backgroundApps;
    }
}
