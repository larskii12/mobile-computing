package com.comp90018.uninooks.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.models.user.User;
import com.comp90018.uninooks.service.background_app.BackgroundAppService;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.mail.MailServiceImpl;
import com.comp90018.uninooks.service.user.UserServiceImpl;

/**
 * Personal information activity
 */
public class PersonalInformationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private final int OTP_TIMER = 20;
    private final int APP_LOCATION_SETTINGS_REQUEST = 1234;
    private final int REQUEST_USAGE_ACCESS = 1001;
    private final int APP_NOTIFICATION_PUSH = 1002;
    /**
     * Detects whether the notification toggle was checked or not
     */
    private final View.OnClickListener notificationToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateNotificationPermission();
        }
    };
    /**
     * Detects whether the usage access toggle was checked or not
     */
    private final View.OnClickListener usageAccessToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateUsagePermission();
        }
    };
    /**
     * Detects whether the precise location toggle was checked or not
     */
    private final View.OnClickListener locationToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updatePreciseLocationPermission();
        }
    };
    private Spinner spinnerChangeFacultyList;
    private ArrayAdapter<CharSequence> facultyChangeAdapter;
    private Spinner spinnerChangeDegreeList;
    private ArrayAdapter<CharSequence> degreeChangeAdapter;
    private int userId;
    private String userName;
    private String userEmail;
    private String userFaculty;
    private String userDegree;
    private TextView userNameTextView;
    private TextView emailTextView;
    private TextView degreeTextView;
    private TextView facultyTextView;
    private EditText editUsernameEditText;
    private Button buttonNewUserName;
    private EditText editTextNewEmail;
    private Button buttonNewEmailGetOTP;
    private EditText editTextEmailOTP;
    private Button buttonNewEmailVerifyOTP;
    private TextView passwordTextView;
    private EditText editCurrentPassword;
    private EditText editNewPassword;
    private EditText editConfirmPassword;
    private Button buttonConfirmNewPassword;
    private Button buttonNewFaculty;
    private Button buttonNewDegree;
    private String otp;
    private String newUserEmail;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Switch shakeToggle;
    /**
     * Detects whether the shake function toggle was checked or not
     */
    private final View.OnClickListener shakeToggleListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editor.putBoolean(getString(R.string.shaking_enabled), shakeToggle.isChecked());
            editor.apply();
        }
    };
    private Switch notificationToggle;
    private Switch usageAccessToggle;
    private Switch preciseLocationToggle;

    /**
     * On create method
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_perfonal_info);

        sharedPreferences = getSharedPreferences("uninooks", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", 6);

        ImageView backArrow = findViewById(R.id.Account_Back_to_Home_Arrow_Left);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        userNameTextView = findViewById(R.id.Account_Pi_Edit_Name);
        buttonNewUserName = findViewById(R.id.Pi_ButtonConfirmNewUserName);
        editUsernameEditText = findViewById(R.id.EditTextNewUsername);

        editTextNewEmail = findViewById(R.id.EditTextConfirmNewEmail);
        buttonNewEmailGetOTP = findViewById(R.id.Pi_ButtonConfirmNewEmail);
        editTextEmailOTP = findViewById(R.id.Pi_EditTextEmailOTP);
        buttonNewEmailVerifyOTP = findViewById(R.id.Pi_ButtonOTPVerify);

        passwordTextView = findViewById(R.id.Account_Pi_Edit_Password);
        editCurrentPassword = findViewById(R.id.Pi_EditTextCurrentPassword);
        editNewPassword = findViewById(R.id.Pi_EditTextNewPassword);
        editConfirmPassword = findViewById(R.id.Pi_EditTextConfirmNewPassword);
        buttonConfirmNewPassword = findViewById(R.id.Pi_ButtonConfirmNewPassword);

        buttonNewFaculty = findViewById(R.id.Pi_ButtonConfirmNewFaculty);
        buttonNewDegree = findViewById(R.id.Pi_ButtonConfirmNewDegree);

        shakeToggle = findViewById(R.id.shakeFunctionToggle);
        notificationToggle = findViewById(R.id.notificationToggle);
        usageAccessToggle = findViewById(R.id.usageAccessToggle);
        preciseLocationToggle = findViewById(R.id.locationToggle);

        shakeToggle.setOnClickListener(shakeToggleListener);
        notificationToggle.setOnClickListener(notificationToggleListener);
        usageAccessToggle.setOnClickListener(usageAccessToggleListener);
        preciseLocationToggle.setOnClickListener(locationToggleListener);

        // retrieve the current permissions from the user, and set whether it has been checked or not
        // pass string, activity (this) --> to get the boolean

        otp = "";

        buttonNewUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUsernameEditText.getText().toString().trim().isEmpty()) {
                    showTextMessage("User Name cannot be empty");
                } else if (!editUsernameEditText.getText().toString().matches("\\w{1,10}")) {
                    showTextMessage("User name can only contain letters, numbers, and underscores, less than 10 characters");
                } else {
                    new Thread() {
                        public void run() {
                            try {
                                new UserServiceImpl().updateUserName(userId, editUsernameEditText.getText().toString());
                                reloadActivity();
                                showTextMessage("User name updated successfully.");
                            } catch (Exception e) {
                                showTextMessage(e.getMessage());
                            }
                        }
                    }.start();
                }
            }
        });

        ImageView editNameIcon = findViewById(R.id.Account_Pi_Ic_Edit_Name);
        editNameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUsernameEditText.getVisibility() == View.GONE) {
                    editUsernameEditText.setVisibility(View.VISIBLE);
                    buttonNewUserName.setVisibility(View.VISIBLE);
                    userNameTextView.setVisibility(View.GONE);
                } else {
                    editUsernameEditText.setVisibility(View.GONE);
                    buttonNewUserName.setVisibility(View.GONE);
                    userNameTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        emailTextView = findViewById(R.id.Account_Pi_Edit_Email);
        ImageView editEmailIcon = findViewById(R.id.Account_Pi_Ic_Edit_Email);

        editEmailIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextNewEmail.getVisibility() == View.GONE) {

                    editTextNewEmail.setVisibility(View.VISIBLE);
                    buttonNewEmailGetOTP.setVisibility(View.VISIBLE);
                    editTextEmailOTP.setVisibility(View.VISIBLE);
                    buttonNewEmailVerifyOTP.setVisibility(View.VISIBLE);
                    emailTextView.setVisibility(View.GONE);

                } else {
                    editTextNewEmail.setVisibility(View.GONE);
                    buttonNewEmailGetOTP.setVisibility(View.GONE);
                    editTextEmailOTP.setVisibility(View.GONE);
                    buttonNewEmailVerifyOTP.setVisibility(View.GONE);
                    emailTextView.setVisibility(View.VISIBLE);
                }
            }
        });


        // Change Password
        buttonNewEmailGetOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {

                            // If email is empty
                            if (editTextNewEmail.getText().toString().trim().isEmpty()) {
                                showTextMessage("Email cannot be empty");
                            } else if (editTextNewEmail.getText().toString().equals(userEmail)) {
                                showTextMessage("New email cannot same as the current email.");
                            } else if (new UserServiceImpl().hasUser(editTextNewEmail.getText().toString())) {
                                showTextMessage("This email has been registered with us, please try another one.");
                            } else if (!editTextNewEmail.getText().toString().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
                                showTextMessage("This is not a valid email address format.");
                            } else {
                                Message msg = new Message();
                                msg.what = 2;
                                msg.obj = OTP_TIMER;
                                handler.sendMessage(msg);

                                // Lock the new email
                                newUserEmail = editTextNewEmail.getText().toString();

                                otp = getOTP();
                            }

                        } catch (Exception e) {
                            throw new RuntimeException("An error happened, please contact the IT administrator");
                        }
                    }
                }.start();
            }
        });


        buttonNewEmailVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread() {
                    public void run() {
                        if (!otp.isEmpty() && otp.equals(editTextEmailOTP.getText().toString())) {
                            try {
                                new UserServiceImpl().updateUserEmail(userId, newUserEmail);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }

                            showTextMessage("Email updated successfully.");
                            reloadActivity();
                        } else {
                            showTextMessage("OTP is not correct.");
                        }
                    }
                }.start();
            }
        });

        // Change Password
        ImageView editPasswordIcon = findViewById(R.id.Account_Pi_Ic_Edit_Password);
        editPasswordIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editCurrentPassword.getVisibility() == View.GONE) {
                    editCurrentPassword.setVisibility(View.VISIBLE);
                    editNewPassword.setVisibility(View.VISIBLE);
                    editConfirmPassword.setVisibility(View.VISIBLE);
                    buttonConfirmNewPassword.setVisibility(View.VISIBLE);
                    passwordTextView.setVisibility(View.GONE);
                } else {
                    editCurrentPassword.setVisibility(View.GONE);
                    editNewPassword.setVisibility(View.GONE);
                    editConfirmPassword.setVisibility(View.GONE);
                    buttonConfirmNewPassword.setVisibility(View.GONE);
                    passwordTextView.setVisibility(View.VISIBLE);
                }

            }
        });

        // Change password button
        buttonConfirmNewPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassWord = editCurrentPassword.getText().toString();
                String newPassword = editNewPassword.getText().toString();
                String newPassWordConfirm = editConfirmPassword.getText().toString();

                if (oldPassWord.trim().isEmpty()) {
                    showTextMessage("Your old password is incorrect.");
                } else if (newPassword.length() < 8 || newPassWordConfirm.length() < 8) {
                    showTextMessage("New Password at least 8 characters.");
                } else if (!newPassword.equals(newPassWordConfirm)) {
                    showTextMessage("Your new password is not identical.");
                } else {
                    new Thread() {
                        public void run() {

                            try {

                                if (new UserServiceImpl().logIn(userEmail, oldPassWord) == null) {
                                    showTextMessage("Your old password is incorrect.");
                                } else {
                                    new UserServiceImpl().updateUserPassword(userId, oldPassWord, newPassword);
                                    editor.putBoolean(getString(R.string.PasswordChanged), true);
                                    editor.apply();
                                    showTextMessage("Password updated successfully.");
                                    reloadActivity();
                                }
                            } catch (Exception e) {
                                showTextMessage("An error happens, pleas contact the IT administrator.");
                            }
                        }
                    }.start();
                }
            }
        });

        spinnerChangeDegreeList = findViewById(R.id.SpinnerPiDegreeList);

        // Set the degree drop down list
        degreeChangeAdapter = ArrayAdapter.createFromResource(this, R.array.degree_list, android.R.layout.simple_spinner_item);
        degreeChangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerChangeDegreeList.setAdapter(degreeChangeAdapter);
        spinnerChangeDegreeList.setOnItemSelectedListener(this);

        degreeTextView = findViewById(R.id.Account_Pi_Edit_Degree);
        ImageView editDegreeIcon = findViewById(R.id.Account_Pi_Ic_Edit_Degree);

        editDegreeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerChangeDegreeList.getVisibility() == View.GONE) {
                    spinnerChangeDegreeList.setVisibility(View.VISIBLE);
                    buttonNewDegree.setVisibility(View.VISIBLE);
                    degreeTextView.setVisibility(View.GONE);
                } else {
                    spinnerChangeDegreeList.setVisibility(View.GONE);
                    degreeTextView.setVisibility(View.VISIBLE);
                    spinnerChangeDegreeList.setVisibility(View.GONE);
                    buttonNewDegree.setVisibility(View.GONE);
                }
            }
        });

        // Degree chooser
        buttonNewDegree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {

                        int aqfLevel = 0;

                        switch (spinnerChangeDegreeList.getSelectedItem().toString()) {
                            case "Certificate I":
                                aqfLevel = 1;
                                break;
                            case "Certificate II":
                                aqfLevel = 2;
                                break;
                            case "Certificate III":
                                aqfLevel = 3;
                                break;
                            case "Certificate IV":
                                aqfLevel = 4;
                                break;
                            case "Diploma":
                                aqfLevel = 5;
                                break;
                            case "Advanced Diploma, Associate Degree":
                                aqfLevel = 6;
                                break;
                            case "Bachelor Degree":
                                aqfLevel = 7;
                                break;
                            case "Bachelor Honours Degree":
                                aqfLevel = 8;
                                break;
                            case "Masters Degree":
                                aqfLevel = 9;
                                break;
                            case "Doctoral Degree":
                                aqfLevel = 10;
                                break;
                            default:
                                break;
                        }

                        try {
                            new UserServiceImpl().updateUserAQFLevel(userId, aqfLevel);
                            showTextMessage("Degree updated successfully");
                            reloadActivity();
                        } catch (Exception e) {
                            showTextMessage("An error happens, pleas contact the IT administrator.");
                        }
                    }
                }.start();
            }
        });

        facultyTextView = findViewById(R.id.Account_Pi_Edit_Faculty);
        ImageView editFacultyIcon = findViewById(R.id.Account_Pi_Ic_Edit_Faculty);

        spinnerChangeFacultyList = findViewById(R.id.SpinnerPiFacultyList);

        // Set the faculty drop down list
        facultyChangeAdapter = ArrayAdapter.createFromResource(this, R.array.faculty_list, android.R.layout.simple_spinner_item);
        facultyChangeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerChangeFacultyList.setAdapter(facultyChangeAdapter);
        spinnerChangeFacultyList.setOnItemSelectedListener(this);

        editFacultyIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinnerChangeFacultyList.getVisibility() == View.GONE) {
                    spinnerChangeFacultyList.setVisibility(View.VISIBLE);
                    buttonNewFaculty.setVisibility(View.VISIBLE);
                    facultyTextView.setVisibility(View.GONE);
                } else {
                    spinnerChangeFacultyList.setVisibility(View.GONE);
                    buttonNewFaculty.setVisibility(View.GONE);
                    facultyTextView.setVisibility(View.VISIBLE);
                }
            }
        });

        buttonNewFaculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread() {
                    public void run() {
                        try {
                            String faculty = spinnerChangeFacultyList.getSelectedItem().toString();
                            if (faculty.equals("Please select your faculty (optional)")) {
                                faculty = "Not Provided";
                            }

                            new UserServiceImpl().updateUserFaculty(userId, faculty);
                            showTextMessage("Faculty updated successfully");
                            reloadActivity();
                        } catch (Exception e) {
                            showTextMessage("An error happens, pleas contact the IT administrator.");
                        }
                    }
                }.start();
            }
        });
        setAllToggles();
        new Thread() {
            public void run() {
                initUserInfo();
            }
        }.start();

    }

    /**
     * Init the user info and show on the UI
     */
    private void initUserInfo() {

        try {

            User user = new UserServiceImpl().getUser(userId);

            userName = user.getUserName();
            userEmail = user.getUserEmail();
            userFaculty = user.getUserFaculty();

            switch (user.getUserAQFLevel()) {
                case 1:
                    userDegree = "Certificate I";
                    break;
                case 2:
                    userDegree = "Certificate II";
                    break;
                case 3:
                    userDegree = "Certificate III";
                    break;
                case 4:
                    userDegree = "Certificate IV";
                    break;
                case 5:
                    userDegree = "Diploma";
                    break;
                case 6:
                    userDegree = "Advanced Diploma, Associate Degree";
                    break;
                case 7:
                    userDegree = "Bachelor Degree";
                    break;
                case 8:
                    userDegree = "Bachelor Honours Degree";
                    break;
                case 9:
                    userDegree = "Masters Degree";
                    break;
                case 10:
                    userDegree = "Doctoral Degree";
                    break;
                default:
                    userDegree = "Not Provided";
            }


            handler.sendEmptyMessage(1);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onResume() {
        super.onResume();
        handler.sendEmptyMessage(1);
    }

    /**
     * get OTP for registration
     *
     * @return OTP
     * @throws Exception if happens
     */
    private String getOTP() throws Exception {

        showTextMessage("The OTP has been sent, please check your mail box.");

        Message counter = new Message();
        counter.what = 2;
        counter.obj = OTP_TIMER;
        handler.sendMessage(counter);

        String newOTP = String.valueOf(new MailServiceImpl().sendOTP(editTextNewEmail.getText().toString()));

        return newOTP;
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
     * Reload current activity
     */
    private void reloadActivity() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Usage Access Permission check method
     *
     * @return true if granted, else false
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
     * Notification permission check method
     *
     * @return true if granted, else false
     */
    private boolean hasNotificationPermission() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.getAppContext());
        return notificationManagerCompat.areNotificationsEnabled();
    }    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    userNameTextView.setText(userName);
                    emailTextView.setText(userEmail);
                    degreeTextView.setText(userDegree);
                    facultyTextView.setText(userFaculty);
                    setAllToggles();
                    break;

                case 2:
                    int time = (int) msg.obj;

                    if (time > 0) {
                        buttonNewEmailGetOTP.setEnabled(false);
                        buttonNewEmailGetOTP.setText(time + "s");
                        buttonNewEmailGetOTP.setTextColor(ContextCompat.getColor(PersonalInformationActivity.this, R.color.black));
                        buttonNewEmailGetOTP.setBackgroundColor(ContextCompat.getColor(PersonalInformationActivity.this, R.color.grey));

                        Message message = new Message();
                        message.what = 2;
                        message.obj = time - 1;
                        handler.sendMessageDelayed(message, 1000);
                    } else {
                        buttonNewEmailGetOTP.setEnabled(true);
                        buttonNewEmailGetOTP.setText("Verify Email");
                        buttonNewEmailGetOTP.setTextColor(ContextCompat.getColor(PersonalInformationActivity.this, R.color.white));
                        buttonNewEmailGetOTP.setBackgroundColor(ContextCompat.getColor(PersonalInformationActivity.this, R.color.primary));
                    }

                    break;
            }
        }
    };

    /**
     * Precision location permission check method
     *
     * @return true if granted, else false
     */
    private boolean hasPrecisionLocationPermission() {
        return ContextCompat.checkSelfPermission(PersonalInformationActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * update precise location permission method
     */
    private void updatePreciseLocationPermission() {
        Intent intentLocationSourceSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intentLocationSourceSettings, APP_LOCATION_SETTINGS_REQUEST);
    }

    /**
     * update usage permission method
     */
    private void updateUsagePermission() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivityForResult(intent, REQUEST_USAGE_ACCESS);
    }

    /**
     * update notification permission method
     */
    private void updateNotificationPermission() {
        Intent intentNotification = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivityForResult(intentNotification, APP_NOTIFICATION_PUSH);
    }

    /**
     * Set each toggle appropriately depending on the current state
     */
    private void setAllToggles() {
        // add saved preferences for shake mode
        boolean shakingEnabled = sharedPreferences.getBoolean(getString(R.string.shaking_enabled), false);

        shakeToggle.setChecked(shakingEnabled);

        notificationToggle.setChecked(hasNotificationPermission());

        BackgroundAppService.USAGE_STATUS = hasUsageAccessPermission();

        usageAccessToggle.setChecked(hasUsageAccessPermission());

        preciseLocationToggle.setChecked(hasPrecisionLocationPermission());
    }
}