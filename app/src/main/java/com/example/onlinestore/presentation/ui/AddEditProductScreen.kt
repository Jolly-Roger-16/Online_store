package com.example.onlinestore.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlinestore.domain.model.Category
import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.presentation.ui.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    product: Product? = null,
    onSaved: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val saveError by viewModel.saveError.collectAsState()

    var name by remember { mutableStateOf(product?.name ?: "") }
    var description by remember { mutableStateOf(product?.description ?: "") }
    var price by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(product?.stockQuantity?.toString() ?: "") }

    var selectedCategory by remember {
        mutableStateOf<Category?>(
            categories.find { it.id == product?.categoryId }
        )
    }
    var expanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val isEdit = product != null

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(if (isEdit) "Редактировать товар" else "Добавить товар")

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Название") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Описание") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Цена") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Количество на складе") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory?.name
                    ?: if (categories.isEmpty()) "Нет категорий" else "Выберите категорию",
                onValueChange = {},
                readOnly = true,
                label = { Text("Категория") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        validationError?.let { Text(it) }
        saveError?.let { Text("Ошибка: $it") }

        Button(
            enabled = categories.isNotEmpty(),
            onClick = {
                val parsedPrice = price.replace(',', '.').toDoubleOrNull()
                val parsedStock = stock.toIntOrNull()
                validationError = when {
                    name.isBlank() -> "Введите название товара"
                    parsedPrice == null || parsedPrice <= 0.0 -> "Введите корректную цену"
                    parsedStock == null || parsedStock < 0 -> "Введите корректное количество"
                    selectedCategory == null -> "Выберите категорию"
                    else -> null
                }
                if (validationError == null) {
                    if (isEdit) {
                        viewModel.updateProduct(
                            id = product!!.id,
                            name = name,
                            description = description,
                            price = parsedPrice!!,
                            categoryId = selectedCategory!!.id,
                            stockQuantity = parsedStock!!
                        ) { success ->
                            if (success) onSaved()
                        }
                    } else {
                        viewModel.addProduct(
                            name = name,
                            description = description,
                            price = parsedPrice!!,
                            categoryId = selectedCategory!!.id,
                            stockQuantity = parsedStock!!
                        ) { success ->
                            if (success) onSaved()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isEdit) "Сохранить изменения" else "Сохранить")
        }
    }
}