package com.example.onlinestore.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.presentation.ui.ProductDetailScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProductDetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val product = Product(
        id = 1,
        name = "Смартфон X",
        description = "Мощный смартфон",
        price = 499.0,
        categoryId = 1,
        imageUrl = "",
        stockQuantity = 8,
        rating = 4.5
    )

    private fun setScreen(
        item: Product = product,
        categoryName: String? = "Электроника",
        onEdit: () -> Unit = {},
        onDelete: () -> Unit = {}
    ) {
        composeRule.setContent {
            ProductDetailScreen(
                product = item,
                categoryName = categoryName,
                onEdit = onEdit,
                onDelete = onDelete
            )
        }
    }

    @Test
    fun showsProductInfo() {
        setScreen()

        composeRule.onNodeWithText("Смартфон X").assertIsDisplayed()
        composeRule.onNodeWithText("Мощный смартфон").assertIsDisplayed()
        composeRule.onNodeWithText("Цена: 499.0 ₽").assertIsDisplayed()
        composeRule.onNodeWithText("Количество на складе: 8").assertIsDisplayed()
        composeRule.onNodeWithText("Категория: Электроника").assertIsDisplayed()
    }

    @Test
    fun showsPlaceholders_whenDescriptionBlank_andCategoryNull() {
        setScreen(item = product.copy(description = ""), categoryName = null)

        composeRule.onNodeWithText("Описание отсутствует").assertIsDisplayed()
        composeRule.onNodeWithText("Категория: Не указана").assertIsDisplayed()
    }

    @Test
    fun editButton_invokesOnEdit() {
        var edited = false
        setScreen(onEdit = { edited = true })

        composeRule.onNodeWithTag("edit_button").performClick()

        assertEquals(true, edited)
    }

    @Test
    fun deleteButton_showsConfirmDialog_andConfirmInvokesOnDelete() {
        var deleted = false
        setScreen(onDelete = { deleted = true })

        composeRule.onNodeWithTag("delete_button").performClick()
        composeRule.onNodeWithText("Удалить товар?").assertIsDisplayed()
        assertEquals(false, deleted)

        composeRule.onNodeWithTag("confirm_delete_button").performClick()

        assertEquals(true, deleted)
    }

    @Test
    fun deleteDialog_cancel_doesNotInvokeOnDelete() {
        var deleted = false
        setScreen(onDelete = { deleted = true })

        composeRule.onNodeWithTag("delete_button").performClick()
        composeRule.onNodeWithText("Отмена").performClick()

        assertEquals(false, deleted)
        composeRule.onNodeWithText("Удалить товар?").assertDoesNotExist()
    }
}
