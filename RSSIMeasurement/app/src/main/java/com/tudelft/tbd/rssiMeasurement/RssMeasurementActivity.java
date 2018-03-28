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

package com.tudelft.tbd.rssiMeasurement;

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
import com.tudelft.tbd.database.databaseManager;
import com.tudelft.tbd.database.TrainingMeasurement;

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

    private int interval = 1000;
    private Handler handler;

    private int loopCounter = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssmeasurement);

        // Create items.
        textRssi = findViewById(R.id.textRSSI);

        Button buttonStop = findViewById(R.id.button_stop);
        // Set listener for the button.
        buttonStop.setOnClickListener(new StopMeasurementListener());

        // Get location information
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            area = bundle.getInt("selectedArea", 0);
        }

        database = databaseManager.getDatabaseInstance(getApplicationContext());

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
        if(loopCounter >= 100){
            stopMeasurement();
            loopCounter = 0;
        }
        else{
            // Set text.
            textRssi.setText(String.format("\n\t%d\tScan all access points for cell %d:", loopCounter, area));
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

                    // Extract Organization Unique Identifier (OUI) from MAC address
                    String vendorId = scanResult.BSSID.replace(":", "");
                    vendorId = vendorId.substring(0, 6);

                    trainingMeasurements.add(
                            new TrainingMeasurement(
                                    area,
                                    scanResult.BSSID,
                                    vendorId,
                                    rssiAbs,
                                    System.currentTimeMillis()));

                    textRssi.setText(textRssi.getText() + "\n\tBSSID = "
                            + scanResult.BSSID + "    RSSI = "
                            + rssiAbs);
                }
                //Store trainingMeasurements
                new StoreMeasurementsTask().execute(trainingMeasurements.toArray(conversionArray));

            }
            loopCounter++;
        }
    }

    private static class StoreMeasurementsTask extends AsyncTask<TrainingMeasurement, Void, Void> {
        @Override
        protected Void doInBackground(TrainingMeasurement... trainingMeasurement) {
            database.measurementDao().insertAll(trainingMeasurement);
            return null;
        }
    }

    private class StopMeasurementListener implements OnClickListener{

        @Override
        public void onClick(View view) {
            stopMeasurement();
        }
    }

    private void stopMeasurement(){
        // Stop measurement
        stopScheduledMeasurementTask();

       // new PerformTrainingTask().execute();

        // Finish and cleanup activity
        finish();
    }
}