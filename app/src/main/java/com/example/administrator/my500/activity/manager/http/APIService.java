package com.example.administrator.my500.activity.manager.http;

import com.example.administrator.my500.activity.dao.PhotoItemCollectionDao;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface APIService {
    //ห้ามใส่ / นำหน้า
    @POST("list")
    Call<PhotoItemCollectionDao> loadPhotoList();

    @POST("list/after/{id}")
    Call<PhotoItemCollectionDao> loadPhotoListAfterId(@Path("id") int id);

    @POST("list/before/{id}")
    Call<PhotoItemCollectionDao> loadPhotoListBeforeId(@Path("id") int id);

}
