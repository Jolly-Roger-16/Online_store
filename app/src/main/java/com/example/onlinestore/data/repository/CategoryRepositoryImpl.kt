package com.example.onlinestore.data.repository

import com.example.onlinestore.data.local.dao.CategoryDao
import com.example.onlinestore.data.mapper.toDomain
import com.example.onlinestore.domain.model.Category
import com.example.onlinestore.domain.repository.CategoryRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override suspend fun getAllCategories(): List<Category> {
        return categoryDao.getAllCategories().map { it.toDomain() }
    }

    override suspend fun getCategoryById(id: Int): Category? {
        return categoryDao.getCategoryById(id)?.toDomain()
    }
}