package com.tudelft.tbd.localizationapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pradhayini on 25-2-18.
 */

public class HomeMapCreator implements MapCreator{

    private List<ShapeDrawable> walls;
    private int width;
    private int height;

    public Bitmap drawHomeMap(int width, int height) {

        this.width = width;
        this.height = height;

        // Set the map dimensions
        int mapWidth = 640+40;
        int mapHeight = 1102+60;

        // Calculate scale
        float floatOneUnit = (float) width/mapWidth;
        if(mapHeight * floatOneUnit > height)
            floatOneUnit = (float) height/mapHeight;

        int oneUnit = (int) floatOneUnit;

        walls = new ArrayList<>();
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

        // draw the objects
        for(ShapeDrawable wall : walls) {
            wall.getPaint().setColor(Color.BLACK);
            wall.draw(areaMap);
        }

        return bitmap;
    }
}
