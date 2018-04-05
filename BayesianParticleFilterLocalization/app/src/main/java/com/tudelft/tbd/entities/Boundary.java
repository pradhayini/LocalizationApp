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

package com.tudelft.tbd.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

/**
 * Database entity representing boundaries (walls, doors, passage partitions) on the map.
 */
@Entity(tableName = "boundaries", primaryKeys = {"id", "type"})
public class Boundary {

    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "type")
    private String type;

    @ColumnInfo(name = "left")
    private int left;

    @ColumnInfo(name = "top")
    private int top;

    @ColumnInfo(name = "right")
    private int right;

    @ColumnInfo(name = "bottom")
    private int bottom;

    @ColumnInfo(name = "cell_id_1")
    private int cell_id_1;

    @ColumnInfo(name = "cell_id_2")
    private int cell_id_2;

    @ColumnInfo(name = "floor")
    private int floor;

    public Boundary(int id, @NonNull String type, int left, int top, int right, int bottom, int floor, int cell_id_1,
             int cell_id_2) {
        this.id = id;
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.type = type;
        this.floor = floor;
        this.cell_id_1 = cell_id_1;
        this.cell_id_2 = cell_id_2;
    }

    public int getId() { return id; }
    public int getLeft() { return left; }
    public int getTop() {
        return top;
    }
    public int getRight() {
        return right;
    }
    public int getBottom() {
        return bottom;
    }

    @NonNull
    public String getType() { return type; }

    public int getCell_id_1() { return cell_id_1; }
    public int getCell_id_2() { return cell_id_2; }
    public int getFloor() { return floor; }
}
