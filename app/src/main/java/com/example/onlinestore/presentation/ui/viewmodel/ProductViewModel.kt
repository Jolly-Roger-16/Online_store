package com.example.onlinestore.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlinestore.domain.model.Category
import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.domain.repository.CategoryRepository
import com.example.onlinestore.domain.repository.ProductRepository
import com.example.onlinestore.domain.usecase.GetProductsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val productRepository: ProductRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    // Channel для Snackbar "Данные сохранены" один раз
    private val _saveEvent = Channel<Unit>(Channel.CONFLATED)
    val saveEvent = _saveEvent.receiveAsFlow()

    init {
        loadProducts()
        loadCategories()
    }

    fun loadProducts() {
        viewModelScope.launch {
            try {
                _products.value = getProductsUseCase()
            } catch (exception: Exception) {
                _saveError.value = exception.message ?: "Не удалось загрузить товары"
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepository.getAllCategories()
            } catch (exception: Exception) {
                _saveError.value = exception.message ?: "Не удалось загрузить категории"
            }
        }
    }

    fun loadProductById(id: Int) {
        viewModelScope.launch {
            try {
                _selectedProduct.value = productRepository.getProductById(id)
            } catch (exception: Exception) {
                _saveError.value = exception.message ?: "Не удалось загрузить товар"
            }
        }
    }

    fun clearSelectedProduct() {
        _selectedProduct.value = null
    }

    fun getCategoryNameById(categoryId: Int): String? {
        return _categories.value.find { it.id == categoryId }?.name
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
                _saveEvent.send(Unit) // Snackbar "Данные сохранены"
                onResult(true)
            } catch (exception: Exception) {
                _saveError.value = exception.message ?: "Не удалось сохранить товар"
                onResult(false)
            }
        }
    }

    fun updateProduct(
        id: Int,
        name: String,
        description: String,
        price: Double,
        categoryId: Int,
        stockQuantity: Int,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                productRepository.updateProduct(
                    Product(
                        id = id,
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
                loadProductById(id)
                _saveEvent.send(Unit)
                onResult(true)
            } catch (exception: Exception) {
                _saveError.value = exception.message ?: "Не удалось обновить товар"
                onResult(false)
            }
        }
    }

    fun deleteProduct(product: Product, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                productRepository.deleteProduct(product)
                _saveError.value = null
                _selectedProduct.value = null
                loadProducts()
                _saveEvent.send(Unit) // Snackbar "Данные сохранены"
                onResult(true)
            } catch (exception: Exception) {
                _saveError.value = exception.message ?: "Не удалось удалить товар"
                onResult(false)
            }
        }
    }

    fun addCategory(name: String, description: String = "") {
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
                _saveEvent.send(Unit) // Snackbar "Данные сохранены"
            } catch (exception: Exception) {
                _saveError.value = exception.message ?: "Не удалось сохранить категорию"
            }
        }
    }

    fun clearSaveError() {
        _saveError.value = null
    }
}