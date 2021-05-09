package com.ragav.cashkaro.DatabaseUtils;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Model.class}, version = 1)
public abstract class ModelDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "model_db";
    public abstract ModelDao modelDao();
    public static ModelDatabase INSTANCE;

    public static ModelDatabase getInstance(final Context context){
        if(INSTANCE==null){
            synchronized (ModelDatabase.class){
                if(INSTANCE==null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),ModelDatabase.class,DATABASE_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
