package com.example.onlinestore.presentation

import app.cash.turbine.test
import com.example.onlinestore.MainDispatcherRule
import com.example.onlinestore.domain.model.Category
import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.domain.repository.CategoryRepository
import com.example.onlinestore.domain.repository.ProductRepository
import com.example.onlinestore.domain.usecase.GetProductsUseCase
import com.example.onlinestore.domain.usecase.GetRemoteProductsUseCase
import com.example.onlinestore.presentation.ui.viewmodel.ProductViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ProductViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getProductsUseCase: GetProductsUseCase
    private lateinit var productRepository: ProductRepository
    private lateinit var categoryRepository: CategoryRepository
    private lateinit var getRemoteProductsUseCase: GetRemoteProductsUseCase

    private val localProducts = listOf(product(1, "Phone"), product(2, "Laptop"))
    private val categories = listOf(Category(1, "Electronics", "Техника"))

    private fun product(id: Int, name: String) = Product(
        id = id,
        name = name,
        description = "desc",
        price = 100.0,
        categoryId = 1,
        imageUrl = "",
        stockQuantity = 3
    )

    @Before
    fun setUp() {
        getProductsUseCase = mockk()
        productRepository = mockk()
        categoryRepository = mockk()
        getRemoteProductsUseCase = mockk()

        // Значения по умолчанию для init { loadProducts(); loadCategories() }
        coEvery { getProductsUseCase() } returns localProducts
        coEvery { categoryRepository.getAllCategories() } returns categories
    }

    private fun createViewModel() = ProductViewModel(
        getProductsUseCase,
        productRepository,
        categoryRepository,
        getRemoteProductsUseCase
    )

    @Test
    fun `init loads products and categories`() = runTest {
        val vm = createViewModel()

        assertEquals(localProducts, vm.products.value)
        assertEquals(categories, vm.categories.value)
        assertNull(vm.saveError.value)
    }

    @Test
    fun `init sets saveError when loading products fails`() = runTest {
        coEvery { getProductsUseCase() } throws RuntimeException("БД недоступна")

        val vm = createViewModel()

        assertTrue(vm.products.value.isEmpty())
        assertEquals("БД недоступна", vm.saveError.value)
    }

    @Test
    fun `getCategoryNameById returns name or null`() = runTest {
        val vm = createViewModel()

        assertEquals("Electronics", vm.getCategoryNameById(1))
        assertNull(vm.getCategoryNameById(999))
    }

    @Test
    fun `loadProductById puts product to selectedProduct and clear resets it`() = runTest {
        coEvery { productRepository.getProductById(2) } returns localProducts[1]
        val vm = createViewModel()

        vm.loadProductById(2)
        assertEquals(localProducts[1], vm.selectedProduct.value)

        vm.clearSelectedProduct()
        assertNull(vm.selectedProduct.value)
    }

    @Test
    fun `loadRemoteProducts success updates remoteProducts and stops loading`() = runTest {
        val remote = listOf(product(10, "Mascara"))
        coEvery { getRemoteProductsUseCase() } returns remote
        val vm = createViewModel()

        vm.loadRemoteProducts()

        assertEquals(remote, vm.remoteProducts.value)
        assertFalse(vm.isRemoteLoading.value)
        assertNull(vm.remoteError.value)
    }

    @Test
    fun `loadRemoteProducts failure sets remoteError and stops loading`() = runTest {
        coEvery { getRemoteProductsUseCase() } throws RuntimeException("Нет сети")
        val vm = createViewModel()

        vm.loadRemoteProducts()

        assertEquals("Нет сети", vm.remoteError.value)
        assertFalse(vm.isRemoteLoading.value)
        assertTrue(vm.remoteProducts.value.isEmpty())
    }

    @Test
    fun `addProduct trims fields saves product reports success and emits saveEvent`() = runTest {
        coEvery { productRepository.addProduct(any()) } returns Unit
        val vm = createViewModel()
        var result: Boolean? = null

        vm.addProduct(
            name = "  Tablet  ",
            description = " cool ",
            price = 250.0,
            categoryId = 1,
            stockQuantity = 4,
            imageUrl = "file:///photo.jpg"
        ) { result = it }

        assertEquals(true, result)
        assertNull(vm.saveError.value)
        coVerify(exactly = 1) {
            productRepository.addProduct(
                match {
                    it.id == 0 &&
                        it.name == "Tablet" &&
                        it.description == "cool" &&
                        it.price == 250.0 &&
                        it.categoryId == 1 &&
                        it.stockQuantity == 4 &&
                        it.imageUrl == "file:///photo.jpg"
                }
            )
        }
        vm.saveEvent.test {
            awaitItem()
        }
    }

    @Test
    fun `addProduct failure sets saveError and reports false`() = runTest {
        coEvery { productRepository.addProduct(any()) } throws RuntimeException("Ошибка записи")
        val vm = createViewModel()
        var result: Boolean? = null

        vm.addProduct("Tablet", "desc", 250.0, 1, 4) { result = it }

        assertEquals(false, result)
        assertEquals("Ошибка записи", vm.saveError.value)
    }

    @Test
    fun `deleteProduct clears selectedProduct and reloads list`() = runTest {
        coEvery { productRepository.getProductById(1) } returns localProducts[0]
        coEvery { productRepository.deleteProduct(any()) } returns Unit
        val vm = createViewModel()
        vm.loadProductById(1)
        var result: Boolean? = null

        vm.deleteProduct(localProducts[0]) { result = it }

        assertEquals(true, result)
        assertNull(vm.selectedProduct.value)
        coVerify(exactly = 1) { productRepository.deleteProduct(localProducts[0]) }
        // init + повторная загрузка после удаления
        coVerify(exactly = 2) { getProductsUseCase() }
    }

    @Test
    fun `clearSaveError resets error`() = runTest {
        coEvery { getProductsUseCase() } throws RuntimeException("boom")
        val vm = createViewModel()
        assertEquals("boom", vm.saveError.value)

        vm.clearSaveError()

        assertNull(vm.saveError.value)
    }
}
