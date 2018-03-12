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

    @Query("SELECT * FROM trainingMeasurement")
    List<TrainingMeasurement> getAll();

    @Query("SELECT DISTINCT(cell_id) FROM trainingMeasurement")
    List<Integer> getAllCellIds();

    @Query("SELECT DISTINCT(bss_id) FROM trainingMeasurement")
    List<String> getAllBssIds();

    @Query("SELECT AVG(rssi) FROM trainingMeasurement WHERE cell_id LIKE :cellId AND bss_id LIKE :bssId")
    int getAvgRssPerBssIdPerCellId(int cellId, String bssId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(TrainingMeasurement... trainingMeasurements);

    @Update
    public void updateMeasurements(TrainingMeasurement... trainingMeasurements);

    @Delete
    void delete(TrainingMeasurement trainingMeasurement);
}
