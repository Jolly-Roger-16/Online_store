package com.example.onlinestore.data.mapper

import com.example.onlinestore.data.local.entity.CategoryEntity
import com.example.onlinestore.data.local.entity.ProductEntity
import com.example.onlinestore.domain.model.Category
import com.example.onlinestore.domain.model.Product

fun ProductEntity.toDomain() = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    categoryId = categoryId,
    imageUrl = imageUrl,
    stockQuantity = stockQuantity,
    rating = rating,
    createdAt = createdAt
)

fun Product.toEntity() = ProductEntity(
    id = id,
    name = name,
    description = description,
    price = price,
    categoryId = categoryId,
    imageUrl = imageUrl,
    stockQuantity = stockQuantity,
    rating = rating,
    createdAt = createdAt
)

fun CategoryEntity.toDomain() = Category(
    id = id,
    name = name,
    description = description
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    name = name,
    description = description
)