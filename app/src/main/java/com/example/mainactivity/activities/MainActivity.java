package com.example.mainactivity.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mainactivity.R;
import com.example.mainactivity.service.gps.GPSService;
import com.example.mainactivity.service.gps.GPSServiceImpl;
import com.example.mainactivity.service.location.LocationService;
import com.example.mainactivity.service.review.ReviewService;
import com.example.mainactivity.service.user.UserService;

public class MainActivity extends AppCompatActivity implements GPSService {

    private ReviewService reviewService;

    private LocationService locationService;

    private UserService userService;


    private static final int REQUEST_LOCATION_PERMISSION = 1234;

    GPSServiceImpl gpsService;

    private static Context context;

    private static boolean gpsPermission;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
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
        context = getApplicationContext();
        setContentView(R.layout.activity_main);

        // Ask user to grant permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

        gpsService = new GPSServiceImpl(this, this);
        gpsService.startGPSUpdates();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Button button = (Button) findViewById(R.id.button);
        Button loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            gpsService.stopGPSUpdates();
                            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        // Test button
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            gpsService.stopGPSUpdates();
                            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                            startActivity(intent);
                        }
                      
                        // If exception when operating
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
    }

    /**
     * GPS permission grant ot deny operation
     * @param requestCode The request code passed in {@link #requestPermissions(
     * android.app.Activity, String[], int)}
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gpsPermission = true;
            } else {
                gpsPermission = false;
                showTextMessage("GPS permission is not granted, some functions is limited.");
            }
        }
    }

    // Restart GPS continually monitor
    @Override
    protected void onRestart () {
        super.onRestart();
        gpsService.startGPSUpdates();
    }


    public static Context getAppContext () {
        return context;
    }

    @Override
    public void onGPSUpdate (Location location){
        Log.d("Main TAG Latitude: ", String.valueOf(gpsService.getLatestLocation().getLatitude()));
        Log.d("Main TAG Longitude: ", String.valueOf(gpsService.getLatestLocation().getLongitude()));
        Log.d("Main TAG History: ", gpsService.getGPSHistory().toString());
        Log.d("Main TAG Latest Location: ", gpsService.getLatestLocation().toString());


        new Handler().post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, String.valueOf(gpsService.getLatestLocation().getLatitude()) + "  " + String.valueOf(gpsService.getLatestLocation().getLongitude()), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static boolean getGPSPermission(){
        return gpsPermission;
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