package com.comp90018.uninooks.service.background_app;

import com.comp90018.uninooks.models.background_app.BackgroundApp;

import java.util.ArrayList;

public interface BackgroundAppService {

    ArrayList<BackgroundApp> getBackgroundApps(long duration);
}
