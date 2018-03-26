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

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

/**
 * Database entity representing cells on the map.
 */

@Entity(tableName = "cells", primaryKeys = {"id", "floor"})
public class Cell {

    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "floor")
    private int floor;

    @ColumnInfo(name = "left")
    private int left;

    @ColumnInfo(name = "top")
    private int top;

    @ColumnInfo(name = "right")
    private int right;

    @ColumnInfo(name = "bottom")
    private int bottom;


    Cell(int id, int floor, int left, int top, int right, int bottom) {
        this.id = id;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.floor = floor;
    }

    public int getId() { return id; }
    public int getLeft() {
        return left;
    }
    public int getTop() {
        return top;
    }
    public int getRight() {
        return right;
    }
    public int getBottom() {
        return bottom;
    }
    public int getFloor() { return floor;}

    void setFloor(int floor) { this.floor = floor; }
    void setId(int id) {
        this.id = id;
    }
}
