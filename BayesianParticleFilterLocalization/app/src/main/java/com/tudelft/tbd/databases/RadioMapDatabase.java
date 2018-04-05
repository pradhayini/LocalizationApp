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

import com.tudelft.tbd.dao.RadioMapDao;
import com.tudelft.tbd.entities.RadioMap;

import java.io.IOException;

/**
 * Class containing DAOs to required tables in the Radio map database, with a singleton instance
 * Reference: https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#6
 *
 * Copy deployed database, if available
 * Reference: https://stackoverflow.com/a/9109728/2169877
 */
@Database(entities = {RadioMap.class}, version = 1)
public abstract class RadioMapDatabase extends DatabaseBase {

    private static RadioMapDatabase INSTANCE;
    private static final String database_name = "training_database.db";

    public abstract RadioMapDao radioMapDao();


    /**
     * Get singleton instance of RadioMapDatabase
     * @param context Application context
     * @return Instance of RadioMapDatabase
     */
    public static RadioMapDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RadioMapDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RadioMapDatabase.class, database_name)
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
