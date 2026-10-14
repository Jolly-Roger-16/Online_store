package com.example.onlinestore.data.di

import com.example.onlinestore.data.repository.CategoryRepositoryImpl
import com.example.onlinestore.data.repository.ProductRepositoryImpl
import com.example.onlinestore.domain.repository.CategoryRepository
import com.example.onlinestore.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.example.onlinestore.data.repository.RemoteProductRepositoryImpl
import com.example.onlinestore.domain.repository.RemoteProductRepository

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        implementation: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        implementation: CategoryRepositoryImpl
    ): CategoryRepository

    @Binds
    @Singleton
    abstract fun bindRemoteProductRepository(
        implementation: RemoteProductRepositoryImpl
    ): RemoteProductRepository
}