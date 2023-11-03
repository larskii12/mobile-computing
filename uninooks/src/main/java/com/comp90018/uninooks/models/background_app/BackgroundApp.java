package com.comp90018.uninooks.models.background_app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BackgroundApp {

    private String packageName;

    private long lastTimeUsed;

    public String getLastTimeUsed() {

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(lastTimeUsed));
    }

    public void setLastTimeUsed(long lastTimeUsed) {
        this.lastTimeUsed = lastTimeUsed;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
