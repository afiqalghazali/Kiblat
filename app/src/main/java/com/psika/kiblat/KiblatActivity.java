package com.psika.kiblat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class KiblatActivity extends AppCompatActivity implements SensorEventListener {

    private ImageButton back;
    private ImageView qiblaImage;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    private float[] accelerometerReading = new float[3];
    private float[] magnetometerReading = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientationValues = new float[3];

    // Konstanta untuk filter low pass dan rotasi smoothing
    private static final float LOW_PASS_ALPHA = 0.1f;
    private static final float ROTATION_ALPHA = 0.2f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiblat);

        back = findViewById(R.id.back);
        qiblaImage = findViewById(R.id.qiblaImage);

        // Inisialisasi manajer sensor dan sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        if (accelerometer == null || magnetometer == null) {
            Toast.makeText(this, "Sensor untuk compass tidak tersedia!", Toast.LENGTH_SHORT).show();
            finish();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(KiblatActivity.this, MainActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            lowPass(event.values, accelerometerReading);
        } else if (event.sensor == magnetometer) {
            lowPass(event.values, magnetometerReading);
        }

        updateOrientation();
    }

    // Metode untuk menerapkan filter low pass pada data sensor
    private void lowPass(float[] input, float[] output) {
        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + LOW_PASS_ALPHA * (input[i] - output[i]);
        }
    }

    private void  updateOrientation() {
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading);
        SensorManager.getOrientation(rotationMatrix, orientationValues);

        float azimuthInDegrees = (float) Math.toDegrees(orientationValues[0]);

        float currentRotation = qiblaImage.getRotation();
        float newRotation = -azimuthInDegrees;
        float smoothedRotation = currentRotation + ROTATION_ALPHA * (newRotation - currentRotation);

        qiblaImage.setRotation(smoothedRotation);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}