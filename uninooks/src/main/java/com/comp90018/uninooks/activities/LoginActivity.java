package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.models.location.Location;
import com.comp90018.uninooks.models.user.User;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.comp90018.uninooks.service.user.UserServiceImpl;

import java.util.List;

/**
 * Log in activity
 */
public class LoginActivity extends AppCompatActivity {

    int userId;
    String userName;
    String userEmail;
    boolean ifPasswordChanged;
    boolean ifLogout;
    private EditText editTextLoginEmail;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    editTextLoginEmail.setText(userEmail);
            }
        }
    };
    private EditText editTextLoginPassword;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * on create method
     *
     * @param savedInstanceState as savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("uninooks", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        Intent intent = getIntent();
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        handler.sendEmptyMessage(1);

        editTextLoginEmail = findViewById(R.id.EditTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.EditTextLoginPassword);
        Button buttonLoginLogIn = findViewById(R.id.ButtonLoginLogIn);
        TextView buttonLogInGoToSignup = findViewById(R.id.ButtonLogInGoToSignup);
        TextView buttonLogInForgetPassword = findViewById(R.id.ButtonLogInForgetPassword);

        userId = -1;
        userEmail = "";
        userName = "";

        retrieveLoginDetails();

        // log in button
        buttonLoginLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            User user = loginUser();

                            if (user != null) {
                                userId = user.getUserId();
                                userEmail = user.getUserEmail();
                                userName = user.getUserName();

                                saveLoginDetails(userId, userEmail, userName);
                                editor.putBoolean(getString(R.string.PasswordChanged), false);
                                editor.putBoolean(getString(R.string.LogOut), false);
                                editor.apply();

                                if (!sharedPreferences.getBoolean("isIntroOpened", false)) {
                                    Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                                    intent.putExtra("USER_ID_EXTRA", userId);
                                    intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                                    intent.putExtra("USER_NAME_EXTRA", userName);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    launchHomeActivity();
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            showTextMessage("An error happened, please contract the IT administrator.");
                        }
                    }
                }.start();
            }
        });

        // password text
        editTextLoginPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    new Thread() {
                        public void run() {
                            try {
                                User user = loginUser();

                                if (user != null) {
                                    userId = user.getUserId();
                                    userEmail = user.getUserEmail();
                                    userName = user.getUserName();

                                    saveLoginDetails(userId, userEmail, userName);
                                    editor.putBoolean(getString(R.string.PasswordChanged), false);
                                    editor.putBoolean(getString(R.string.LogOut), false);
                                    editor.apply();

                                    if (!sharedPreferences.getBoolean("isIntroOpen", false)) {
                                        Intent intent = new Intent(LoginActivity.this, IntroActivity.class);
                                        intent.putExtra("USER_ID_EXTRA", userId);
                                        intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                                        intent.putExtra("USER_NAME_EXTRA", userName);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        launchHomeActivity();
                                    }

                                }

                            } catch (Exception e) {
                                showTextMessage("An error happened, please contract the IT administrator.");
                            }
                        }
                    }.start();
                    return true;
                }
                return false;
            }
        });

        // sign up button
        buttonLogInGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);

                } catch (Exception e) {
                    showTextMessage("An error happened, please contract the IT administrator.");
                }
            }
        });

        // forget password button
        buttonLogInForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("USER_EMAIL_EXTRA", editTextLoginEmail.getText().toString());
                    startActivity(intent);
                } catch (Exception e) {
                    showTextMessage("An error happened, please contract the IT administrator.");
                }
            }
        });
    }

    public void onResume() {
        super.onResume();
        retrieveLoginDetails();
    }

    /**
     * User log in
     *
     * @return user if log in successful, otherwise null
     * @throws Exception if any exception
     */
    private User loginUser() throws Exception {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {

            showTextMessage("Please fill all fields.");
            return null;
        }

        // @TODO: Here, you can integrate your backend logic to authenticate the user and validate inputs.
        User logInUser = new UserServiceImpl().logIn(email, password);

        if (logInUser == null) {
            showTextMessage("Your input does not match our records, please try again.");
            return null;
        }

        return logInUser;
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


    /**
     * Launch the home activity with needed intents
     */
    private void launchHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

        // Pass the user to next page
        intent.putExtra("USER_ID_EXTRA", userId);
        intent.putExtra("USER_EMAIL_EXTRA", userEmail);
        intent.putExtra("USER_NAME_EXTRA", userName);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Saves users log in details if it is correct to automatically log in the next time
     *
     * @param userId   as user id
     * @param email    as user email
     * @param username as user username
     */
    private void saveLoginDetails(int userId, String email, String username) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.UserId), userId);
        editor.putString(getString(R.string.Email), email);
        editor.putString(getString(R.string.Username), username);
        editor.putString(getString(R.string.Password), editTextLoginPassword.getText().toString().trim());
        editor.apply();
    }

    /**
     * get from sharedPreferences, their userEmail and userName
     * if any of them are empty, then it won't go to the next page, otherwise it will automatically launch
     */
    private void retrieveLoginDetails() {
        userId = sharedPreferences.getInt(getString(R.string.UserId), -1);
        userEmail = sharedPreferences.getString(getString(R.string.Email), "");
        userName = sharedPreferences.getString(getString(R.string.Username), "");
        String password = sharedPreferences.getString(getString(R.string.Password), "");

        ifPasswordChanged = sharedPreferences.getBoolean(getString(R.string.PasswordChanged), false);
        ifLogout = sharedPreferences.getBoolean(getString(R.string.LogOut), false);

        if (!(userId == -1 || userEmail.equals("") || userName.equals("") || password.equals("")) && (!ifPasswordChanged) && (!ifLogout)) {
            launchHomeActivity();
        }
    }



}