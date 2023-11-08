package com.comp90018.uninooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;


public class FocusModeSplashActivity extends AppCompatActivity {

    private int userId;

    private ProgressBar loading;

    private static final int LOADING_DELAY_MS = 5000; // loading bar for the focus mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_splash);

        loading = findViewById(R.id.progressBar);

        // Initialize user
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 6);

        Handler handler = new Handler();

        // Post a delayed task to be executed after 5 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, LOADING_DELAY_MS);

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

}
