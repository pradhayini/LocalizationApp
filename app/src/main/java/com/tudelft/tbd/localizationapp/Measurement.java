package com.tudelft.tbd.localizationapp;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Defines structure of each RSS measurement
 */
@Entity
public class Measurement {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "cell_id")
    private int cellId;

    @ColumnInfo(name = "bss_id")
    private String bssId;

    @ColumnInfo(name = "rssi_dBm")
    private int rssi;

    public int getCellId() {
        return cellId;
    }

    public void setCellId(int cellId) {

        this.cellId = cellId;
    }

    public String getBssId() {

        return bssId;
    }

    public void setBssId(String bssId) {

        this.bssId = bssId;
    }

    public int getRssi() {

        return rssi;
    }

    public void setRssi(int rssi) {

        this.rssi = rssi;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
