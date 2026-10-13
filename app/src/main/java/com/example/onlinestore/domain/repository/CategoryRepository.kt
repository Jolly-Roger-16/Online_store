package com.example.onlinestore.domain.repository

import com.example.onlinestore.domain.model.Category

interface CategoryRepository
{
    suspend fun addCategory(category: Category): Long
    suspend fun getAllCategories(): List<Category>
    suspend fun getCategoryById(id: Int): Category?

}