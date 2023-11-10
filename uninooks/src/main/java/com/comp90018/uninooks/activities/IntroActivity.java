package com.comp90018.uninooks.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.comp90018.uninooks.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Intro Activity
 */
public class IntroActivity extends AppCompatActivity {
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    Button btnNext, btnGetStarted;
    LinearLayout linearLayoutNext, linearLayoutGetStarted;
    int userId;
    String userEmail;
    String userName;
    boolean isIntroActivityOpened;
    private ViewPager screenPager;

    String onboarding_1;
    String onboarding_2;
    String onboarding_3;

    /**
     * on create method
     *
     * @param savedInstanceState as savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        userId = intent.getIntExtra("USER_ID_EXTRA", 0);
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        userName = intent.getStringExtra("USER_NAME_EXTRA");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_intro);

        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        linearLayoutNext = findViewById(R.id.linear_layout_next);
        linearLayoutGetStarted = findViewById(R.id.linear_layout_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);

        onboarding_1 = getString(R.string.onboarding_page_1);
        onboarding_2 = getString(R.string.onboarding_page_2);
        onboarding_3 = getString(R.string.onboarding_page_3);
        //Data
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem(getString(R.string.onboarding_page_1_title), onboarding_1, R.drawable.homepage_screenshot));
        mList.add(new ScreenItem(getString(R.string.onboarding_page_2_title), onboarding_2, R.drawable.search_page_screenshot));
        mList.add(new ScreenItem(getString(R.string.onboarding_page_3_title), onboarding_3, R.drawable.focus_mode_screenshot));

        //Setup viewPager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        //Setup tab indicator
        tabIndicator.setupWithViewPager(screenPager);

        //Button Next
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                screenPager.setCurrentItem(screenPager.getCurrentItem() + 1, true);
            }
        });

        tabIndicator.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Button Get Started
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
                intent.putExtra("USER_ID_EXTRA", userId);
                intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                intent.putExtra("USER_NAME_EXTRA", userName);

                savePrefsData();
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Save user shared preference data
     */
    private void savePrefsData() {
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("uninooks", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        isIntroActivityOpened = preferences.getBoolean("isIntroOpened", true);
        editor.putBoolean("isIntroOpened", true);
        editor.apply();
    }

    /**
     * Load last screen
     */
    private void loadLastScreen() {
        linearLayoutNext.setVisibility(View.INVISIBLE);
        linearLayoutGetStarted.setVisibility(View.VISIBLE);
    }
}