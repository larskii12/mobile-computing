package com.comp90018.uninooks.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.comp90018.uninooks.R;

public class FocusModeMainActivity extends AppCompatActivity {

    ImageView toggleGif;

    ImageView studyGif;

    ImageView goodLuckGif;

    Button startFocusButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_focus_main);

//        toggleGif = findViewById(R.id.focus_toggle);
        studyGif = findViewById(R.id.focus_studygif);
//        goodLuckGif = findViewById(R.id.focus_goodluck);

        startFocusButton = findViewById(R.id.btn_focus_mode);

//        Glide.with(this).load(R.drawable.toggle).into(toggleGif);
        Glide.with(this).load(R.drawable.study).into(studyGif);
//        Glide.with(this).load(R.drawable.good_luck).into(goodLuckGif);

        startFocusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FocusModeMainActivity.this, FocusModeTimerActivity.class);
                startActivity(intent);
            }
        });

        // Initialize unwantedApps list
//        SharedPreferences preferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
//        String unwantedAppsList = preferences.getString("unwanted_apps", "");
//        unwantedApps = new ArrayList<>(Arrays.asList(unwantedAppsList.split(",")));
    }
}
