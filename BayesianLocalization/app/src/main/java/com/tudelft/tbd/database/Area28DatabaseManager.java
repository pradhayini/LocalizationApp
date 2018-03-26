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

package com.tudelft.tbd.database;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Manage creation of an instance of the data base for Home measurements
 * Reference: https://stackoverflow.com/a/9109728/2169877
 */

public class Area28DatabaseManager {

    private static AppDatabase database;
    private static String DB_NAME = "area28_database";
    private Context context;

    /**
     * Constructor
     * @param context: Application context
     */
    public Area28DatabaseManager(Context context){
        this.context = context;
    }

    /**
     * Get singleton instance of the database
     * @return : instance of database
     */
    private static synchronized AppDatabase getDatabaseInstance(Context context){
        if(database == null && context != null){

            // Create Room database builder to read/write to database
            database =  Room.databaseBuilder(context,
                    AppDatabase.class, DB_NAME).build();

            // Copy pre-created database to application runtime memory space
            try {
                copyDatabase(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return database;
    }

    /**
     * Copy the pre-created database from assets
     */
    private static void copyDatabase(Context context) throws IOException {
        InputStream inputStream = context.getAssets().open(DB_NAME);
        String outFileName = context.getDatabasePath(DB_NAME).getAbsolutePath();
        OutputStream outputStream = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer))>0)
        {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }

    /**
     * Get all cells defined in the database
     * Reference for AsyncTask: https://stackoverflow.com/a/17596496/2169877
     * @return List of cells
     */
    public List<Cell> getAllCells(int floor){
        List<Cell> cells = new ArrayList<>();
        try {
            cells = (new GetCells()).execute(new Integer[]{floor}).get();//getDatabaseInstance().cellsDao().getAll();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return cells;
    }

    /**
     * Get all walls defined in the database
     * @return List of walls
     */
    public List<Boundary> getAllWalls(int floor){
        List<Boundary> walls = new ArrayList<>();
        try {
            walls = (new GetWalls()).execute(new Integer[]{floor}).get();
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
        List<Boundary> doors = new ArrayList<>();
        try {
            doors = (new GetDoors()).execute(new Integer[]{floor}).get();
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
        List<Boundary> partitions = new ArrayList<>();
        try {
            partitions = (new GetPartitions()).execute(new Integer[]{floor}).get();
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
        List<Boundary> borders = new ArrayList<>();
        try {
            borders = (new GetBorders()).execute(new Integer[]{floor}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return borders;
    }

    private class GetCells extends AsyncTask<Integer, Void, List<Cell>> {

        @Override
        protected List<Cell> doInBackground(Integer... floors) {
            List<Cell> cells =  Area28DatabaseManager.getDatabaseInstance(context).cellsDao().getAll(floors[0]);
            return cells;
        }
    }

    private class GetWalls extends AsyncTask<Integer, Void, List<Boundary>> {

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return Area28DatabaseManager.getDatabaseInstance(context).boundariesDao().getAllWalls(floors[0]);
        }
    }

    private class GetDoors extends AsyncTask<Integer, Void, List<Boundary>> {

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return Area28DatabaseManager.getDatabaseInstance(context).boundariesDao().getAllDoors(floors[0]);
        }
    }

    private class GetPartitions extends AsyncTask<Integer, Void, List<Boundary>> {

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return Area28DatabaseManager.getDatabaseInstance(context).boundariesDao().getAllPartitions(floors[0]);
        }
    }

    private class GetBorders extends AsyncTask<Integer, Void, List<Boundary>> {

        @Override
        protected List<Boundary> doInBackground(Integer... floors) {
            return Area28DatabaseManager.getDatabaseInstance(context).boundariesDao().getAllBorders(floors[0]);
        }
    }
}
