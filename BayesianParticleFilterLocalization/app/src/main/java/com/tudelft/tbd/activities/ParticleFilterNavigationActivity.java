/*
 * Copyright (c) 2018.  Group TBD, SPS2018, TUDelft
 * Author: Pradhayini Ramamurthy
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without
 * fee is hereby granted, provided that the above copyright notice and this permission notice appear
 * in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS
 * SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 * AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 * NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 */

package com.tudelft.tbd.activities;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tudelft.tbd.localization.R;
import com.tudelft.tbd.map.MapView;
import com.tudelft.tbd.viewmodels.ParticleFilterViewModel;

/**
 * Activity that generates maps and enables navigation for Particle Filter localization
 */
public class ParticleFilterNavigationActivity extends AppCompatActivity implements ViewTreeObserver.OnWindowFocusChangeListener {
    // Sensor Variables
    private SensorManager sensorManager;
    private Sensor stepCounter;
    private Sensor stepDetector;
    private Sensor rotationVector;
    private Sensor accelerometer;
    private SensorListener sensorListener;

    // Sensor accuracy check variables
    private int scLastAccuracy;
    private int sdLastAccuracy;
    private int rvLastAccuracy;
    private int acLastAccuracy;

    // UI variables
    private TextView textFloorNum;
    private TextView textCellNum;
    private MapView imageAreaMap;
    private ImageView imagePosition;
    private ParticleFilterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Find UI elements
        textFloorNum = findViewById(R.id.textView_FloorNum);
        textCellNum = findViewById(R.id.textView_CellNum);
        imageAreaMap = findViewById(R.id.image_floorMap);
        imagePosition = findViewById(R.id.image_position);
        Button buttonLocateMe = findViewById(R.id.button_nav_LocateMe);

        // Get starting floor number and user height
        Bundle bundle = getIntent().getExtras();
        float userHeight;
        int floorNum;
        if(bundle != null){
            floorNum = bundle.getInt(getString(R.string.key_startingFloor), 3);
            userHeight = bundle.getFloat(getString(R.string.height), (float)1.79);
        }
        else {
            floorNum = 3;
            userHeight = (float) 1.79;
        }

        // Initializations
        viewModel = ViewModelProviders.of(this).get(ParticleFilterViewModel.class);
        viewModel.setUserHeight(userHeight);
        viewModel.getFloor().setValue(floorNum);

        imageAreaMap.setViewModel(viewModel);

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.

