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

package com.tudelft.tbd.lib;

import android.os.AsyncTask;

import com.tudelft.tbd.database.AppDatabase;
import com.tudelft.tbd.database.Measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * kNN training class
 */

public class kNNTraining {
    private static AppDatabase database;
    private Measurement[] measurements;

    public kNNTraining(AppDatabase db)
    {
        database = db;
        measurements = new Measurement[]{};
    }

    public void doTraining()
    {
        try {
            List<Integer> cellIds = new GetCellIds().execute().get();
            List<String> bssIds = new GetBssIds().execute().get();

            List<Measurement> trainedData = new ArrayList<>();
            for (int cellId : cellIds) {
                for(String bssId : bssIds){
                    int avgRssi = new GetAverageRssValues().execute(new AvgRssParams(cellId, bssId)).get();
                    trainedData.add(
                            new Measurement(cellId, bssId, avgRssi));
                }
            }
            new WriteTrainedVales().execute(trainedData.toArray(measurements)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private class GetCellIds extends AsyncTask<Void, Void, List<Integer>> {
        @Override
        protected List<Integer> doInBackground(Void... voids) {
            return database.measurementDao().getAllCellIds();
        }
    }

    private class GetBssIds extends AsyncTask<Void, Void, List<String>> {
        @Override
        protected List<String> doInBackground(Void... voids) {
            return database.measurementDao().getAllBssIds();
        }
    }

    private static class AvgRssParams{
        int cellId;
        String bssId;

        AvgRssParams(int cellId, String bssId){
            this.cellId = cellId;
            this.bssId = bssId;
        }
    }

    private static class GetAverageRssValues extends AsyncTask<AvgRssParams, Void, Integer> {
        @Override
        protected Integer doInBackground(AvgRssParams... avgRssParams) {
            return database.measurementDao().getAvgRssPerBssIdPerCellId(
                    avgRssParams[0].cellId, avgRssParams[0].bssId);
        }
    }

    private static class WriteTrainedVales extends AsyncTask<Measurement, Void, Void>{
        @Override
        protected Void doInBackground(Measurement... trainedData) {
            database.trainedDataDao().insertAll(trainedData);
            return null;
        }
    }

}
