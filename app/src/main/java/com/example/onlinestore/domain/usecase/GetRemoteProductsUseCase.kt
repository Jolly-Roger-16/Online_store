package com.example.onlinestore.domain.usecase

import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.domain.repository.RemoteProductRepository
import javax.inject.Inject

class GetRemoteProductsUseCase @Inject constructor(
    private val repository: RemoteProductRepository
) {
    suspend operator fun invoke(): List<Product> {
        return repository.getProducts()
    }
}