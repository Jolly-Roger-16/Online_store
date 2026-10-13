package com.example.onlinestore.domain.usecase

import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.domain.repository.ProductRepository
import javax.inject.Inject

class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(): List<Product> {
        return productRepository.getAllProducts()
    }
}