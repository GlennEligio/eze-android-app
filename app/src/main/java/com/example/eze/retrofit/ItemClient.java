package com.example.eze.retrofit;

import com.example.eze.model.Item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ItemClient {

    @GET("api/item/{id}")
    Call<Item> getItem(@Path("id") String id);

    @GET("api/item")
    Call<List<Item>> getItems();

    @PUT("api/item/{id}")
    Call<Void> updateItem(@Path("id") String id);

    @POST("api/item")
    Call<Void> insertItem();

    @DELETE("api/item")
    Call<Void> deleteItem();

}
