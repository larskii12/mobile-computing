package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.models.location.Location;
import com.comp90018.uninooks.models.user.User;
import com.comp90018.uninooks.service.gps.GPSService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.location.LocationServiceImpl;
import com.comp90018.uninooks.service.user.UserServiceImpl;

import java.util.List;

public class LoginActivity extends AppCompatActivity implements GPSService {

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private Button buttonLoginLogIn;
    private TextView buttonLogInGoToSignup;

    private TextView buttonLogInForgetPassword;

    private GPSServiceImpl gpsService;

    int userId;

    String userName;

    String userEmail;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("USER_EMAIL_EXTRA");
        handler.sendEmptyMessage(1);

        gpsService = new GPSServiceImpl(this, this);

        editTextLoginEmail = findViewById(R.id.EditTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.EditTextLoginPassword);
        buttonLoginLogIn = findViewById(R.id.ButtonLoginLogIn);
        buttonLogInGoToSignup = findViewById(R.id.ButtonLogInGoToSignup);
        buttonLogInForgetPassword = findViewById(R.id.ButtonLogInForgetPassword);

        userId = -1;
        userEmail = "";
        userName = "";

        buttonLoginLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {

                            User user = loginUser();

                            if (user != null){

                                userId = user.getUserId();
                                userEmail = user.getUserEmail();
                                userName = user.getUserName();

                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);

                                // Pass the user to next page
                                intent.putExtra("USER_ID_EXTRA", userId);
                                intent.putExtra("USER_EMAIL_EXTRA", userEmail);
                                intent.putExtra("USER_NAME_EXTRA", userName);

                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }

                        } catch (Exception e) {
                            showTextMessage("An error happened, please contract the IT administrator.");
                        }
                    }
                }.start();
            }
        });

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

        buttonLogInForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                    intent.putExtra("USER_EMAIL_EXTRA", editTextLoginEmail.getText().toString());
                    startActivity(intent);
                }
                catch (Exception e){
                    showTextMessage("An error happened, please contract the IT administrator.");
                }
            }
        });
    }

    public void onStart(){
        super.onStart();
        gpsService.startGPSUpdates();
    }

    public void onRestart(){
        super.onRestart();
        gpsService.startGPSUpdates();
    }

    // When back button pressed
    public void onBackPressed() {
        super.onBackPressed();
        gpsService.stopGPSUpdates();
    }

    public void onPause() {
        super.onPause();
        gpsService.stopGPSUpdates();
    }
    public void onResume() {
        super.onResume();
        gpsService.startGPSUpdates();
    }

    public void onStop(){
        super.onStop();
        gpsService.stopGPSUpdates();
    }

    public void onDestroy(){
        super.onDestroy();
        gpsService.stopGPSUpdates();
    }


    private User loginUser() throws Exception {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {

            showTextMessage("Please fill all fields.");
            return null;
        }

        // @TODO: Here, you can integrate your backend logic to authenticate the user and validate inputs.
        User logInUser = new UserServiceImpl().logIn(email, password);
        List<Location> results = new LocationServiceImpl().findAllLocations("STUDY", "ERC", false);
        System.out.println(results.get(0).getName());

        if (logInUser == null) {
            showTextMessage("Your input does not match our records, please try again.");
            return null;
        }
      
        return logInUser;
    }

    /**
     * Show message text
     * @param text as the showing message
     */
    private void showTextMessage(String text){
        Message msg = new Message();
        msg.what = 0;
        msg.obj = text;
        handler.sendMessage(msg);
    }

    @Override
    public void onGPSUpdate(android.location.Location location) {
        gpsService.stopGPSUpdates();
    }
}