package com.tudelft.tbd.localizationapp;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Data Access Objects for Measurement
 */
@Dao
public interface MeasurementDao {

    @Query("SELECT * FROM measurement")
    List<Measurement> getAll();

    @Query("SELECT * FROM measurement WHERE cell_id IN (:cellIds)")
    List<Measurement> loadAllByCellIds(int[] cellIds);

    @Query("SELECT * FROM measurement WHERE bss_id IN (:bssIds)")
    List<Measurement> loadAllByBssIds(String[] bssIds);

    @Query("SELECT * FROM measurement WHERE cell_id LIKE :cellId AND bss_id LIKE :bssId")
    List<Measurement> findByIds(int cellId, String bssId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Measurement... measurements);

    @Update
    public void updateMeasurements(Measurement... measurements);

    @Delete
    void delete(Measurement measurement);
}
