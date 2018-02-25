package com.tudelft.tbd.localizationapp;

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
    private int trainingArea;
    private ImageView imageAreaMap;

    private MapCreator mapCreator;

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
            location = bundleLocation.getString("selectedArea", "def");
        else
            location = "Undefined location.";

        if(location.equals(getString(R.string.home)))
            mapCreator = new HomeMapCreator();
        else if(location.equals(getString(R.string.eemcs_building_36)))
            mapCreator = new Building36MapCreator();

        buttonStartTraining.setOnClickListener(new TrainingButtonClickListener());
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(mapCreator != null)
        {
            Bitmap map = mapCreator.drawHomeMap(imageAreaMap.getWidth(), imageAreaMap.getHeight());
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
            trainingArea = Integer.parseInt(inputCellId.getText().toString());
            StartTraining();
        }

        public void StartTraining(){
            // store training data
        }
    }
}
