package com.tudelft.tbd.databases;

import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class DatabaseBase extends RoomDatabase {

    /**
     * Copy the pre-created database from assets
     */
    static void copyDatabase(final Context context, final String database_name) throws IOException {
        if(context == null)
            return;

        InputStream inputStream = context.getAssets().open(database_name);
        String outFileName = context.getDatabasePath(database_name).getAbsolutePath();
        OutputStream outputStream = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer))>0)
        {
            outputStream.write(buffer, 0, length);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
    }
}
