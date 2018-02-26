package com.tudelft.tbd.localizationapp;

import android.graphics.Bitmap;

/**
 * Navigation area manager that creates and enables navigation through specific area.
 */

public interface MapManager {

    /**
     * Draw a map of the selected indoor location
     * @param width : width of the display area
     * @param height : height of the display area
     * @return : Bitmap image that contains the generated map
     */
    Bitmap drawMap(int width, int height);
}
