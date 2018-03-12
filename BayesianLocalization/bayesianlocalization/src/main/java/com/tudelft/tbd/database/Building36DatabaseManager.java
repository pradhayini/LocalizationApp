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

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Manage creation of an instance of the data base for Home measurements
 */

public class Building36DatabaseManager  {

    private static AppDatabase database;

    /**
     * Get singleton instance of the database
     * @param context : application context
     * @return : instance of database
     */
    public static synchronized AppDatabase getDatabaseInstance(Context context){
        if(database == null && context != null){
            database =  Room.databaseBuilder(context,
                    AppDatabase.class, "building36_database").build();
        }
        return database;
    }
}
