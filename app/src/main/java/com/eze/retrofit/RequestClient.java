package com.eze.retrofit;

import com.eze.dtos.CreateRequest;
import com.eze.dtos.RequestDto;
import com.eze.dtos.UpdateRequest;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RequestClient {

    @GET("api/request-pending/{id}")
    Call<ArrayList<RequestDto>> getPendingRequest(@Header("Authorization") String header,
                                                  @Path("id") String id);

    @POST("api/request")
    Call<Void> createRequest(@Body CreateRequest createRequest);

    @PUT("api/request/{id}")
    Call<Void> updateRequest(@Header("Authorization") String token,
                            @Path("id") String id,
                             @Body UpdateRequest updateRequest);

    @DELETE("api/request/{id}/")
    Call<Void> deleteRequest(@Path("id") String id);

}
