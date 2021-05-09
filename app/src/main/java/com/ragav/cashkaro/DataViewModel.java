package com.ragav.cashkaro;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.ragav.cashkaro.DatabaseUtils.Model;
import com.ragav.cashkaro.DatabaseUtils.ModelDao;
import com.ragav.cashkaro.DatabaseUtils.ModelDatabase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DataViewModel extends AndroidViewModel {


    private ModelDao modelDao;
    private ExecutorService executorService;

    public DataViewModel(@NonNull Application application) {
        super(application);
        modelDao = ModelDatabase.getInstance(application).modelDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    LiveData<List<Model>> getAllData() {
        return modelDao.getAlldata();
    }

    void saveData(Model model) {
        executorService.execute(() -> modelDao.InsertData(model));
    }

    void deleteData(Model model) {
        executorService.execute(() -> modelDao.DeleteData(model));
    }
}

