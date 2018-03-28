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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;

import com.tudelft.tbd.databasehandler.Area28DatabaseManager;
import com.tudelft.tbd.databasehandler.Boundary;
import com.tudelft.tbd.databasehandler.Cell;

import java.util.List;

/**
 * Generates, displays and updates the map of Building 28
 */

public class Area28MapManager implements MapManager {
    private float oneUnit;
    private Area28DatabaseManager databaseManager;

    Area28MapManager(Area28DatabaseManager manager){
        databaseManager = manager;
    }

    @Override
    public Bitmap drawMap(int mapWidth, int mapHeight, int floorNum) {
        if(databaseManager == null) {
            return null;
        }

        List<Cell> cells = databaseManager.getAllCells(floorNum);
        List<Boundary> walls = databaseManager.getAllWalls(floorNum);
        List<Boundary> doors = databaseManager.getAllDoors(floorNum);
        List<Boundary> partitions = databaseManager.getAllPartitions(floorNum);
        List<Boundary> borders = databaseManager.getAllBorders(floorNum);

        int height = 2610;
        int width = 1450;

        // Calculate scale
        float h = (float)mapHeight/height;
        float w = (float)mapWidth/width;

        if(Float.compare(h, w) < 0){
            oneUnit = h;
        }
        else {
            oneUnit = w;
        }

        // create a map
        Bitmap bitmap = Bitmap.createBitmap(mapWidth, mapHeight, Bitmap.Config.ARGB_8888);
        Canvas areaMap = new Canvas(bitmap);

        // Draw all borders
        for(Boundary boundary : borders){
            DrawBoundary(boundary, Color.LTGRAY).draw(areaMap);
        }

        // Draw all walls
        for (Boundary boundary : walls) {
            DrawBoundary(boundary, Color.BLACK).draw(areaMap);
        }

        // Draw all doors
        for (Boundary boundary : doors) {
            DrawBoundary(boundary, Color.BLUE).draw(areaMap);
        }

        // Draw all passage partitions
        for (Boundary boundary : partitions) {
            DrawBoundary(boundary, Color.GREEN).draw(areaMap);
        }

        // Draw cell IDs
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(50*oneUnit);
        for(Cell cell : cells){
            DrawCellId(areaMap, paint, cell);
        }
        return bitmap;
    }

    @Override
    public int[] getCellCenter(int cellId, int width, int height) {
        return new int[2];
    }

    private ShapeDrawable DrawBoundary(Boundary boundary, int color){
        ShapeDrawable drawableBoundary = new ShapeDrawable(new RectShape());
        drawableBoundary.setBounds(
                (int)(boundary.getLeft()*oneUnit),
                (int)(boundary.getTop()*oneUnit),
                (int)(boundary.getRight()*oneUnit),
                (int)(boundary.getBottom()*oneUnit));

        drawableBoundary.getPaint().setColor(color);
        return drawableBoundary;
    }

    private void DrawCellId(Canvas canvas, Paint paint, Cell cell) {
        int x = (int)((((cell.getRight() - cell.getLeft())/2) + cell.getLeft()) * oneUnit);
        int y = (int)((((cell.getBottom() - cell.getTop())/2) + cell.getTop()) * oneUnit);
        canvas.drawText(Integer.toString(cell.getId()), x, y, paint);
    }
}
