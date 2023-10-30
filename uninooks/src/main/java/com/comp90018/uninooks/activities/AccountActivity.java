package com.comp90018.uninooks.activities;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;

public class AccountActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Get reference to the Personal Information LinearLayout
        LinearLayout personalInfoLayout = findViewById(R.id.Account_Personal_Info_Layout); // Set an ID for your LinearLayout in XML and use it here

        // Set an OnClickListener
        personalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Personal Information Activity
                Intent intent = new Intent(AccountActivity.this, PersonalInformationActivity.class); // Assume the name of your next activity is PersonalInformationActivity
                startActivity(intent);
            }
        });
    }
}
