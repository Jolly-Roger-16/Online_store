package com.example.onlinestore.data.di

import android.content.Context
import androidx.room.Room
import com.example.onlinestore.data.local.dao.ProductDao
import com.example.onlinestore.data.local.ShopDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.onlinestore.data.local.dao.CategoryDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideShopDatabase(
        @ApplicationContext context: Context
    ): ShopDatabase {
        return Room.databaseBuilder(
            context,
            ShopDatabase::class.java,
            "online_store_database"
        ).build()
    }

    @Provides
    fun provideProductDao(database: ShopDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    fun provideCategoryDao(database: ShopDatabase): CategoryDao {
        return database.categoryDao()
    }
}