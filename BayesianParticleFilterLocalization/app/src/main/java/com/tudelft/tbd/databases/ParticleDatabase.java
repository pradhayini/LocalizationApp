package com.tudelft.tbd.databases;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.tudelft.tbd.dao.ParticleDescriptorDao;
import com.tudelft.tbd.entities.ParticleDescriptor;

/**
 * Class containing DAOs to required tables in the in-memory particle descriptor database
 * with a singleton instance
 * Reference: https://codelabs.developers.google.com/codelabs/android-room-with-a-view/#6
 */
@Database(entities = {ParticleDescriptor.class}, version = 1)
public abstract class ParticleDatabase extends RoomDatabase {

    private static ParticleDatabase INSTANCE;

    public abstract ParticleDescriptorDao particleDescriptorDao();

    /**
     * Get singleton instance of ParticleDatabase
     * @param context Application context
     * @return Instance of ParticleDatabase
     */
    public static ParticleDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (ParticleDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.inMemoryDatabaseBuilder(context.getApplicationContext(),
                            ParticleDatabase.class)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
