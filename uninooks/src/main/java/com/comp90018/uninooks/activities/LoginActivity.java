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

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("SetTextI18n")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        gpsService = new GPSServiceImpl(this, this);

        editTextLoginEmail = findViewById(R.id.EditTextLoginEmail);
        editTextLoginPassword = findViewById(R.id.EditTextLoginPassword);
        buttonLoginLogIn = findViewById(R.id.ButtonLoginLogIn);
        buttonLogInGoToSignup = findViewById(R.id.ButtonLogInGoToSignup);
        buttonLogInForgetPassword = findViewById(R.id.ButtonLogInForgetPassword);

        buttonLoginLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            loginUser();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();
            }
        });

        buttonLogInGoToSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {

                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                        startActivity(intent);
                    }
                }.start();
            }
        });

        buttonLogInForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                        startActivity(intent);
                    }
                }.start();
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



    private int loginUser() throws Exception {
        String email = editTextLoginEmail.getText().toString().trim();
        String password = editTextLoginPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {

            showTextMessage("Please fill all fields.");
            return -1;
        }

        // @TODO: Here, you can integrate your backend logic to authenticate the user and validate inputs.
        User logInUser = new UserServiceImpl().logIn(email, password);
        List<Location> results = new LocationServiceImpl().findAllLocations("STUDY", "ERC", false);
        System.out.println(results.get(0).getName());

        if (logInUser == null){

            showTextMessage("Your input does not match our records, please try again.");
        }

        else{

            System.out.println("User login successful");
            System.out.println("User ID: " + logInUser.getUserId());
            System.out.println("User Name: " + logInUser.getUserName());
            System.out.println("User Email: " + logInUser.getUserEmail());
            System.out.println("User Faculty: " + logInUser.getUserFaculty());
            System.out.println("User AQF level: " + logInUser.getUserAQFLevel());

            // Show login successful message
            String message = logInUser.getUserId() + " " + logInUser.getUserName()
                    + " " + logInUser.getUserEmail()
                    + " " + logInUser.getUserFaculty()
                    + " " + logInUser.getUserAQFLevel();
            showTextMessage("Login successfully!\n" + message);

//            Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            // Pass the user to next page
            intent.putExtra("userId", logInUser.getUserId());
            intent.putExtra("userId", logInUser.getUserName());

            startActivity(intent);
            finish();

        }

        return 0;
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