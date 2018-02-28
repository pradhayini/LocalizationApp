package com.tudelft.tbd.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Measurements Database
 * Reference: https://developer.android.com/training/data-storage/room/index.html
 */

@Database(entities = {TrainingMeasurement.class, Measurement.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TrainingMeasurementDao measurementDao();
    public abstract MeasurementDao trainedDataDao();
}
