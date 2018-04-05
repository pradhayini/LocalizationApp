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

package com.tudelft.tbd.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.tudelft.tbd.dao.BoundaryDao;
import com.tudelft.tbd.dao.CellDao;
import com.tudelft.tbd.databases.Area28Database;
import com.tudelft.tbd.entities.Boundary;
import com.tudelft.tbd.entities.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Repository class that provides access to data in Area28Database
 *
 * For LiveData implementation:
 * Reference: https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#7
 */

public class Area28Repository {

    private CellDao cellsDao;
    private BoundaryDao boundariesDao;

    /**
     * Constructor
     * @param application: Application context
     */
    public Area28Repository(Application application){
        Area28Database db = Area28Database.getDatabase(application);
        cellsDao = db.cellsDao();
        boundariesDao = db.boundariesDao();
    }

    /**
     * Get all cells defined in the database
     * Reference for AsyncTask: https://stackoverflow.com/a/17596496/2169877
     * @return List of cells
     */
    public List<Cell> getAllCells(){
        List<Cell> cells = Collections.synchronizedList(new ArrayList<Cell>());
        try {
            cells = (new GetAllCells(cellsDao)).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return cells;
    }

    /**
     * Get all cells defined in the database for a specific floor
     * Reference for AsyncTask: https://stackoverflow.com/a/17596496/2169877
     * @return List of cells
     */
    public List<Cell> getAllCells(int floor){
        List<Cell> cells = Collections.synchronizedList(new ArrayList<Cell>());
        try {
            cells = (new GetCells(cellsDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return cells;
    }

    /**
     * Get all walls and borders defined in the database for specified floor
     * @return List of walls
     */
    public List<Boundary> getAllClosedBorders(int floor){
        List<Boundary> walls = Collections.synchronizedList(new ArrayList<Boundary>());
        try {
            walls = (new GetAllClosedBorders(boundariesDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return walls;
    }

    /**
     * Get all walls defined in the database
     * @return List of walls
     */
    public List<Boundary> getAllWalls(int floor){
        List<Boundary> walls = Collections.synchronizedList(new ArrayList<Boundary>());
        try {
            walls = (new GetWalls(boundariesDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return walls;
    }

    /**
     * Get all doors defined in the database
     * @return List of doors
     */
    public List<Boundary> getAllDoors(int floor){
        List<Boundary> doors = Collections.synchronizedList(new ArrayList<Boundary>());
        try {
            doors = (new GetDoors(boundariesDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return doors;
    }

    /**
     * Get all passage partitions defined in the database
     * @return List of partitions
     */
    public List<Boundary> getAllPartitions(int floor){
        List<Boundary> partitions = Collections.synchronizedList(new ArrayList<Boundary>());
        try {
            partitions = (new GetPartitions(boundariesDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return partitions;
    }

    /**
     * Get all area borders defined in the database
     * @return List of borders
     */
    public List<Boundary> getAllBorders(int floor){
        List<Boundary> borders = Collections.synchronizedList(new ArrayList<Boundary>());
        try {
            borders = (new GetBorders(boundariesDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return borders;
    }

    /**
     * Get cell data corresponding to cellId
     * @param cellId ID of cell
     * @return Cell data
     */
    public Cell getCell(int cellId){
        Cell cell = null;
        try {
            cell = (new GetCell(cellsDao)).execute(new Integer[]{cellId}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return cell;
    }

    /**
     * Get all cell IDs with data in database
     * @return List of cell IDs
     */
    public List<Integer> getAllCellIds() {
        List<Integer> cellIds = null;
        try {
            cellIds = (new GetAllCellIds(cellsDao)).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return cellIds;
    }

    /**
     * Get all horizontal walls surrounding navigable cells on specified floor
     * @param floor floor number
     * @return List of horizontal walls on specified floor
     */
    public List<Boundary> getAllHorWalls(int floor) {
        List<Boundary> topLimits = null;
        try{
            topLimits = (new GetAllHorWalls(boundariesDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return topLimits;
    }

    /**
     * Get all vertical walls surrounding navigable cells on specified floor
     * @param floor floor number
     * @return List of vertical walls on specified floor
     */
    public List<Boundary> getAllVerWalls(int floor) {
        List<Boundary> leftLimits = null;
        try{
            leftLimits = (new GetAllVerWalls(boundariesDao)).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return leftLimits;
    }

    /**
     * AsyncTask wrappers around Dao methods
     */

    private static class GetAllCells extends AsyncTask<Void, Void, List<Cell>> {
        private CellDao dao;

        GetAllCells(CellDao cellDao){
            this.dao = cellDao;
        }

        @Override
        protected List<Cell> doInBackground(Void... voids) {
            return dao.getAll();
        }
    }

    private static class GetCells extends AsyncTask<Integer, Void, List<Cell>> {
        private CellDao dao;

        GetCells(CellDao cellDao){
            this.dao = cellDao;
        }

        @Override
        protected List<Cell> doInBackground(Integer... floors) {
            return dao.getAll(floors[0]);
        }
    }

    private static class GetAllClosedBorders extends AsyncTask<Integer, Void, List<Boundary>> {
        private BoundaryDao dao;

        GetAllClosedBorders(BoundaryDao dao){ this.dao = dao; }

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return dao.getAllClosedBorders(floors[0]);
        }
    }

    private static class GetWalls extends AsyncTask<Integer, Void, List<Boundary>> {
        private BoundaryDao dao;

        GetWalls(BoundaryDao dao){ this.dao = dao; }

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return dao.getAllWalls(floors[0]);
        }
    }

    private static class GetDoors extends AsyncTask<Integer, Void, List<Boundary>> {
        private BoundaryDao dao;

        GetDoors(BoundaryDao dao){ this.dao = dao; }

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return dao.getAllDoors(floors[0]);
        }
    }

    private static class GetPartitions extends AsyncTask<Integer, Void, List<Boundary>> {
        private BoundaryDao dao;

        GetPartitions(BoundaryDao dao){ this.dao = dao; }

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return dao.getAllPartitions(floors[0]);
        }
    }

    private static class GetBorders extends AsyncTask<Integer, Void, List<Boundary>> {
        private BoundaryDao dao;

        GetBorders(BoundaryDao dao){ this.dao = dao; }

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return dao.getAllBorders(floors[0]);
        }
    }

    private static class GetCell extends AsyncTask<Integer, Void, Cell>{
        private CellDao dao;

        GetCell(CellDao dao){ this.dao = dao; }

        @Override
        protected Cell doInBackground(Integer... cellIds) {
            return dao.getCell(cellIds[0]);
        }
    }

    private static class GetAllCellIds extends AsyncTask<Void, Void, List<Integer>>{
        private CellDao dao;

        GetAllCellIds(CellDao dao){ this.dao = dao; }

        @Override
        protected List<Integer> doInBackground(Void... voids) {
            return dao.getAllCellIds();
        }
    }

    private static class GetAllHorWalls extends AsyncTask<Integer, Void, List<Boundary>>{
        private BoundaryDao dao;

        GetAllHorWalls(BoundaryDao dao){ this.dao = dao; }

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return dao.getAllHorWalls(floors[0]);
        }
    }

    private static class GetAllVerWalls extends AsyncTask<Integer, Void, List<Boundary>>{
        private BoundaryDao dao;

        GetAllVerWalls(BoundaryDao dao){ this.dao = dao; }

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return dao.getAllVerWalls(floors[0]);
        }
    }
}


