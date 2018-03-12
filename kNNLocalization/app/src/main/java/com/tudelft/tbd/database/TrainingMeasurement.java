package com.tudelft.tbd.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Defines structure of each RSS measurement
 */
@Entity(tableName = "trainingMeasurement")
public class TrainingMeasurement {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "time_stamp")
    private long timestamp;

    @ColumnInfo(name = "cell_id")
    private int cellId;

    @ColumnInfo(name = "bss_id")
    private String bssId;

    @ColumnInfo(name = "rssi")
    private int rssi;

    public int getCellId() { return cellId; }
    public void setCellId(int cellId) { this.cellId = cellId; }

    public String getBssId() { return bssId; }
    public void setBssId(String bssId) { this.bssId = bssId; }

    public int getRssi() { return rssi; }
    public void setRssi(int rssi) { this.rssi = rssi; }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public TrainingMeasurement(int cellId, String bssId, int rssi, long timestamp){
        this.cellId = cellId;
        this.bssId = bssId;
        this.rssi = rssi;
        this.timestamp = timestamp;
    }

}
