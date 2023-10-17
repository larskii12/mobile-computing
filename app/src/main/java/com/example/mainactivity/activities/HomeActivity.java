package com.example.mainactivity.activities;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.hardware.SensorManager;
import android.widget.Toast;

//import kotlin.math.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.mainactivity.R;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    // Declaring sensorManager
    // and acceleration constants
    private Context context;
//    public HomeActivity(Context context) {
//        this.context = context;
//    }

    private SensorManager sensorManager;
    private float acceleration = 0f;
    private float currentAcceleration = 0f;
    private float lastAcceleration = 0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Getting the Sensor Manager instance
//        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        Objects.requireNonNull(sensorManager).registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);

        acceleration = 10f;
        currentAcceleration = SensorManager.GRAVITY_EARTH;
        lastAcceleration = SensorManager.GRAVITY_EARTH;

        //Filter buttons at the top - These need on click functions
        ImageButton studyButton = (ImageButton) findViewById(R.id.studyButton);
        ImageButton foodButton = (ImageButton) findViewById(R.id.foodButton);
        ImageButton favouritesButton = (ImageButton) findViewById(R.id.favouritesButton);

        TextView greetingMessage = (TextView) findViewById(R.id.textView);

        LinearLayout filterLayout = (LinearLayout) findViewById(R.id.linearLayout);

        //Clickable cards
        CardView card1 = (CardView) findViewById(R.id.card1);
        CardView card2 = (CardView) findViewById(R.id.card2);
        CardView card3 = (CardView) findViewById(R.id.card3);
        CardView card4 = (CardView) findViewById(R.id.card4);
        CardView card5 = (CardView) findViewById(R.id.card5);
        CardView card6 = (CardView) findViewById(R.id.card6);

        //Images in the cards
        ImageView banner1 = (ImageView) findViewById(R.id.banner1);
        ImageView banner2 = (ImageView) findViewById(R.id.banner2);
        ImageView banner3 = (ImageView) findViewById(R.id.banner3);
        ImageView banner4 = (ImageView) findViewById(R.id.banner4);
        ImageView banner5 = (ImageView) findViewById(R.id.banner5);
        ImageView banner6 = (ImageView) findViewById(R.id.banner6);

        //Names in the cards

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                startActivity(intent);


                // should collect what they wrote here??
            }
        });
        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                startActivity(intent);


                // should collect what they wrote here??
            }
        });
        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                startActivity(intent);


                // should collect what they wrote here??
            }
        });
        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                startActivity(intent);


                // should collect what they wrote here??
            }
        });
        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                startActivity(intent);


                // should collect what they wrote here??
            }
        });
        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                startActivity(intent);


                // should collect what they wrote here??
            }
        });
        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, LocationActivity.class);
                startActivity(intent);


                // should collect what they wrote here??
            }
        });
    }
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            lastAcceleration = currentAcceleration;
            currentAcceleration = (float) Math.sqrt((double) (x * x + y * y + z * z));
            float delta = currentAcceleration - lastAcceleration;
            acceleration = acceleration * 0.9f + delta;
            if (acceleration > 12) {
                Toast.makeText(getApplicationContext(), "Shake event detected", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    @Override
    protected void onResume() {
        sensorManager.registerListener(sensorListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        super.onResume();
    }
    @Override
    protected void onPause() {
        sensorManager.unregisterListener(sensorListener);
        super.onPause();
    }
}
