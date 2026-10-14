package com.example.onlinestore.data.remote

import com.example.onlinestore.data.remote.dto.DummyJsonProductsResponse
import retrofit2.http.GET

interface DummyJsonApi {

    @GET("products")
    suspend fun getProducts(): DummyJsonProductsResponse
}