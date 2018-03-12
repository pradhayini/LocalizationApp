package com.tudelft.tbd.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Defines structure of the kNN training results
 */
@Entity(tableName = "measurement", primaryKeys = {"cell_id", "bss_id"})
public class Measurement {
    @ColumnInfo(name = "bss_id")
    @NonNull
    private String bssId;

    @ColumnInfo(name = "cell_id")
    private int cellId;

    @ColumnInfo(name = "rssi")
    private int rssi;

    public int getCellId() { return cellId; }
    public void setCellId(int cellId) { this.cellId = cellId; }

    public String getBssId() { return bssId; }
    public void setBssId(String bssId) { this.bssId = bssId; }

    public int getRssi() { return rssi; }
    public void setRssi(int rssi) { this.rssi = rssi; }

    public Measurement(int cellId, String bssId, int rssi){
        this.cellId = cellId;
        this.bssId = bssId;
        this.rssi = rssi;
    }
}
