package com.example.onlinestore.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinestore.domain.model.Category
import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.domain.repository.CategoryRepository
import com.example.onlinestore.domain.repository.ProductRepository
import com.example.onlinestore.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError = _saveError.asStateFlow()

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.value = getProductsUseCase()
            } catch (exception: Exception) {
                _saveError.value =
                    exception.message ?: "Не удалось загрузить товары"
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepository.getAllCategories()
            } catch (exception: Exception) {
                _saveError.value =
                    exception.message ?: "Не удалось загрузить категории"
            }
        }
    }

    fun addProduct(
        name: String,
        description: String,
        price: Double,
        categoryId: Int,
        stockQuantity: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                productRepository.addProduct(
                    Product(
                        id = 0,
                        name = name.trim(),
                        description = description.trim(),
                        price = price,
                        categoryId = categoryId,
                        imageUrl = "",
                        stockQuantity = stockQuantity
                    )
                )

                _saveError.value = null
                loadProducts()
                onResult(true)
            } catch (exception: Exception) {
                _saveError.value =
                    exception.message ?: "Не удалось сохранить товар"
                onResult(false)
            }
        }
    }

    fun addCategory(
        name: String,
        description: String = ""
    ) {
        viewModelScope.launch {
            try {
                categoryRepository.addCategory(
                    Category(
                        id = 0,
                        name = name.trim(),
                        description = description.trim()
                    )
                )
                loadCategories()
            } catch (exception: Exception) {
                _saveError.value =
                    exception.message ?: "Не удалось сохранить категорию"
            }
        }
    }

    fun clearSaveError() {
        _saveError.value = null
    }
}