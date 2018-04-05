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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.tudelft.tbd.localization.R;

public class MainActivity extends AppCompatActivity {

    private RadioGroup rgMethod;
    private Spinner spinnerFloorNos;
    private TextView spinnerLabel;
    private TextView heightLabel;
    private EditText heightValue;
    private RadioButton rbBayesian;

    private int startingFloorNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startingFloorNo = 3;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Instantiation
         */
        rbBayesian = findViewById(R.id.radioButton_bayesian);
        RadioButton rbParticle = findViewById(R.id.radioButton_particle);
        rgMethod = findViewById(R.id.radioGroup_Method);
        spinnerFloorNos = findViewById(R.id.spinner_floorNums);
        spinnerLabel = findViewById(R.id.textView_startingFloorLabel);
        heightLabel = findViewById(R.id.textView_heightLabel);
        heightValue = findViewById(R.id.editText_height);
        Button buttonLocateMe = findViewById(R.id.button_main_locateMe);

        // Set listeners on radio buttons to enable/disable floor selection
        rbBayesian.setOnClickListener(new RadioButtonOnClickListener(false));
        rbParticle.setOnClickListener(new RadioButtonOnClickListener(true));

        // Set listener on button click to open activity for navigation
        buttonLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                populateAndStartNextActivity();
            }
        });

        setUpSpinnerFloorNum();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reset default values
        rbBayesian.performClick();
    }

    /**
     * Dropdown list to select floor from which localization is started
     * Reference: http://www.ahotbrew.com/android-dropdown-spinner-example/
     */
    private void setUpSpinnerFloorNum() {

        String[] floorNos = new String[] { "3rd Floor", "4th Floor" };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, floorNos);

        spinnerFloorNos.setAdapter(adapter);

        spinnerFloorNos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                startingFloorNo = (id == 1) ? 4 : 3;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    /**
     * Launch Bayesian or Particle Filter localization based on user choice.
     */
    private void populateAndStartNextActivity(){
        Intent intentStartNavigation = null;

        // Create next activity for selected method and share required information
        switch (rgMethod.getCheckedRadioButtonId()){
            case R.id.radioButton_bayesian:
                intentStartNavigation = new Intent(getApplicationContext(), BayesianNavigationActivity.class);
                break;
            case R.id.radioButton_particle:
                intentStartNavigation = new Intent(getApplicationContext(), ParticleFilterNavigationActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt(getString(R.string.key_startingFloor), startingFloorNo);
                bundle.putFloat(getString(R.string.height), getHeight());
                intentStartNavigation.putExtras(bundle);
                break;
        }

        if(intentStartNavigation != null){
            intentStartNavigation.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentStartNavigation);
        }
    }

    /**
     * Get user height for the motion model used in Particle Filter localization
     * @return User height in m
     */
    private Float getHeight(){
        String h = heightValue.getText().toString();
        float height;
        try{
            height = Float.parseFloat(h);
        } catch (NumberFormatException ex){
            height = 0;
        }
        if(Float.compare(height, (float)2.5) > 0 || Float.compare(height, (float) 0) <= 0){
            height = (float) 1.79;
        }
        return height;
    }

    /**
     * Handler for changes in the selected localization method
     * Enables additional input choices for Particle Filter localization
     */
    class RadioButtonOnClickListener implements View.OnClickListener {
        private int enableParticleFilterOptions;

        RadioButtonOnClickListener(boolean enable) {
            this.enableParticleFilterOptions = enable ? View.VISIBLE : View.INVISIBLE;
        }

        @Override
        public void onClick(View view) {
            spinnerFloorNos.setVisibility(enableParticleFilterOptions);
            spinnerLabel.setVisibility(enableParticleFilterOptions);
            heightValue.setVisibility(enableParticleFilterOptions);
            heightLabel.setVisibility(enableParticleFilterOptions);
        }
    }
}
