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

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class TrainingActivity extends AppCompatActivity implements ViewTreeObserver.OnWindowFocusChangeListener{

    private String location;
    private EditText inputCellId;
    private ImageView imageAreaMap;

    private MapManager mapManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training);

        Button buttonStartTraining = findViewById(R.id.button_StartTraining);
        inputCellId = findViewById(R.id.text_CellId);
        imageAreaMap = findViewById(R.id.areaMap);

        // Get selected location
        Bundle bundleLocation = getIntent().getExtras();
        if(bundleLocation != null)
            location = bundleLocation.getString("selectedLocation", "def");
        else
            location = "Undefined location.";

        if(location.equals(getString(R.string.home)))
            mapManager = new HomeMapManager();
        else if(location.equals(getString(R.string.eemcs_building_36)))
            mapManager = new Building36MapManager();

        buttonStartTraining.setOnClickListener(new TrainingButtonClickListener());
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

    class TrainingButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int trainingArea = Integer.parseInt(inputCellId.getText().toString());

            if(mapManager != null) {
                Intent intentStartMeasurement = new Intent(getApplicationContext(), RssMeasurementActivity.class);

                // Share selected area with measurement activity
                Bundle bundle = new Bundle();
                bundle.putInt("selectedArea", trainingArea);
                bundle.putString("selectedLocation", location);
                intentStartMeasurement.putExtras(bundle);
                intentStartMeasurement.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentStartMeasurement);
            }
        }
    }
}
