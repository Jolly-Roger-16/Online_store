package com.example.onlinestore.domain.repository

import com.example.onlinestore.domain.model.Product

interface RemoteProductRepository {
    suspend fun getProducts(): List<Product>
}