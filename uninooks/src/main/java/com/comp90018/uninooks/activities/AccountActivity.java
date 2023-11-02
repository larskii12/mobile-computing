package com.comp90018.uninooks.activities;


import android.annotation.SuppressLint;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.models.user.User;
import com.comp90018.uninooks.service.user.UserServiceImpl;

public class AccountActivity extends AppCompatActivity {

    private TextView textViewAccountGreetingUserName;

    private String userName;

    private int userId;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {

                // Show message
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;

                // Show greeting user name
                case 1:
                    textViewAccountGreetingUserName.setText(userName);

            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Initialize user
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 6);

        // Get reference to the Personal Information LinearLayout
        LinearLayout personalInfoLayout = findViewById(R.id.Account_Personal_Info_Layout); // Set an ID for your LinearLayout in XML and use it here
        LinearLayout logoutLayout = findViewById(R.id.Account_Logout_Layout);
        LinearLayout deleteAccountLayout = findViewById(R.id.Account_Delete_Account_Layout);

        // Get username
        textViewAccountGreetingUserName = findViewById(R.id.Account_Greeting_Username);

        // Set an OnClickListener
        personalInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Personal Information Activity
                Intent intent = new Intent(AccountActivity.this, PersonalInformationActivity.class); // Assume the name of your next activity is PersonalInformationActivity

                // Pass the user to next page
                intent.putExtra("userId", userId);

                startActivity(intent);
            }
        });

        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutDialog();
            }
        });

        deleteAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteAccountDialog();
            }
        });

        // Show account greeting name
        new Thread(){
            public void run(){
                try {
                    showAccountGreetingUsername();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }.start();
    }

    private void showAccountGreetingUsername() throws Exception {

        User user = new UserServiceImpl().getUser(userId);

        userName = user.getUserName();
        handler.sendEmptyMessage(1);
    }

    private void showLogoutDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.logout_confirmation_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialogView.findViewById(R.id.dialog_positive_btn);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout logic here
                dialog.dismiss();
            }
        });

        Button negativeButton = dialogView.findViewById(R.id.dialog_negative_btn);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showDeleteAccountDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_account_confirmation_dialog, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialogView.findViewById(R.id.account_Delete_Confirm_Button);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle logout logic here
                dialog.dismiss();
            }
        });

        Button negativeButton = dialogView.findViewById(R.id.account_delete_Cancel_Button);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}

