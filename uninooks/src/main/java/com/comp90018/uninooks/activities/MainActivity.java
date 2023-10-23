package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.location.LocationService;
import com.comp90018.uninooks.service.review.ReviewService;
import com.comp90018.uninooks.service.user.UserService;

public class MainActivity extends AppCompatActivity{

    private ReviewService reviewService;

    private LocationService locationService;

    private UserService userService;

    private static Context context;

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
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
//        }

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

//    /**
//     * GPS permission grant ot deny operation
//     * @param requestCode The request code passed in {@link #requestPermissions(
//     * android.app.Activity, String[], int)}
//     * @param permissions The requested permissions. Never null.
//     * @param grantResults The grant results for the corresponding permissions
//     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
//     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
//     *
//     */
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == REQUEST_LOCATION_PERMISSION) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                gpsPermission = true;
//            } else {
//                gpsPermission = false;
//            }
//        }
//    }

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
        super.onStop();;
    }

    public void onDestroy(){
        super.onDestroy();;
    }


    public static Context getAppContext () {
        return context;
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