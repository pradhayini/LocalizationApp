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
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Data Access Objects for Radio Map
 */
@Dao
public interface RadioMapDao {

    @Query("SELECT * FROM pmf_parameters")
    List<RadioMap> getAll();

    @Query("SELECT DISTINCT(cell_id) FROM pmf_parameters")
    List<Integer> getCellIds();

    @Query("SELECT DISTINCT(bss_id) FROM pmf_parameters")
    List<String> getBssIds();

    @Query("SELECT * FROM pmf_parameters WHERE cell_id LIKE :cellId AND bss_id LIKE :bssId")
    RadioMap getParameters(int cellId, String bssId);

    @Query("SELECT * FROM pmf_parameters WHERE bss_id LIKE :bssId")
    List<RadioMap> getParametersForBssId(String bssId);
}
