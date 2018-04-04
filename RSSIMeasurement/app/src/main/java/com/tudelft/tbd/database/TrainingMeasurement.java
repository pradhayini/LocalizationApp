package com.tudelft.tbd.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Defines structure of each RSS measurement
 */
@Entity(tableName = "training_measurement")
public class TrainingMeasurement {
    @PrimaryKey(autoGenerate = true)
    private Integer id;

    @ColumnInfo(name = "time_stamp")
    private long timestamp;

    @ColumnInfo(name = "cell_id")
    private int cellId;

    @ColumnInfo(name = "bss_id")
    private String bssId;

    @ColumnInfo(name = "vendor_id")
    private String vendorId;

    @ColumnInfo(name = "rssi")
    private int rssi;

    int getCellId() { return cellId; }
    String getBssId() { return bssId; }
    String getVendorId() { return vendorId; }
    int getRssi() { return rssi; }
    public Integer getId() { return id; }
    long getTimestamp() { return timestamp; }

    public void setId(Integer id) { this.id = id; }

    public TrainingMeasurement(int cellId, String bssId, String vendorId, int rssi, long timestamp){
        this.cellId = cellId;
        this.bssId = bssId;
        this.vendorId = vendorId;
        this.rssi = rssi;
        this.timestamp = timestamp;
    }


}
