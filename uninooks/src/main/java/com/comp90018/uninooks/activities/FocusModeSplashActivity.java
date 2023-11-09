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
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.background_app.BackgroundAppService;

/**
 * Focus mode splash screen activity
 */
public class FocusModeSplashActivity extends AppCompatActivity {

    private static final int LOADING_DELAY_MS = 1000; // loading bar for the focus mode
    private static final int REQUEST_USAGE_ACCESS = 1001;
    private static final int APP_NOTIFICATION_PUSH = 1002;
    SharedPreferences.Editor editor;
    private int userId;
    private String userEmail;
    private String userName;
    private ProgressBar loading;
    /**
     * Handler to handle the UI thread change
     */
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
        public void handleMessage(Message msg) {
            if (msg.what == 0) {//focusButton.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);

                try {
                    Intent intent = new Intent(FocusModeSplashActivity.this, FocusModeTimerActivity.class);
                    // Pass the user to next page
                    intent.putExtra("USER_ID_EXTRA", userId);
                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                    intent.putExtra("USER_NAME_EXTRA", userName);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    };
    /**
     * To do after notification is granted on Android 13+ (API 33+)
     */
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            BackgroundAppService.NOTIFICATION_STATUS = true;

            // Disable the second time permission query
            editor.putBoolean("isFocusModeFirstTimeLaunch", false);
            editor.apply();
        } else {
            BackgroundAppService.NOTIFICATION_STATUS = false;

            // Disable the second time permission query
            editor.putBoolean("isFocusModeFirstTimeLaunch", false);
            editor.apply();
        }

        handler.sendEmptyMessageDelayed(0, LOADING_DELAY_MS);
    });

    /**
     * on create method
     *
     * @param savedInstanceState as savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_splash);

        // Initialize user
        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");

        loading = findViewById(R.id.progressBar);

        // Log whether is first time launch, ask for permission only if first time launch
        SharedPreferences sharedPreferences = getSharedPreferences("uninooks", MODE_PRIVATE);
        boolean isFocusModeFirstTimeLaunch = sharedPreferences.getBoolean("isFocusModeFirstTimeLaunch", true);

        editor = sharedPreferences.edit();

        // Check permission is given or not, if not given, pop up permission needed box
        // Need to change to check whether the app is first time launch
        if (isFocusModeFirstTimeLaunch) {

            // first time launch focus mode, inside method has Android Version Check
            firstTimeLaunchFocusMode();
        }

        // If not first time launch
        else {

            BackgroundAppService.USAGE_STATUS = hasUsageAccessPermission();
            BackgroundAppService.NOTIFICATION_STATUS = hasNotificationPermission();

            handler.sendEmptyMessageDelayed(0, LOADING_DELAY_MS);
        }
    }

    /**
     * First time to launch the focus mode, asking for permissions
     */
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void firstTimeLaunchFocusMode() {

        // Show user what permission needed
        new AlertDialog.Builder(this).setTitle("Permissions Required").setMessage(Html.fromHtml("For Focus Mode to function optimally, it requires the following permissions:<br><br>" + "1. <b><font color='red'>Usage Access</font></b>: To monitor your study activity.<br><br>" + "2. <b><font color='red'>Notification Access</font></b>: To notify your study activity.<br><br>" + "Your personal information will remain private and will not be collected or shared with any third-party entities.")).setPositiveButton("I understand", (dialog, which) -> {

            // Ask for usage permission
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivityForResult(intent, REQUEST_USAGE_ACCESS);

        }).setCancelable(false).show();
    }

    /**
     * Check whether usage permission has been granted
     *
     * @return true if yes, otherwise false
     */
    private boolean hasUsageAccessPermission() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * Check whether notification permission has been granted
     *
     * @return true if yes, otherwise false
     */
    private boolean hasNotificationPermission() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.getAppContext());
        return notificationManagerCompat.areNotificationsEnabled();
    }

    /**
     * When usage or notification permission query end, processing the notification permission
     *
     * @param requestCode as the permission code
     * @param resultCode  as the permission query result code
     * @param data        as the intent data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // If request is usage access
        if (requestCode == REQUEST_USAGE_ACCESS) {

            // If usage permission granted
            if (hasUsageAccessPermission()) {
                BackgroundAppService.USAGE_STATUS = true;

                // If version is higher or equal than Android 13, request notification permission with pop up box
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                } else {
                    // If version is lower than Android 13, request notification by bring user to the app notification permission page
                    Intent intentNotification = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivityForResult(intentNotification, APP_NOTIFICATION_PUSH);
                }
            }

            // If usage permission NOT granted
            else {
                BackgroundAppService.USAGE_STATUS = false;

                // If version is higher or equal than Android 13, request notification permission with pop up box
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
                } else {
                    // If version is lower than Android 13, request notification by bring user to the app notification permission page
                    Intent intentNotification = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    startActivityForResult(intentNotification, APP_NOTIFICATION_PUSH);

                }
            }
        }

        // If request is notification, to do after notification is granted on Android 12 or lower version (API 31-)
        if (requestCode == APP_NOTIFICATION_PUSH) {

            // If notification permission granted
            if (hasNotificationPermission()) {

                BackgroundAppService.NOTIFICATION_STATUS = true;

                // Disable the second time permission query
                editor.putBoolean("isFocusModeFirstTimeLaunch", false);
                editor.apply();
                handler.sendEmptyMessageDelayed(0, LOADING_DELAY_MS);

            }

            // If notification permission NOT granted
            else {
                BackgroundAppService.NOTIFICATION_STATUS = false;

                // Disable the second time permission query
                editor.putBoolean("isFocusModeFirstTimeLaunch", false);
                editor.apply();
                handler.sendEmptyMessageDelayed(0, LOADING_DELAY_MS);
            }
        }

    }
}
