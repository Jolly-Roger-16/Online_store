package com.example.onlinestore.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.onlinestore.domain.model.Product
import com.example.onlinestore.presentation.ui.viewmodel.ProductViewModel
import coil3.compose.AsyncImage
import androidx.compose.material3.CircularProgressIndicator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel = hiltViewModel()
) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val remoteProducts by viewModel.remoteProducts.collectAsStateWithLifecycle()
    val isRemoteLoading by viewModel.isRemoteLoading.collectAsStateWithLifecycle()
    val remoteError by viewModel.remoteError.collectAsStateWithLifecycle()

    var showRemoteProducts by rememberSaveable {
        mutableStateOf(false)
    }
    val selectedProduct by viewModel.selectedProduct.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Обработка события "Данные сохранены" через Channel (показывается один раз)
    LaunchedEffect(viewModel.saveEvent) {
        viewModel.saveEvent.collect {
            snackbarHostState.showSnackbar("Данные сохранены")
        }
    }

    var showAddProductForm by rememberSaveable { mutableStateOf(false) }
    var showAddCategoryDialog by rememberSaveable { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    // Диалог создания категории
    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onSave = { name, description ->
                viewModel.addCategory(name = name, description = description)
                showAddCategoryDialog = false
            }
        )
    }

    when {
        // 1. Режим просмотра деталей товара
        selectedProduct != null -> {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text("Товар") },
                        navigationIcon = {
                            TextButton(onClick = { viewModel.clearSelectedProduct() }) {
                                Text("Назад")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    ProductDetailScreen(
                        product = selectedProduct!!,
                        categoryName = viewModel.getCategoryNameById(selectedProduct!!.categoryId),
                        onEdit = {
                            editingProduct = selectedProduct
                            viewModel.clearSelectedProduct()
                        },
                        onDelete = {
                            viewModel.deleteProduct(selectedProduct!!) { success ->
                                if (success) {
                                    // selectedProduct уже обнуляется внутри ViewModel
                                }
                            }
                        }
                    )
                }
            }
        }

        // 2. Режим редактирования товара
        editingProduct != null -> {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text("Редактировать товар") },
                        navigationIcon = {
                            TextButton(onClick = { editingProduct = null }) {
                                Text("Назад")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    AddEditProductScreen(
                        product = editingProduct,
                        onSaved = {
                            editingProduct = null
                        },
                        viewModel = viewModel
                    )
                }
            }
        }

        // 3. Режим добавления нового товара
        showAddProductForm -> {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text("Новый товар") },
                        navigationIcon = {
                            TextButton(onClick = { showAddProductForm = false }) {
                                Text("Назад")
                            }
                        }
                    )
                }
            ) { padding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    AddEditProductScreen(
                        onSaved = { showAddProductForm = false },
                        viewModel = viewModel
                    )
                }
            }
        }

        // 4. Главный экран — список товаров
        else -> {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = { Text("Онлайн-магазин") },
                        actions = {
                            TextButton(onClick = { showAddCategoryDialog = true }) {
                                Text("Категории")
                            }
                        }
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(onClick = { showAddProductForm = true }) {
                        Text("+")
                    }
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = !showRemoteProducts,
                            onClick = { showRemoteProducts = false },
                            label = { Text("Мои товары") }
                        )
                        FilterChip(
                            selected = showRemoteProducts,
                            onClick = {
                                showRemoteProducts = true
                                viewModel.loadRemoteProducts()
                            },
                            label = { Text("DummyJSON") }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        val displayedProducts =
                            if (showRemoteProducts) remoteProducts else products

                        when {
                            showRemoteProducts && isRemoteLoading -> {
                                CircularProgressIndicator()
                            }

                            showRemoteProducts && remoteError != null -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Ошибка: $remoteError")
                                    TextButton(onClick = { viewModel.loadRemoteProducts() }) {
                                        Text("Повторить")
                                    }
                                }
                            }

                            displayedProducts.isEmpty() -> {
                                Text(
                                    if (showRemoteProducts) {
                                        "В API пока нет товаров"
                                    } else {
                                        "Товаров пока нет"
                                    }
                                )
                            }

                            else -> {
                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    contentPadding = PaddingValues(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(displayedProducts, key = { it.id }) { product ->
                                        Card(
                                            onClick = {
                                                if (!showRemoteProducts) {
                                                    viewModel.loadProductById(product.id)
                                                }
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                if (product.imageUrl.isNotBlank()) {
                                                    AsyncImage(
                                                        model = product.imageUrl,
                                                        contentDescription = product.name,
                                                        contentScale = ContentScale.Crop,
                                                        modifier = Modifier
                                                            .size(72.dp)
                                                            .clip(RoundedCornerShape(8.dp))
                                                    )
                                                    Spacer(modifier = Modifier.width(12.dp))
                                                }

                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = product.name,
                                                        style = MaterialTheme.typography.titleMedium,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                    )

                                                    // DummyJSON отдаёт цены в долларах, локальные товары храним в рублях
                                                    Text(
                                                        text = if (showRemoteProducts) "${product.price} $" else "${product.price} ₽",
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )

                                                    if (showRemoteProducts) {
                                                        Text(
                                                            text = "${product.categoryName ?: "Без категории"} · ★ ${product.rating} · в наличии: ${product.stockQuantity}",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            maxLines = 1,
                                                            overflow = TextOverflow.Ellipsis
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddCategoryDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, description: String) -> Unit
) {
    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая категория") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; error = null },
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
                error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = "Введите название категории"
                    } else {
                        onSave(name.trim(), description.trim())
                    }
                }
            ) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}