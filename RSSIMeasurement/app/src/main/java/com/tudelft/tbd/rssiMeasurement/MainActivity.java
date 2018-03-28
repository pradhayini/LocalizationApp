package com.tudelft.tbd.rssiMeasurement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private EditText inputCellId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonStartTraining = findViewById(R.id.button_StartTraining);
        inputCellId = findViewById(R.id.text_CellId);

        buttonStartTraining.setOnClickListener(new ButtonClickListener());
    }

    class ButtonClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            int trainingArea = Integer.parseInt(inputCellId.getText().toString());

            Intent intentStartMeasurement = new Intent(getApplicationContext(), RssMeasurementActivity.class);

            // Share selected area with measurement activity
            Bundle bundle = new Bundle();
            bundle.putInt("selectedArea", trainingArea);
            intentStartMeasurement.putExtras(bundle);
            intentStartMeasurement.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentStartMeasurement);
        }
    }

}
