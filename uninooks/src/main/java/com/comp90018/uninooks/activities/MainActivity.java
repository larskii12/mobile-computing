package com.comp90018.uninooks.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.emulator.EmulatorServiceImpl;
import com.comp90018.uninooks.service.gps.GPSService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;

/**
 * Main Activity
 */
@SuppressLint("CustomSplashScreen")
public class MainActivity extends AppCompatActivity implements GPSService {

    private static final int REQUEST_LOCATION_PERMISSION = 1234;
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                String info = (String) msg.obj;
                Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private GPSServiceImpl gpsService;

    public static Context getAppContext() {
        return context;
    }

    /**
     * on create method
     *
     * @param savedInstanceState as savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        gpsService = new GPSServiceImpl(this, this);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Ask for permission
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);

        /**
         * Emulator Testing Mode Detection
         */
        if (EmulatorServiceImpl.isEmulator()) {
            showTextMessage("Emulator Testing Mode\nLocation: Melbourne Connect");
        }
    }

    public void onStart() {
        super.onStart();
    }

    public void onRestart() {
        super.onRestart();
    }

    // When back button pressed
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
    }

    public void onStop() {
        super.onStop();
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // Update first launch is done and start Main Activity
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GPSServiceImpl.setGPSPermissionStatus(true);
                gpsService.startGPSUpdates();

            } else {
                GPSServiceImpl.setGPSPermissionStatus(false);
                startLoginActivity();
            }
        }
    }

    /**
     * Start the Main Activity
     */
    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    /**
     * Show message text
     *
     * @param text as the showing message
     */
    private void showTextMessage(String text) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    @Override
    public void onGPSUpdate(Location location) {
        gpsService.stopGPSUpdates();
        startLoginActivity();
    }
}