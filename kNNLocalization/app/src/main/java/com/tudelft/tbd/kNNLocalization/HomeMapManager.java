/*
 * Copyright (c) 2018.  Group TBD, SPS2018, TUDelft
 * Permission to use, copy, modify, and/or distribute this software for any purpose with or without
 *  fee is hereby granted, provided that the above copyright notice and this permission notice appear
 *  in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS
 * SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 * AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
 *  NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 *  OF THIS SOFTWARE.
 */

package com.tudelft.tbd.kNNLocalization;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Class that creates and manages a map for 'Home'
 */

public class HomeMapManager implements MapManager {
    private int oneUnit;

    public Bitmap drawMap(int width, int height) {

        // Set the map dimensions
        int mapWidth = 640+40;
        int mapHeight = 1102+60;

        // Calculate scale
        float floatOneUnit = (float) width/mapWidth;
        if(mapHeight * floatOneUnit > height)
            floatOneUnit = (float) height/mapHeight;

        oneUnit = (int) floatOneUnit;

        List<ShapeDrawable> walls = new ArrayList<>();
        // Horizontal Walls
        ShapeDrawable wall1 = new ShapeDrawable(new RectShape());
        wall1.setBounds(10*oneUnit, 10*oneUnit, 670*oneUnit, 20*oneUnit);
        ShapeDrawable wall2 = new ShapeDrawable(new RectShape());
        wall2.setBounds(10*oneUnit, 520*oneUnit, 497*oneUnit, 530*oneUnit);
        ShapeDrawable wall3 = new ShapeDrawable(new RectShape());
        wall3.setBounds(10*oneUnit, 809*oneUnit, 670*oneUnit, 819*oneUnit);
        ShapeDrawable wall4 = new ShapeDrawable(new RectShape());
        wall4.setBounds(10*oneUnit, 1142*oneUnit, 670*oneUnit, 1152*oneUnit);

        // Vertical Walls
        ShapeDrawable wall5 = new ShapeDrawable(new RectShape());
        wall5.setBounds(10*oneUnit, 10*oneUnit, 20*oneUnit, 1152*oneUnit);
        ShapeDrawable wall6 = new ShapeDrawable(new RectShape());
        wall6.setBounds(497*oneUnit, 10*oneUnit, 507*oneUnit, 809*oneUnit);
        ShapeDrawable wall7 = new ShapeDrawable(new RectShape());
        wall7.setBounds(670*oneUnit, 10*oneUnit, 680*oneUnit, 1152*oneUnit);

        walls.add(wall1);
        walls.add(wall2);
        walls.add(wall3);
        walls.add(wall4);
        walls.add(wall5);
        walls.add(wall6);
        walls.add(wall7);

        // create a map
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        Canvas areaMap = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setTextSize(50*oneUnit);
        areaMap.drawText("1", 258*oneUnit, 270*oneUnit, paint);
        areaMap.drawText("2", 588*oneUnit, 414*oneUnit, paint);
        areaMap.drawText("3", 258*oneUnit, 669*oneUnit, paint);
        areaMap.drawText("4", 345*oneUnit, 980*oneUnit, paint);

        // draw the objects
        for(ShapeDrawable wall : walls) {
            wall.getPaint().setColor(Color.BLACK);
            wall.draw(areaMap);
        }

        return bitmap;
    }

    public int[] getCellCenter(int cellId, int width, int height){
        // left, top, right, bottom
        int[] coords = new int[4];
        switch (cellId){
            case 1:
                coords[0] = 308*oneUnit;
                coords[1] = 320*oneUnit;
                break;
            case 2:
                coords[0] = 638*oneUnit;
                coords[1] = 464*oneUnit;
                break;
            case 3:
                coords[0] = 308*oneUnit;
                coords[1] = 719*oneUnit;
                break;
            case 4:
                coords[0] = 395*oneUnit;
                coords[1] = 1030*oneUnit;
                break;
        }
        coords[2] = coords[0] + width*oneUnit;
        coords[3] = coords[1] + height*oneUnit;
        return coords;
    }
}
