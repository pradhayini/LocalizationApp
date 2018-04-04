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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Access radio map from database, stored as Gaussian PMF parameters.
 */

@Entity(tableName = "pmf_parameters", primaryKeys = {"cell_id", "bss_id"})
public class RadioMap {
    @ColumnInfo(name = "bss_id")
    @NonNull
    private String bssId;

    @ColumnInfo(name = "cell_id")
    private int cellId;

    @ColumnInfo(name = "mean")
    private float mean;

    @ColumnInfo(name = "variance")
    private float variance;

    @ColumnInfo(name = "upper_limit")
    private float upperLimit;

    @ColumnInfo(name = "lower_limit")
    private float lowerLimit;

    public int getCellId() { return cellId; }
    public void setCellId(int cellId) { this.cellId = cellId; }

    public String getBssId() { return bssId; }
    public void setBssId(String bssId) { this.bssId = bssId; }

    public float getMean() { return mean; }
    public float getVariance() { return variance; }
    public float getUpperLimit() { return upperLimit; }
    public float getLowerLimit() { return lowerLimit; }

    public RadioMap(int cellId, String bssId, float mean, float variance, float upperLimit, float lowerLimit){
        this.cellId = cellId;
        this.bssId = bssId;
        this.mean = mean;
        this.variance = variance;
        this.upperLimit = upperLimit;
        this.lowerLimit = lowerLimit;
    }

}
