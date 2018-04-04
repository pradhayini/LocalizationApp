package com.tudelft.tbd.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Data Access Objects for TrainingMeasurement
 */
@Dao
public interface TrainingMeasurementDao {

    @Query("SELECT * FROM training_measurement")
    List<TrainingMeasurement> getAll();

    @Query("SELECT DISTINCT(cell_id) FROM training_measurement")
    List<Integer> getAllCellIds();

    @Query("SELECT DISTINCT(bss_id) FROM training_measurement")
    List<String> getAllBssIds();

    @Query("SELECT AVG(rssi) FROM training_measurement WHERE cell_id LIKE :cellId AND bss_id LIKE :bssId")
    int getAvgRssPerBssIdPerCellId(int cellId, String bssId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(TrainingMeasurement... trainingMeasurements);

    @Update
    public void updateMeasurements(TrainingMeasurement... trainingMeasurements);

    @Delete
    void delete(TrainingMeasurement trainingMeasurement);
}
