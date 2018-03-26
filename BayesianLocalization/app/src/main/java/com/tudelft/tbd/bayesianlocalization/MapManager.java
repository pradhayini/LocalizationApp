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

import android.graphics.Bitmap;

/**
 * Navigation area manager that generates, displays and updates the map of a specific area.
 */

public interface MapManager {

    /**
     * Draw a map of the selected indoor location
     * @param mapWidth : width of the display area
     * @param mapHeight : height of the display area
     * @return : Bitmap image that contains the generated map
     */
    Bitmap drawMap(int mapWidth, int mapHeight, int floorNum);

    int[] getCellCenter(int cellId, int width, int height);
}
