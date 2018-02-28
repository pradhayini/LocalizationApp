package com.tudelft.tbd.lib;

import android.os.AsyncTask;

import com.tudelft.tbd.database.AppDatabase;
import com.tudelft.tbd.database.Measurement;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.sqrt;

/**
 * Identify current cell location using kNN localization
 */

public class kNNLocalization {

    private static AppDatabase database;
    private Measurement[] measurements;

    public kNNLocalization(AppDatabase db){
        database = db;
        measurements = new Measurement[]{};
    }

    public Integer locate(List<Measurement> measurements){

        Integer location = 0;
        try {
            location = new DetermineLocation().execute(measurements.toArray(new Measurement[]{})).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return location;
    }

    private class DetermineLocation extends AsyncTask<Measurement, Void, Integer> {

        @Override
        protected Integer doInBackground(Measurement... measurements) {
            int location = 0;

            // cellId indexed from 1 for stored data
            // cellId 0 is assigned to current measurement
            List<Integer> cellIds = database.trainedDataDao().getCellIds();

            Double[] distances = new Double[cellIds.size()];
            Double minDistance = Double.MAX_VALUE;

            if(measurements == null)
                return 0;

            for(int cellId : cellIds){
                int diffSq = 0;
                for(Measurement measurement : measurements){
                    int diff = (measurement.getRssi() - database.trainedDataDao().getRssi(cellId, measurement.getBssId()));
                    diffSq += (diff * diff);
                }
                distances[cellId-1] = sqrt(diffSq);
                if(distances[cellId-1].compareTo(minDistance) < 0){
                    minDistance = distances[cellId-1];
                    location = cellId;
                }
            }
            return location;
        }
    }
}
