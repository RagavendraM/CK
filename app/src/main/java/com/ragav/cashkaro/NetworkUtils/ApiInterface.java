package com.ragav.cashkaro.NetworkUtils;

import com.ragav.cashkaro.DataModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("db.json")
    Call<List<DataModel>> getData();
}
