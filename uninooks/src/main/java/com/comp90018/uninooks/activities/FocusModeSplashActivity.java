package com.comp90018.uninooks.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.background_app.BackgroundAppService;


public class FocusModeSplashActivity extends AppCompatActivity {

    private int userId;
    private String userEmail;
    private String userName;

    private ProgressBar loading;

    private static final int LOADING_DELAY_MS = 1000 * 3; // loading bar for the focus mode

    SharedPreferences.Editor editor;

    private static final int REQUEST_USAGE_ACCESS = 1001;

    private static final int APP_NOTIFICATION_PUSH = 1002;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    // This code will run after the specified delay (5 seconds)
                    // You can perform your loading or any other task here
                    loading.setVisibility(View.GONE);
                    //focusButton.setVisibility(View.VISIBLE);
                    try {
                        Intent intent = new Intent(FocusModeSplashActivity.this, FocusModeMainActivity.class);
                        startActivity(intent);
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException(e);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_splash);

        // Initialize user
        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");

        loading = findViewById(R.id.progressBar);

        SharedPreferences sharedPreferences = getSharedPreferences("uninooks", MODE_PRIVATE);
        boolean isFocusModeFirstTimeLaunch = sharedPreferences.getBoolean("isFocusModeFirstTimeLaunch", true);

        editor = sharedPreferences.edit();

        // Check permission is given or not, if not given, pop up permission needed box
        // Need to change to check whether the app is first time launch
        if (isFocusModeFirstTimeLaunch) {

            // first time launch focus mode
            firstTimeLaunchFocusMode();
        }

        // If not first time launch
        else{

            BackgroundAppService.USAGE_STATUS = hasUsageAccessPermission();
            BackgroundAppService.NOTIFICATION_STATUS = hasNotificationPermission();

            Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.USAGE_STATUS));
            Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.NOTIFICATION_STATUS));

            handler.sendEmptyMessageDelayed(0, LOADING_DELAY_MS);
        }
    }

    public void onStart(){
        super.onStart();
    }

    public void onRestart(){
        super.onRestart();
    }

    // When back button pressed
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void firstTimeLaunchFocusMode() {

        new AlertDialog.Builder(this)
                .setTitle("Permissions Required")
                .setMessage(Html.fromHtml("For Focus Mode to function optimally, it requires the following permissions:<br><br>"
                        + "1. <b><font color='red'>Usage Access</font></b>: To monitor your study activity.<br><br>"
                        + "2. <b><font color='red'>Notification Access</font></b>: To notify your study activity.<br><br>"
                        + "Your personal information will remain private and will not be collected or shared with any third-party entities."))
                .setPositiveButton("I understand", (dialog, which) -> {

                    // Ask for usage permission
                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivityForResult(intent, REQUEST_USAGE_ACCESS);

                })
                .setCancelable(false)
                .show();
    }

    private boolean hasUsageAccessPermission() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(MainActivity.getAppContext().APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean hasNotificationPermission() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.getAppContext());
        return notificationManagerCompat.areNotificationsEnabled();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_USAGE_ACCESS) {
            if (hasUsageAccessPermission()) {
                BackgroundAppService.USAGE_STATUS = true;


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }

                else {

                    // Ask for notification for old android
                    Intent intentNotification = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivityForResult(intentNotification, APP_NOTIFICATION_PUSH);
                }
            }

            else {
                BackgroundAppService.USAGE_STATUS = false;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                }

                else {

                    Intent intentNotification = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                            .putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivityForResult(intentNotification, APP_NOTIFICATION_PUSH);

                }
            }
        }


        if (requestCode == APP_NOTIFICATION_PUSH){
            if (hasNotificationPermission()){

                BackgroundAppService.NOTIFICATION_STATUS = true;

                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.USAGE_STATUS));
                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.NOTIFICATION_STATUS));

                editor.putBoolean("isFocusModeFirstTimeLaunch", false);
                editor.apply();
                handler.sendEmptyMessageDelayed(0, LOADING_DELAY_MS);

            }

            else{
                BackgroundAppService.NOTIFICATION_STATUS = false;

                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.USAGE_STATUS));
                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.NOTIFICATION_STATUS));

                editor.putBoolean("isFocusModeFirstTimeLaunch", false);
                editor.apply();
                handler.sendEmptyMessageDelayed(0, LOADING_DELAY_MS);
            }
        }

    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
        registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                BackgroundAppService.NOTIFICATION_STATUS = true;

                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.USAGE_STATUS));
                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.NOTIFICATION_STATUS));


                editor.putBoolean("isFocusModeFirstTimeLaunch", false);
                editor.apply();
            } else {
                BackgroundAppService.NOTIFICATION_STATUS = false;

                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.USAGE_STATUS));
                Log.d("XXXXXXXXXXXXXXXXXXXXXXXXX", String.valueOf(BackgroundAppService.NOTIFICATION_STATUS));

                editor.putBoolean("isFocusModeFirstTimeLaunch", false);
                editor.apply();
            }

            handler.sendEmptyMessageDelayed(0, LOADING_DELAY_MS);
        });
}
