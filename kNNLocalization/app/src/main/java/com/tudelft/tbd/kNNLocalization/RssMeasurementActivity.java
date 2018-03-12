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

package com.tudelft.tbd.kNNLocalization;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.tudelft.tbd.database.AppDatabase;
import com.tudelft.tbd.database.Building36DatabaseManager;
import com.tudelft.tbd.database.HomeDatabaseManager;
import com.tudelft.tbd.database.TrainingMeasurement;
import com.tudelft.tbd.lib.kNNTraining;

import java.util.ArrayList;
import java.util.List;

/**
 * Measure Wifi RSS and store in database
 * Reference: Smart Phone Sensing Example 4. Wifi received signal strength.
 */
public class RssMeasurementActivity extends AppCompatActivity {

    /**
     * The text view.
     */
    private TextView textRssi;

    private static AppDatabase database;
    private int area;
    private String location;

    private int interval = 10000;
    private Handler handler;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssmeasurement);

        // Create items.
        textRssi = findViewById(R.id.textRSSI);

        Button buttonStop = findViewById(R.id.button_stop);
        // Set listener for the button.
        buttonStop.setOnClickListener(new StopMeasurmentListener());

        // Get location information
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            location = bundle.getString("selectedLocation", "Undefined");
            area = bundle.getInt("selectedArea", 0);
        }

        if(location.equals(getString(R.string.home)))
            database = HomeDatabaseManager.getDatabaseInstance(getApplicationContext());
        else if(location.equals(getString(R.string.eemcs_building_36)))
            database = Building36DatabaseManager.getDatabaseInstance(getApplicationContext());

        handler = new Handler();
        startScheduledMeasurementTask();
    }

    // onResume() registers the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
    }

    // onPause() unregisters the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
    }

    /**
     * Reference: https://stackoverflow.com/a/6242292/2169877
     */
    Runnable runRssMeasurement = new Runnable() {
        @Override
        public void run() {
            try {
                measureRss();
            }
            finally {
                handler.postDelayed(runRssMeasurement, interval);
            }
        }
    };

    /**
     * Repeat RSS measurement and storage every 10 seconds
     */
    private void startScheduledMeasurementTask() {
        runRssMeasurement.run();
    }

    private void stopScheduledMeasurementTask(){
        handler.removeCallbacks(runRssMeasurement);
    }

    private void measureRss() {
        // Set text.
        textRssi.setText(String.format("\n\tScan all access points for cell %d:", area));
        // Set wifi manager.
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if(wifiManager != null) {
            // Start a wifi scan.
            wifiManager.startScan();
            // Store results in a list.
            List<ScanResult> scanResults = wifiManager.getScanResults();
            List<TrainingMeasurement> trainingMeasurements = new ArrayList<>();
            TrainingMeasurement[] conversionArray = {};

            // Write results to a label
            for (ScanResult scanResult : scanResults) {
                int rssiAbs = WifiManager.calculateSignalLevel(scanResult.level, 256);
                textRssi.setText(textRssi.getText() + "\n\tBSSID = "
                        + scanResult.BSSID + "    RSSI = "
                        + rssiAbs);

                trainingMeasurements.add(
                        new TrainingMeasurement(
                                area,
                                scanResult.BSSID,
                                rssiAbs,
                                System.currentTimeMillis()));
            }
            //Store trainingMeasurements
            new StoreMeasurementsTask().execute(trainingMeasurements.toArray(conversionArray));
        }
    }

    private static class StoreMeasurementsTask extends AsyncTask<TrainingMeasurement, Void, Void> {
        @Override
        protected Void doInBackground(TrainingMeasurement... trainingMeasurement) {
            database.measurementDao().insertAll(trainingMeasurement);
            return null;
        }
    }

    private class StopMeasurmentListener implements OnClickListener{

        @Override
        public void onClick(View view) {
            // Stop measurement
            stopScheduledMeasurementTask();

            new PerformTrainingTask().execute();

            // Finish and cleanup activity
            finish();
        }
    }

    private static class PerformTrainingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // Perform training on measured data
            kNNTraining trainer = new kNNTraining(database);
            trainer.doTraining();
            return null;
        }
    }
}