package com.example.onlinestore.data.repository

import com.example.onlinestore.data.remote.DummyJsonApi
import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.domain.repository.RemoteProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteProductRepositoryImpl @Inject constructor(
    private val api: DummyJsonApi
) : RemoteProductRepository {

    override suspend fun getProducts(): List<Product> {
        return api.getProducts().products.map { dto ->
            Product(
                id = dto.id,
                name = dto.title,
                description = dto.description,
                price = dto.price,
                categoryId = 0,
                imageUrl = dto.thumbnail,
                stockQuantity = dto.stock,
                rating = dto.rating,
                categoryName = dto.category
            )
        }
    }
}