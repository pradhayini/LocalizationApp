package com.tudelft.tbd.localizationapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends AppCompatActivity {

    private RadioButton radioButtonHome;
    private RadioButton radioButtonBuilding36;
    private RadioGroup radioGroupLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
          Instantiate elements
         */
        Button buttonTrain = findViewById(R.id.button_Train);
        Button buttonLocate = findViewById(R.id.button_Locate);
        radioButtonHome = findViewById(R.id.radioButton_Home);
        radioButtonBuilding36 = findViewById(R.id.radioButton_Building36);
        radioGroupLocation = findViewById(R.id.radioGroup_Location);

        /*
          Set listener on button click to open activity for navigation
         */
        buttonLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStartNavigation = new Intent(getApplicationContext(), NavigationActivity.class);

                // Share selected location with navigation activity
                Bundle bundleLocation = new Bundle();
                if(radioGroupLocation.getCheckedRadioButtonId() == R.id.radioButton_Home)
                    bundleLocation.putString("selectedArea", "home");
                if(radioGroupLocation.getCheckedRadioButtonId() == R.id.radioButton_Building36)
                    bundleLocation.putString("selectedArea", "36");
                intentStartNavigation.putExtras(bundleLocation);
                intentStartNavigation.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentStartNavigation);
            }
        });

        /*
          Set listener on button click to open activity for training
         */
        buttonTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStartTraining = new Intent(getApplicationContext(), TrainingActivity.class);

                // Share selected location with navigation activity
                Bundle bundleLocation = new Bundle();
                if(radioGroupLocation.getCheckedRadioButtonId() == R.id.radioButton_Home)
                    bundleLocation.putString("selectedLocation", radioButtonHome.getText().toString());
                if(radioGroupLocation.getCheckedRadioButtonId() == R.id.radioButton_Building36)
                    bundleLocation.putString("selectedLocation", radioButtonBuilding36.getText().toString());
                intentStartTraining.putExtras(bundleLocation);
                intentStartTraining.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentStartTraining);
            }
        });
    }
}
