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

package com.tudelft.tbd.map;

import android.app.Application;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.util.SparseArray;

import com.tudelft.tbd.entities.Boundary;
import com.tudelft.tbd.entities.Cell;
import com.tudelft.tbd.entities.ParticleDescriptor;
import com.tudelft.tbd.localization.R;
import com.tudelft.tbd.repositories.Area28Repository;
import com.tudelft.tbd.repositories.ParticleRepository;

import java.util.Arrays;
import java.util.List;

/**
 * Generates, displays and updates the map of Building 28
 */

public class Area28MapModel {
    private Area28Repository repository;
    private ParticleRepository particleRepository;
    private static int colorLightSteelBlue;
    private static int colorSteelBlue;
    private List<Integer> possibleLocations;
    private static final int mapHeight = 2960;
    private static final int mapWidth = 1450;
    private int floor;
    private boolean showParticles;

    /**
     * Record of cells per floor in Building28
     */
    private static SparseArray<List<Integer>> cellsPerFloor;
    static {
        cellsPerFloor = new SparseArray<List<Integer>>();
        cellsPerFloor.put(3, Arrays.asList(17, 18, 19));
        cellsPerFloor.put(4, Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16));
    }

    public Area28MapModel(Area28Repository repository, ParticleRepository particleRepository, Application application){

        this.repository = repository;
        this.particleRepository = particleRepository;
        colorLightSteelBlue = application.getColor(R.color.LightSteelBlue);
        colorSteelBlue = application.getColor(R.color.SteelBlue);
    }

    public Area28MapModel(Area28Repository area28Repository, Application application) {
        this(area28Repository, null, application);
    }

    public void setPossibleLocations(List<Integer> possibleLocations){
        this.possibleLocations = possibleLocations;
    }

    public void setFloor(int floor){
        this.floor = floor;
    }

    public void setShowParticles(boolean showParticles){
        this.showParticles = showParticles;
    }

    public int[] getCurrentCellCenter() {
        if(possibleLocations == null || possibleLocations.isEmpty())
            return null;

        int coord[] = {0, 0};
        int cellId = possibleLocations.get(0);
        Cell cell = repository.getCell(cellId);
        if(cell != null){
            int halfWidth = (cell.getRight() - cell.getLeft())/2;
            int halfHeight = (cell.getBottom() - cell.getTop())/2;
            coord[0] = (cell.getLeft() + halfWidth);
            coord[1] = (cell.getTop() + halfHeight);
        }

        return coord;
    }

    public void createMap(Canvas canvas) {
        // Check if parameters for map generation have changed
        if((repository == null) || (floor > 4) || (floor < 3)) {
            return;
        }

        canvas.drawColor(Color.WHITE);

        List<ParticleDescriptor> particles = (particleRepository != null) ?
                particleRepository.getAllFromFloor(floor) :
                null;

        List<Cell> cells = repository.getAllCells(floor);
        List<Boundary> walls = repository.getAllWalls(floor);
        List<Boundary> doors = repository.getAllDoors(floor);
        List<Boundary> partitions = repository.getAllPartitions(floor);
        List<Boundary> borders = repository.getAllBorders(floor);

        if(possibleLocations != null && !possibleLocations.isEmpty()){
            // Color possible cell locations, if any for Bayesian localization
            RectShapeObj coloredCell = new RectShapeObj();
            coloredCell.getPaint().setColor(
                    (possibleLocations.size() > 1) ?
                            colorLightSteelBlue :
                            colorSteelBlue);
            for(int c : possibleLocations){
                // Colour cells that are on the currently selected floor
                if(cellsPerFloor.get(floor).contains(c)){
                    Cell cell = repository.getCell(c);
                    coloredCell.setBounds(
                            cell.getLeft(),
                            cell.getTop(),
                            cell.getRight(),
                            cell.getBottom());
                    coloredCell.draw(canvas);

                    // Cell 15 is represented as two cells in the database
                    if(c == 15){
                        cell = repository.getCell(151);
                        coloredCell.setBounds(
                                cell.getLeft(),
                                cell.getTop(),
                                cell.getRight(),
                                cell.getBottom());
                        coloredCell.draw(canvas);
                    }
                }
            }

        }

        RectShapeObj border = new RectShapeObj();

        // Draw all borders
        border.getPaint().setColor(Color.LTGRAY);
        for(Boundary boundary : borders){
            border.setBounds(
                    boundary.getLeft(),
                    boundary.getTop(),
                    boundary.getRight(),
                    boundary.getBottom());
            border.draw(canvas);
        }

        // Draw all walls
        border.getPaint().setColor(Color.BLACK);
        for (Boundary boundary : walls) {
            border.setBounds(
                    boundary.getLeft(),
                    boundary.getTop(),
                    boundary.getRight(),
                    boundary.getBottom());
            border.draw(canvas);
        }

        // Draw all doors
        border.getPaint().setColor(Color.BLUE);
        for (Boundary boundary : doors) {
            border.setBounds(
                    boundary.getLeft(),
                    boundary.getTop(),
                    boundary.getRight(),
                    boundary.getBottom());
            border.draw(canvas);
        }

        // Draw all passage partitions
        border.getPaint().setColor(Color.GREEN);
        for (Boundary boundary : partitions) {
            border.setBounds(
                    boundary.getLeft(),
                    boundary.getTop(),
                    boundary.getRight(),
                    boundary.getBottom());
            border.draw(canvas);
        }

        // Draw particles for Particle Filter localization
        if(showParticles && particles != null && !particles.isEmpty()){
            Particle drawableParticle = new Particle();
            for(ParticleDescriptor particle : particles){
                drawableParticle.setBounds((particle.getX()-8), (particle.getY()-8),
                        (particle.getX()+8), (particle.getY()+8));
                drawableParticle.draw(canvas);
            }
        }

        // Draw cell IDs
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(50);
        for(Cell cell : cells){
            if(cell.getId() > 19)
                continue;
            int x = (((cell.getRight() - cell.getLeft())/2) + cell.getLeft());
            int y = (((cell.getBottom() - cell.getTop())/2) + cell.getTop());
            canvas.drawText(Integer.toString(cell.getId()), x, y, paint);
        }
    }

    private class Particle extends ShapeDrawable{
        Particle(){
            super(new OvalShape());
            getPaint().setColor(colorSteelBlue);
        }
    }

    private class RectShapeObj extends ShapeDrawable{
        RectShapeObj(){
            super(new RectShape());
        }
    }
}
