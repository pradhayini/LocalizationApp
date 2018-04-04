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

package com.tudelft.tbd.databasehandler;

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
 * Manage creation of an instance of the data base for Radio Map parameters
 * Reference: https://stackoverflow.com/a/9109728/2169877
 */

public class RadioMapDatabaseManager {
    private static RadioMapDatabase database;
    private static String DB_NAME = "training_database.db";
    private Context context;

    /**
     * Constructor
     * @param context: Application context
     */
    public RadioMapDatabaseManager(Context context){
        this.context = context;
    }

    /**
     * Get singleton instance of the database
     * @return : instance of database
     */
    private static synchronized RadioMapDatabase getDatabaseInstance(Context context){
        if(database == null && context != null){

            // Create Room database builder to read/write to database
            database =  Room.databaseBuilder(context,
                    RadioMapDatabase.class, DB_NAME).build();

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
     * Get all PMF parameters corresponding to given cell and bssid
     * @param cellId
     * @param bssId
     * @return PMF parameter
     */
    public RadioMap getParametersForCellAndBssId(int cellId, String bssId){
        RadioMap parameter = null;
        try {
            parameter = (new GetParameter(cellId, bssId)).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return parameter;
    }

    /**
     * Get all PMF parameters for all cells corresponding to given bssid
     * @param bssId
     * @return set of parameters
     */
    public List<RadioMap> getAllParametersForBssId(String bssId){
        List<RadioMap> parameters = new ArrayList<RadioMap>();
        try {
            parameters = (new GetParameterForBssId()).execute(new String[]{bssId}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return parameters;
    }

    private class GetParameter extends AsyncTask<Void, Void, RadioMap> {
        private int cellId;
        private String bssId;

        public GetParameter(int cellId, String bssId){
            this.cellId = cellId;
            this.bssId = bssId;
        }

        @Override
        protected RadioMap doInBackground(Void... voids) {
            return getDatabaseInstance(context).radioMapDao().getParameters(cellId, bssId);
        }
    }

    private class GetParameterForBssId extends AsyncTask<String, Void, List<RadioMap>> {

        @Override
        protected List<RadioMap> doInBackground(String... bssIds) {
            return getDatabaseInstance(context).radioMapDao().getParametersForBssId(bssIds[0]);
        }
    }
}
