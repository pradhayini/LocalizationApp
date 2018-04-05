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

package com.tudelft.tbd.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.tudelft.tbd.entities.Boundary;

import java.util.List;

/**
 * Created by pradhayini on 23-3-18.
 */

@Dao
public interface BoundaryDao {

    @Query("SELECT * FROM boundaries")
    List<Boundary> getAll();

    @Query("SELECT * FROM boundaries WHERE (type='wall' OR type='brdr') AND (floor=:floor OR floor=0)")
    List<Boundary> getAllClosedBorders(int floor);

    @Query("SELECT * FROM boundaries WHERE type='wall' AND (floor=:floor OR floor=0)")
    List<Boundary> getAllWalls(int floor);

    @Query("SELECT * FROM boundaries WHERE (type='wall' OR type='brdr') AND (floor=:floor OR floor=0) AND (bottom-top=10)")
    List<Boundary> getAllHorWalls(int floor);

    @Query("SELECT * FROM boundaries WHERE (type='wall' OR type='brdr') AND (floor=:floor OR floor=0) AND (right-`left`=10)")
    List<Boundary> getAllVerWalls(int floor);

    @Query("SELECT * FROM boundaries WHERE type='door' AND (floor=:floor OR floor=0)")
    List<Boundary> getAllDoors(int floor);

    @Query("SELECT * FROM boundaries WHERE type='part' AND (floor=:floor OR floor=0)")
    List<Boundary> getAllPartitions(int floor);

    @Query("SELECT * FROM boundaries WHERE type='brdr' AND (floor=:floor OR floor=0)")
    List<Boundary> getAllBorders(int floor);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Boundary... boundaries);

    @Update
    void updateCoordinates(Boundary... boundaries);

    @Delete
    void delete(Boundary boundary);
}
