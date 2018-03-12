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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {

    private int startingFloorNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startingFloorNo = 3;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        Instantiation
         */
        Button buttonLocateMe = findViewById(R.id.button_main_locateMe);

        /*
        Set listener on button click to open activity for navigation
         */
        buttonLocateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentStartNavigation = new Intent(getApplicationContext(), NavigationActivity.class);
                populateAndStartNextActivity(intentStartNavigation);
            }
        });

        /*
        Dropdown list
        Reference: http://www.ahotbrew.com/android-dropdown-spinner-example/
         */
        Spinner spinnerFloorNos = findViewById(R.id.spinner_floorNums);

        String[] floorNos = new String[] { "3rd Floor", "4th Floor" };

        ArrayAdapter<String> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item, floorNos);

        spinnerFloorNos.setAdapter(adapter);

        spinnerFloorNos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                if(id == 0)
                    startingFloorNo = 3;
                else if(id == 1)
                    startingFloorNo = 4;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
    }

    private void populateAndStartNextActivity(Intent intent){

        // Share starting location with next activity
        Bundle bundleFloorNum = new Bundle();
        bundleFloorNum.putInt(getString(R.string.key_startingFloor), startingFloorNo);
        intent.putExtras(bundleFloorNum);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
