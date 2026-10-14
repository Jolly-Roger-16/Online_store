package com.example.onlinestore.domain.usecase

import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.domain.repository.RemoteProductRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class GetRemoteProductsUseCaseTest {

    private lateinit var repository: RemoteProductRepository
    private lateinit var useCase: GetRemoteProductsUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetRemoteProductsUseCase(repository)
    }

    private fun product(id: Int, name: String) = Product(
        id = id,
        name = name,
        description = "desc",
        price = 10.0,
        categoryId = 0,
        imageUrl = "",
        stockQuantity = 5
    )

    @Test
    fun `invoke returns products from repository`() = runTest {
        val expected = listOf(product(1, "Phone"), product(2, "Laptop"))
        coEvery { repository.getProducts() } returns expected

        val result = useCase()

        assertEquals(expected, result)
        coVerify(exactly = 1) { repository.getProducts() }
    }

    @Test
    fun `invoke returns empty list when repository is empty`() = runTest {
        coEvery { repository.getProducts() } returns emptyList()

        val result = useCase()

        assertTrue(result.isEmpty())
    }

    @Test
    fun `invoke propagates repository exception`() = runTest {
        coEvery { repository.getProducts() } throws RuntimeException("Нет сети")

        try {
            useCase()
            fail("Ожидалось исключение")
        } catch (e: RuntimeException) {
            assertEquals("Нет сети", e.message)
        }
    }
}
