package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;
import com.comp90018.uninooks.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class FocusModeMainActivity extends AppCompatActivity {

    ImageView studyGif;

    Button startFocusButton;
    BottomNavigationView bottomNav;

    private int userId;

    private String userEmail;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_main);

        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");

        studyGif = findViewById(R.id.focus_studygif);
        startFocusButton = findViewById(R.id.btn_focus_mode);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.focusNav);

        Glide.with(this).load(R.drawable.study).into(studyGif);

        startFocusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FocusModeMainActivity.this, FocusModeTimerActivity.class);
                // Pass the user to next page
                intent.putExtra("USER_ID_EXTRA", userId);
                intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                intent.putExtra("USER_NAME_EXTRA", userName);
                startActivity(intent);
            }
        });

        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.homeNav){
                    Intent intent = new Intent(FocusModeMainActivity.this, HomeActivity.class);

                    // Pass the user to next page
                    intent.putExtra("USER_ID_EXTRA", userId);
                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                    intent.putExtra("USER_NAME_EXTRA", userName);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }

                else if (id == R.id.searchNav) {
                    Intent intent = new Intent(FocusModeMainActivity.this, MapsActivity.class);

                    // Pass the user to next page
                    intent.putExtra("USER_ID_EXTRA", userId);
                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                    intent.putExtra("USER_NAME_EXTRA", userName);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();

                } else if (id == R.id.focusNav) {
                    ;
                } else {
                    Intent intent = new Intent(FocusModeMainActivity.this, AccountActivity.class);
                    // Pass the user to next page
                    intent.putExtra("USER_ID_EXTRA", userId);
                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                    intent.putExtra("USER_NAME_EXTRA", userName);
                    startActivity(intent);
                }
                return false;
            }
        });
    }
}
