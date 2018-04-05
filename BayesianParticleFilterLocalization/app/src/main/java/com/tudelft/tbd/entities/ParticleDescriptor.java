package com.tudelft.tbd.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

/**
 * In-memory database that stores particle descriptors
 */
@Entity(tableName = "particles", primaryKeys = {"x", "y", "floor"})
public class ParticleDescriptor {
    @ColumnInfo(name = "x")
    private int x;

    @ColumnInfo(name = "y")
    private int y;

    @ColumnInfo(name = "floor")
    private int floor;

    @ColumnInfo(name = "cell_id")
    private int cell_id;

    @ColumnInfo(name = "weight")
    private float weight;

    public ParticleDescriptor(int x, int y, int floor, int cell_id, float weight){
        this.x = x;
        this.y = y;
        this.floor = floor;
        this.cell_id = cell_id;
        this.weight = weight;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }
    public int getFloor() { return floor; }
    public int getCell_id() {
        return cell_id;
    }
    public float getWeight() {
        return weight;
    }

    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setFloor(int floor) { this.floor = floor; }
    public void setCell_id(int cellId) { this.cell_id = cellId; }
    public void setWeight(float weight) {
        this.weight = weight;
    }
}
