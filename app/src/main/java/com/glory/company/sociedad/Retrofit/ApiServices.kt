package com.glory.company.sociedad.Retrofit

import com.glory.company.sociedad.Models.Store
import com.glory.company.sociedad.Models.User
import retrofit2.Call
import retrofit2.http.*


interface ApiServices {
    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("login")
    fun verifyStore(@Body store: Int) : Call<Store?>?

    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("login")
    fun loginUser(@Body login:MutableMap<String,Any>) : Call<User?>?
    /////////////////ROUTES EMPLOYESS/////////////////////
    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("get/employes")
    fun getEmployes(@Body data:MutableMap<String,Any>) : Call<List<User>?>?

    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("add/employee")
    fun addEmployee(@Body client:User) : Call<User?>?

    /////////////////ROUTES CLIENTS/////////////////////
    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("get/clients")
    fun getClients(@Body data:MutableMap<String,Any>) : Call<List<User>?>?

    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("add/client")
    fun addClient(@Body client:User) : Call<User?>?

    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("edit/client")
    fun editClient(@Body client:User) : Call<User?>?

    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("delete/client")
    fun deleteClient(@Body client:User) : Call<User?>?

    /*@Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("users/active")
    fun activeUser(@Body user:MutableMap<String,Any>) : Call<String>?


    ///////////////REPORT//////////////
    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("report/save")
    fun saveReport(@Body map:MutableMap<String,Any>): Call<ModelRating>?

    @Headers(*["Accept: application/json", "Content-Type: application/json"])
    @POST("report/get")
    fun getReport(@Body map:MutableMap<String,Any>): Call<PaginateRating>?*/
}