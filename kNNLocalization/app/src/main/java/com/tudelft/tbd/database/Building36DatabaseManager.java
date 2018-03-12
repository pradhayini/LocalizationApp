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
