package com.example.onlinestore.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.onlinestore.data.local.dao.CategoryDao
import com.example.onlinestore.data.local.dao.ProductDao
import com.example.onlinestore.data.local.entity.CategoryEntity
import com.example.onlinestore.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ShopDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
}