package com.tudelft.tbd.localizationapp;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

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

    private AppDatabase database;
    private int area;
    private String location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rssmeasurement);

        // Create items.
        textRssi = findViewById(R.id.textRSSI);

        Button buttonStop = findViewById(R.id.button_stop);
        // Set listener for the button.
        buttonStop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //close database
                finish();
            }
        });

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

        startMeasurement();
    }


    // onResume() registers the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
    }

    // onPause() unregisters the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
    }

    private void startMeasurement() {
        // Set text.
        textRssi.setText(String.format("\n\tScan all access points for cell %d:", area));
        // Set wifi manager.
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if(wifiManager != null) {
            // Start a wifi scan.
            wifiManager.startScan();
            // Store results in a list.
            List<ScanResult> scanResults = wifiManager.getScanResults();

            // Write results to a label
            for (ScanResult scanResult : scanResults) {
                textRssi.setText(textRssi.getText() + "\n\tBSSID = "
                        + scanResult.BSSID + "    RSSI = "
                        + scanResult.level + "dBm");
                storeMeasurement(scanResult);
            }

        }
    }

    private void storeMeasurement(ScanResult scanResult) {
        Measurement measurement = new Measurement();
        measurement.setCellId(area);
        measurement.setBssId(scanResult.BSSID);
        measurement.setRssi(scanResult.level);
        new StoreMeasurementsTask().execute(measurement);
    }

    private class StoreMeasurementsTask extends AsyncTask<Measurement, Void, Void> {
        @Override
        protected Void doInBackground(Measurement... measurement) {
            database.measurementDao().insertAll(measurement);
            return null;
        }
    }

}