        viewModel.getUpdateMap().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable final Boolean updateMap) {
                // Update the UI, in this case, an ImageView.
                if(updateMap == null)
                    return;

                if(updateMap){
                    drawFloorMap(true);
                }
            }
        });

        viewModel.getCellIds().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newCellIds) {
                // Update the UI, in this case, a TextView.
                if(newCellIds == null)
                    return;

                textCellNum.setText(newCellIds);
                if(!newCellIds.contains(";")){
                    updateMovingIcon();
                    sensorManager.unregisterListener(sensorListener);
                }
            }
        });

        viewModel.getFloor().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer floor) {
                if(floor == null)
                    return;
                textFloorNum.setText(String.valueOf((floor)));
                // Reset particles on new floor
                viewModel.initialize();
                drawFloorMap();
            }
        });

        // Initialize event listeners
        imagePosition.setVisibility(View.INVISIBLE);
        buttonLocateMe.setOnClickListener(new NavigationButtonClickListener());
    }

    /**
     * On resuming activity, registers the sensorManager to listen to motion sensor events
     */
    protected void onResume() {
        super.onResume();
        registerMotionSensors();
    }

    /**
     * On pausing activity, unregisters the sensorManger to stop listening to motion sensor events
     */
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    /**
     * Finish (clean up) activity with back button, i.e. on returning to main activity
     * Reference: https://stackoverflow.com/a/4778845/2169877
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
            finish();
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Update UI after activity is created and available for interaction
     * @param hasFocus Window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
    }

    /**
     * Draw map of current floor on the UI
     */
    private void drawFloorMap() {
        drawFloorMap(false);
    }

    private void drawFloorMap(boolean drawParticles) {
        if(viewModel.getFloor().getValue() != null){
            viewModel.setShowParticles(drawParticles);
            viewModel.setMapModelFloor(viewModel.getFloor().getValue());

            imageAreaMap.startDrawing();
        }
    }

    /**
     * Initialize SensorManager and register listeners for step detectors and counters
     */
    private void registerMotionSensors() {
        // Set the sensor manager
        if(sensorManager == null){
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }

        // Register listeners for detected step and rotation sensors
        if (sensorManager != null){
            if(sensorListener == null) {
                sensorListener = new SensorListener();
            }

            if(stepCounter == null){
                stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            }
            if(stepCounter != null){
                sensorManager.registerListener(sensorListener, stepCounter,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

            if(stepDetector == null) {
                stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            }
            if(stepDetector != null){
                sensorManager.registerListener(sensorListener, stepDetector,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

            if(rotationVector == null){
                rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            }
            if(rotationVector != null){
                sensorManager.registerListener(sensorListener, rotationVector,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }

            if(accelerometer == null){
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            }
            if(accelerometer != null){
                sensorManager.registerListener(sensorListener, accelerometer,
                        SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else {
            // No motion sensors!
        }
    }

    /**
     * Assuming that the device is held horizontally, tangential to the ground, calculate rotation.
     * @param rotationVector rotation vector from sensor
     */
    private void calculateRotation(float[] rotationVector) {
        if(rotationVector == null || rotationVector.length == 0)
            return;

        float[] rotationMatrix = new float[16];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, rotationVector);

        // Calculate orientation from the rotation matrix
        float[] orientation = new float[3];
        SensorManager.getOrientation(rotationMatrix, orientation);

        // Azimuth represents rotation around the z-axis, or changes in walking latestDirection
        float azimuth = orientation[0];
        viewModel.evaluateDirectionChange(azimuth);
    }

    /**
     * Update user location in map
     */
    private void updateMovingIcon(){
        // TODO move to surfaceview
        int[] coord = viewModel.getCurrentCellCenter();
        if(coord != null){
            imagePosition.setX(coord[0]);
            imagePosition.setY(coord[1]);
            imagePosition.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Triggers navigation and UI update on button click
     */
    class NavigationButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            // Reset position icon
            imagePosition.setImageDrawable(getDrawable(R.drawable.ic_standing));
            sensorManager.unregisterListener(sensorListener);
            registerMotionSensors();
            viewModel.initialize();
        }
    }

    /**
     * Registers changes detected by motion sensors
     */
    class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            switch (sensorEvent.sensor.getType())
            {
                case Sensor.TYPE_STEP_COUNTER: {
                    // Used for distance calculation
                    if(scLastAccuracy != SensorManager.SENSOR_STATUS_UNRELIABLE)
                        viewModel.calculateDistanceMoved((int)sensorEvent.values[0]);
                    break;
                }
                case Sensor.TYPE_STEP_DETECTOR: {
                    // Used to choose between user walking or standing icon
                    if(sdLastAccuracy != SensorManager.SENSOR_STATUS_UNRELIABLE){
                        if(sensorEvent.values[0] == 1)
                            imagePosition.setImageDrawable(getDrawable(R.drawable.ic_walking));
                        else
                            imagePosition.setImageDrawable(getDrawable(R.drawable.ic_standing));
                    }
                    break;
                }
                case Sensor.TYPE_ROTATION_VECTOR: {
                    // Used to determine latestDirection of movement
                    if(rvLastAccuracy != SensorManager.SENSOR_STATUS_UNRELIABLE){
                        calculateRotation(sensorEvent.values);
                    }
                    break;
                }
                case Sensor.TYPE_ACCELEROMETER: {
                    // Used to detect ascending/descending of stairways
                    if(acLastAccuracy != SensorManager.SENSOR_STATUS_UNRELIABLE){
                        viewModel.monitorMovementType(sensorEvent.values[2]);
                    }
                }
            }
        }

        /**
         * Record sensor accuracy changes to detect unreliable states
         * Reference: https://github.com/kplatfoot/android-rotation-sensor-sample/blob/master/app/src/main/java/com/kviation/sample/orientation/Orientation.java
         * @param sensor Sensor type
         * @param accuracy Accuracy status
         */
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            switch (sensor.getType())
            {
                case Sensor.TYPE_STEP_COUNTER:{
                    if (scLastAccuracy != accuracy) {
                        scLastAccuracy = accuracy;
                    }
                    break;
                }
                case Sensor.TYPE_STEP_DETECTOR:{
                    if (sdLastAccuracy != accuracy) {
                        sdLastAccuracy = accuracy;
                    }
                    break;
                }
                case Sensor.TYPE_ROTATION_VECTOR:{
                    if (rvLastAccuracy != accuracy) {
                        rvLastAccuracy = accuracy;
                    }
                    break;
                }
                case Sensor.TYPE_ACCELEROMETER: {
                    if(acLastAccuracy != accuracy) {
                        acLastAccuracy = accuracy;
                    }
                }
            }
        }
    }
}
