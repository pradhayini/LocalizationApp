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
