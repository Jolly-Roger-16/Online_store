package com.example.onlinestore.data.repository

import com.example.onlinestore.data.local.dao.ProductDao
import com.example.onlinestore.data.mapper.toDomain
import com.example.onlinestore.data.mapper.toEntity
import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.domain.repository.ProductRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao
) : ProductRepository {

    override suspend fun getAllProducts(): List<Product> {
        return productDao.getAllProducts().map { it.toDomain() }
    }

    override suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)?.toDomain()
    }

    override suspend fun getProductsByCategory(categoryId: Int): List<Product> {
        return productDao.getProductsByCategory(categoryId).map { it.toDomain() }
    }

    override suspend fun addProduct(product: Product) {
        productDao.insert(product.toEntity())
    }

    override suspend fun updateProduct(product: Product) {
        productDao.update(product.toEntity())
    }

    override suspend fun deleteProduct(product: Product) {
        productDao.delete(product.toEntity())
    }
}