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

package com.tudelft.tbd.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.content.Context;

import com.tudelft.tbd.dao.BoundaryDao;
import com.tudelft.tbd.dao.CellDao;
import com.tudelft.tbd.entities.Boundary;
import com.tudelft.tbd.entities.Cell;

import java.io.IOException;

/**
 * Class containing DAOs to required tables in the Area28 database, with a singleton instance
 * Reference: https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#6
 *
 * Copy deployed database, if available
 * Reference: https://stackoverflow.com/a/9109728/2169877
 */
@Database(entities = {Cell.class, Boundary.class}, version = 1)
public abstract class Area28Database extends DatabaseBase {

    private static Area28Database INSTANCE;
    private static final String database_name = "area28_database.db";

    public abstract CellDao cellsDao();
    public abstract BoundaryDao boundariesDao();

    /**
     * Get singleton instance of Area28Database
     * @param context Application context
     * @return Instance of Area28Database
     */
    public static Area28Database getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (Area28Database.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            Area28Database.class, database_name)
                            .build();

                    // Copy pre-created database to application runtime memory space
                    try {
                        copyDatabase(context, database_name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return INSTANCE;
    }
}
