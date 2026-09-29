package com.example.onlinestore.domain.model

data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: Int,
    val imageUrl: String,
    val stockQuantity: Int,
    val rating: Double = 0.0,
    val createdAt: Long = System.currentTimeMillis()
)