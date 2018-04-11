package com.mongmx.vendingmachine.api;

import com.mongmx.vendingmachine.models.User;
import com.mongmx.vendingmachine.models.Category;
import com.mongmx.vendingmachine.models.LoginData;
import com.mongmx.vendingmachine.models.Order;
import com.mongmx.vendingmachine.models.Queue;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;

public interface APIService {
    @POST("/authenticate")
    void postAuthCallback(@Body LoginData loginData, Callback<User> callback);

    @GET("/product_list")
    void getProductListCallback(Callback<List<Category>> callback);

    @POST("/order")
    void postOrderCallback(@Body List<Order> orders, Callback<Queue> callback);

    @GET("/queue")
    void getQueueCallback(Callback<List<Queue>> callback);

    @POST("/queue_close")
    void postCloseQueue(@Body Queue queue, Callback<String> callback);

//    @POST("/product") #Product CRUD
//    @GET("/product") #Product CRUD
//    @PUT("/product") #Product CRUD
//    @DELETE("/product") #Product CRUD

//    @GET("/report")
}