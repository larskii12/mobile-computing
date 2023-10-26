package com.comp90018.uninooks.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1234;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        AppOpsManager appUsage = (AppOpsManager) this.getSystemService(Context.APP_OPS_SERVICE);

        // Check permission is given or not, if not given, pop up permission needed box
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) || (appUsage.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), this.getPackageName()) != AppOpsManager.MODE_ALLOWED)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permissions Required")
                    .setMessage("For your best experience, Uninook needs two permissions, usage data for study mode monitor and location for recommendation. Your data is store on your local device and not going to share with any third-party even Uninook.")
                    .setPositiveButton("I understand", (dialog, which) -> {

                        // Check GPS permission granted
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                        } else {
                            GPSServiceImpl.setGPSPermissionStatus(true);
                        }

                        // Check device usage history permission granted
                        if (appUsage.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), this.getPackageName()) != AppOpsManager.MODE_ALLOWED) {

                            // If not granted, guide user to access settings, otherwise pass.
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            this.startActivity(intent);
                        } else {
                            startMainActivity();
                        }


                    })
                    .setCancelable(false)
                    .show();
        }

        // If all permission already granted, go to main activities directly
        else{
            startMainActivity();
        }

    }

    public void onStart(){
        super.onStart();
    }

    public void onRestart(){
        super.onRestart();;
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

    public void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GPSServiceImpl.setGPSPermissionStatus(true);
                startMainActivity();
            } else {
                GPSServiceImpl.setGPSPermissionStatus(false);
                showTextMessage("Location permission not granted, some functions may not work properly.");
                startMainActivity();
            }
        }
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

}
