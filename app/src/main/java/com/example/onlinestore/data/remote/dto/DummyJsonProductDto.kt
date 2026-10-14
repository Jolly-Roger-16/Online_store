package com.example.onlinestore.data.remote.dto

data class DummyJsonProductDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val rating: Double,
    val stock: Int,
    val thumbnail: String
)

data class DummyJsonProductsResponse(
    val products: List<DummyJsonProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)