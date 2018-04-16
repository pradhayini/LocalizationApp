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

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.TextView;

import com.tudelft.tbd.localization.R;
import com.tudelft.tbd.map.MapView;
import com.tudelft.tbd.viewmodels.BayesianViewModel;

/**
 * Activity that generates maps and enables navigation for Bayesian localization
 */
public class BayesianNavigationActivity extends AppCompatActivity implements ViewTreeObserver.OnWindowFocusChangeListener {

    // Location/map variables
    private int floorNum = 3;

    // UI variables
    private TextView textFloorNum;
    private TextView textCellNum;
    private MapView imageAreaMap;

    private BayesianViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Find UI elements
        textFloorNum = findViewById(R.id.textView_FloorNum);
        textCellNum = findViewById(R.id.textView_CellNum);
        imageAreaMap = findViewById(R.id.image_floorMap);
        Button buttonLocateMe = findViewById(R.id.button_nav_LocateMe);

        // Initializations
        viewModel = ViewModelProviders.of(this).get(BayesianViewModel.class);

        imageAreaMap.setViewModel(viewModel);

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        viewModel.getCellIds().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String newCellIds) {
                // Update the UI, in this case, a TextView.
                if(newCellIds == null)
                    return;

                textCellNum.setText(newCellIds);
                drawFloorMap();
                //TODO fix icon location
           //     if(!newCellIds.contains(";"))
           //         imageAreaMap.startDrawingUserIcon();
            }
        });

        viewModel.getFloor().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer floor) {
                if(floor == null)
                    return;
                setFloorNum(floor);
                drawFloorMap();
            }
        });

        buttonLocateMe.setOnClickListener(new NavigationButtonClickListener());
    }

    /**
     * Finish (clean up) activity with back button, i.e. on returning to main activity
     * Reference: https://stackoverflow.com/a/4778845/2169877
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
            finish();
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Update UI after activity is created and available for interaction
     * @param hasFocus Window has focus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        viewModel.setFloor(floorNum);
    }

    /**
     * Set the current (or default) floor number based on localization results
     * @param num Detected (or) default floor number
     */
    private void setFloorNum(int num){
        floorNum = num;
        textFloorNum.setText(String.valueOf(num));
    }

    /**
     * Draw map of current floor on the UI
     */
    private void drawFloorMap() {
        viewModel.setMapModelFloor(floorNum);
        viewModel.setPossibleLocations(viewModel.getPossibleCellIds());
        imageAreaMap.startDrawing();
    }

    /**
     * Triggers navigation and UI update on button click
     */
    class NavigationButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            viewModel.localize();
        }
    }
}
