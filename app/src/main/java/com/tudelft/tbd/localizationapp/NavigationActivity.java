package com.tudelft.tbd.localizationapp;

import android.content.Context;
import android.content.Intent;
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

import com.tudelft.tbd.database.AppDatabase;
import com.tudelft.tbd.database.Building36DatabaseManager;
import com.tudelft.tbd.database.HomeDatabaseManager;
import com.tudelft.tbd.database.Measurement;
import com.tudelft.tbd.lib.kNNLocalization;

import java.util.ArrayList;
import java.util.List;

/**
 * Reference: example 2
 */

public class NavigationActivity extends AppCompatActivity implements ViewTreeObserver.OnWindowFocusChangeListener{

    /**
     * The sensor manager object.
     */
    private SensorManager sensorManager;
    /**
     * The stepCounter.
     */
    private Sensor stepCounter;

    private ImageView imageAreaMap;
    private ImageView imageStanding;

    private MapManager mapManager;
    private static AppDatabase database;
    private SensorListener sensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        Button buttonStartNavigation = findViewById(R.id.button_navigate);
        imageAreaMap = findViewById(R.id.floorMap);
        imageStanding = findViewById(R.id.image_position);

        // Get selected location
        Bundle bundleLocation = getIntent().getExtras();
        String location ="";
        if(bundleLocation != null)
            location = bundleLocation.getString("selectedLocation", "def");

        if(location.equals(getString(R.string.home))) {
            mapManager = new HomeMapManager();
            database = HomeDatabaseManager.getDatabaseInstance(getApplicationContext());
        }
        else if(location.equals(getString(R.string.eemcs_building_36))) {
            mapManager = new Building36MapManager();
            database = Building36DatabaseManager.getDatabaseInstance(getApplicationContext());
        }

        buttonStartNavigation.setOnClickListener(new NavigationButtonClickListener());

        imageStanding.setVisibility(View.INVISIBLE);

        // Set the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // if the default stepCounter exists
        if (sensorManager != null &&
                sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR) != null) {
            // set stepCounter
            stepCounter = sensorManager
                        .getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
            // register 'this' as a listener that updates values. Each time a sensor value changes,
            // the method 'onSensorChanged()' is called.
            sensorListener = new SensorListener();
            sensorManager.registerListener(sensorListener, stepCounter, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // No stepCounter!
        }

    }

    class SensorListener implements SensorEventListener {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.values[0] == 1)
                imageStanding.setImageDrawable(getDrawable(R.drawable.ic_walking));
            else
                imageStanding.setImageDrawable(getDrawable(R.drawable.ic_standing));
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    class NavigationButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            List<Measurement> measurements = measureRss();
            kNNLocalization localizer = new kNNLocalization(database);
            int cellId = localizer.locate(measurements);

            // Update location in view
            int[] coord = mapManager.getCellCenter(cellId, imageStanding.getWidth(), imageStanding.getHeight());
            //ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(imageStanding.getWidth(), imageStanding.getHeight());
            //lp.setMargins(coord[0], coord[1], coord[2], coord[3]);
            imageStanding.setX(coord[0]);
            imageStanding.setY(coord[1]);
            //imageStanding.setLayoutParams(lp);
            imageStanding.setVisibility(View.VISIBLE);
        }
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

}
