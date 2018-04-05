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

import com.tudelft.tbd.entities.RadioMap;
import com.tudelft.tbd.dao.RadioMapDao;
import com.tudelft.tbd.databases.RadioMapDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Repository class that provides access to data in RadioMapDatabase
 */

public class RadioMapRepository {
    private RadioMapDao radioMapDao;

    /**
     * Constructor
     * @param application: Application context
     */
    public RadioMapRepository(Application application){

        RadioMapDatabase db = RadioMapDatabase.getDatabase(application);
        radioMapDao = db.radioMapDao();
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
            parameter = (new GetParameter(radioMapDao, cellId, bssId)).execute().get();
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
        List<RadioMap> parameters = Collections.synchronizedList(new ArrayList<RadioMap>());
        try {
            parameters = (new GetParameterForBssId(radioMapDao)).execute(new String[]{bssId}).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return parameters;
    }

    /**
     * Get all represented cells IDs
     * @return set of cellIds
     */
    public List<Integer> getAllCellIds(){
        List<Integer> cellIds = Collections.synchronizedList(new ArrayList<Integer>());
        try {
            cellIds = (new GetCellIds(radioMapDao)).execute().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return cellIds;
    }

    /**
     * Get all represented BSSIDs
     * @return List of BSSIDs
     */
    public List<String> getAllBssIDs() {
        List<String> bssIds = Collections.synchronizedList(new ArrayList<String>());
        try {
            bssIds = (new GetBssIds(radioMapDao)).execute().get();
        }catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return bssIds;
    }

    private static class GetParameter extends AsyncTask<Void, Void, RadioMap> {
        private RadioMapDao dao;

        private int cellId;
        private String bssId;

        GetParameter(RadioMapDao dao, int cellId, String bssId){
            this.dao = dao;
            this.cellId = cellId;
            this.bssId = bssId;
        }

        @Override
        protected RadioMap doInBackground(Void... voids) {
            return dao.getParameters(cellId, bssId);
        }
    }

    private static class GetParameterForBssId extends AsyncTask<String, Void, List<RadioMap>> {
        private RadioMapDao dao;

        GetParameterForBssId(RadioMapDao dao){
            this.dao = dao;
        }

        @Override
        protected List<RadioMap> doInBackground(String... bssIds) {
            return dao.getParametersForBssId(bssIds[0]);
        }
    }

    private static class GetCellIds extends AsyncTask<Void, Void, List<Integer>> {
        private RadioMapDao dao;

        GetCellIds(RadioMapDao dao){
            this.dao = dao;
        }

        @Override
        protected List<Integer> doInBackground(Void... voids) {
            return dao.getCellIds();
        }
    }

    private static class GetBssIds extends AsyncTask<Void, Void, List<String>> {
        private RadioMapDao dao;

        GetBssIds(RadioMapDao dao){
            this.dao = dao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return dao.getBssIds();
        }
    }
}
