package com.ragav.cashkaro.DatabaseUtils;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ModelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertData(Model model);

    @Delete
    void DeleteData(Model model);

    @Query("SELECT * FROM model_table")
    LiveData<List<Model>> getAlldata();
}
