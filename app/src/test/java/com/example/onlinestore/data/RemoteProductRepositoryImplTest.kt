package com.example.onlinestore.data

import com.example.onlinestore.data.remote.DummyJsonApi
import com.example.onlinestore.data.remote.dto.DummyJsonProductDto
import com.example.onlinestore.data.remote.dto.DummyJsonProductsResponse
import com.example.onlinestore.data.repository.RemoteProductRepositoryImpl
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class RemoteProductRepositoryImplTest {

    @Test
    fun `getProducts maps DummyJSON dto to domain product`() = runTest {
        val api = mockk<DummyJsonApi>()
        coEvery { api.getProducts() } returns DummyJsonProductsResponse(
            products = listOf(
                DummyJsonProductDto(
                    id = 7,
                    title = "Mascara",
                    description = "Black mascara",
                    category = "beauty",
                    price = 9.99,
                    rating = 4.5,
                    stock = 12,
                    thumbnail = "https://img/1.png"
                )
            ),
            total = 1, skip = 0, limit = 30
        )

        val result = RemoteProductRepositoryImpl(api).getProducts()

        assertEquals(1, result.size)
        with(result.first()) {
            assertEquals(7, id)
            assertEquals("Mascara", name)
            assertEquals("Black mascara", description)
            assertEquals(9.99, price, 0.0001)
            assertEquals(12, stockQuantity)
            assertEquals(4.5, rating, 0.0001)
            assertEquals("https://img/1.png", imageUrl)
            assertEquals("beauty", categoryName)
        }
    }
}
