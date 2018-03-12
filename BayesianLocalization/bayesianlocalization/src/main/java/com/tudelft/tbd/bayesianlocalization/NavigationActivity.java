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

package com.tudelft.tbd.bayesianlocalization;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tudelft.tbd.database.Measurement;

import java.util.ArrayList;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements ViewTreeObserver.OnWindowFocusChangeListener {
    /**
     * The sensor manager object.
     */
    private SensorManager sensorManager;
    /**
     * The stepCounter.
     */
    private Sensor stepCounter;

    private int floorNum;
    private int cellNum;
    private  TextView textFloorNum;
    private TextView textCellNum;
    private ImageView imageAreaMap;
    private ImageView imagePosition;

    private MapManager mapManager;
    private SensorListener sensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        textFloorNum = findViewById(R.id.textView_FloorNum);
        textCellNum = findViewById(R.id.textView_CellNum);

        imageAreaMap = findViewById(R.id.image_floorMap);
        imagePosition = findViewById(R.id.image_position);

        mapManager = new HomeMapManager();

        // Get starting floor
        Bundle bundleFloorNum = getIntent().getExtras();
        int num;
        if (bundleFloorNum != null)
            num = bundleFloorNum.getInt(getString(R.string.key_startingFloor), 3);
        else
            num = 3;

        setFloorNum(num);

        imagePosition.setVisibility(View.INVISIBLE);

        // Set the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // if the default stepCounter exists
        if (sensorManager != null){
            // set stepCounter
            stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            if(stepCounter != null){
                // register 'this' as a listener that updates values. Each time a sensor value changes,
                // the method 'onSensorChanged()' is called.
                sensorListener = new SensorListener();
                sensorManager.registerListener(sensorListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
            }
        } else {
            // No stepCounter!
        }


        Button buttonLocateMe = findViewById(R.id.button_nav_LocateMe);
        buttonLocateMe.setOnClickListener(new NavigationButtonClickListener());
    }

    // onResume() registers the stepCounter for listening the events
    protected void onResume() {
        super.onResume();
        sensorListener = new SensorListener();
        sensorManager.registerListener(sensorListener, stepCounter,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    // onPause() unregisters the stepCounter for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(sensorListener);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(mapManager != null)
        {
            Bitmap map = mapManager.drawMap(imageAreaMap.getWidth(), imageAreaMap.getHeight());
            imageAreaMap.setImageBitmap(map);
            imageAreaMap.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private void StartLocalization(){

    }

    private void setFloorNum(int num){
        floorNum = num;
        textFloorNum.setText(String.valueOf(num));
    }

    private void setCellNum(int num){
        cellNum = num;
        textCellNum.setText(String.valueOf(num));
    }

    private List<Measurement> measureRss() {
        // Set wifi manager.
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<Measurement> measurements = new ArrayList<>();

        if(wifiManager != null) {
            // Start a wifi scan.
            wifiManager.startScan();
            // Store results in a list.
            List<ScanResult> scanResults = wifiManager.getScanResults();

            // Write results to a label
            for (ScanResult scanResult : scanResults) {
                int rssiAbs = WifiManager.calculateSignalLevel(scanResult.level, 256);
                measurements.add(new Measurement(0, scanResult.BSSID, rssiAbs));
            }
        }
        return measurements;
    }

    class NavigationButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            // Reset position icon
            imagePosition.setImageDrawable(getDrawable(R.drawable.ic_standing));

            StartLocalization();

            // Update location in view
            int[] coord = mapManager.getCellCenter(cellNum, imagePosition.getWidth(), imagePosition.getHeight());
            //ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(imagePosition.getWidth(), imagePosition.getHeight());
            //lp.setMargins(coord[0], coord[1], coord[2], coord[3]);
            imagePosition.setX(coord[0]);
            imagePosition.setY(coord[1]);
            //imagePosition.setLayoutParams(lp);
            imagePosition.setVisibility(View.VISIBLE);
        }
    }

    class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.values[0] == 1)
                imagePosition.setImageDrawable(getDrawable(R.drawable.ic_walking));
            else
                imagePosition.setImageDrawable(getDrawable(R.drawable.ic_standing));
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}
