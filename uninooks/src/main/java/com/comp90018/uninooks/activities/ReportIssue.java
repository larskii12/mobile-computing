package com.comp90018.uninooks.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.comp90018.uninooks.R;
import com.comp90018.uninooks.service.gps.GPSServiceImpl;
import com.comp90018.uninooks.service.mail.MailServiceImpl;


/**
 * Report issue activity
 */
public class ReportIssue extends AppCompatActivity {

    private EditText editTextRaiseIssue;
    private Button buttonRaiseIssue;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @SuppressLint({"SetTextI18n", "HandlerLeak"})
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String info = (String) msg.obj;
                    Toast.makeText(getApplicationContext(), info, Toast.LENGTH_SHORT).show();
                    break;

                case 1:
                    buttonRaiseIssue.setEnabled(false);
                    buttonRaiseIssue.setBackgroundColor(ContextCompat.getColor(ReportIssue.this, R.color.grey));
                    buttonRaiseIssue.setText("Sending...");
                    break;

                case 2:
                    buttonRaiseIssue.setEnabled(true);
                    buttonRaiseIssue.setText("Report issue");
                    buttonRaiseIssue.setBackgroundColor(ContextCompat.getColor(ReportIssue.this, R.color.primary));
            }
        }
    };

    /**
     * on create method
     *
     * @param savedInstanceState as savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_issue);

        editTextRaiseIssue = findViewById(R.id.EditTextRaiseIssue);

        buttonRaiseIssue = findViewById(R.id.ButtonRaiseIssue);

        buttonRaiseIssue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editTextRaiseIssue.getText().toString().trim().isEmpty()) {
                    showTextMessage("Please write more details about the issue.");
                } else {
                    new Thread() {
                        public void run() {
                            try {
                                handler.sendEmptyMessage(1);
                                new MailServiceImpl().raiseIssue(editTextRaiseIssue.getText().toString() + "\n\nReport Location: " + GPSServiceImpl.getCurrentLocation().latitude + ", " + GPSServiceImpl.getCurrentLocation().longitude);
                                handler.sendEmptyMessage(2);
                            } catch (Exception e) {
                                throw new RuntimeException("An error occurs when raising app issue, please contact the IT administrator.");
                            }

                            showTextMessage("Issue report success.");
                            finish();

                        }
                    }.start();
                }
            }
        });
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
}
