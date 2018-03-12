package com.tudelft.tbd.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Data Access Objects for Trained Measurements
 */
@Dao
public interface MeasurementDao {

    @Query("SELECT * FROM measurement")
    List<Measurement> getAll();

    @Query("SELECT DISTINCT(cell_id) FROM measurement")
    List<Integer> getCellIds();

    @Query("SELECT DISTINCT(bss_id) FROM measurement")
    List<String> getBssIds();

    @Query("SELECT rssi FROM measurement WHERE cell_id LIKE :cellId AND bss_id LIKE :bssId")
    int getRssi(int cellId, String bssId);

    @Query("SELECT * FROM measurement WHERE cell_id LIKE :cellId")
    List<Measurement> getMeasurementsPerCellId(int cellId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Measurement... measurements);

    @Delete
    void delete(Measurement measurement);
}
