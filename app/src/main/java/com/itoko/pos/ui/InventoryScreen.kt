package com.itoko.pos.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.itoko.pos.viewmodel.AppViewModel
import com.itoko.pos.data.Product

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(navController: NavController, viewModel: AppViewModel) {
    val products by viewModel.products.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Input Barang (Inventaris)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Barang")
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            items(products) { product ->
                ListItem(
                    headlineContent = { Text(product.name) },
                    supportingContent = { Text("Kategori: ${product.categoryName} | Modal: Rp ${product.purchasePrice} | Jual: Rp ${product.sellingPrice}") },
                    trailingContent = { Text("${product.stock} ${product.unitName}") }
                )
            }
        }
    }

    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var category by remember { mutableStateOf("") }
        var unit by remember { mutableStateOf("") }
        var purchasePrice by remember { mutableStateOf("") }
        var sellingPrice by remember { mutableStateOf("") }
        var stock by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Masukkan Detail Barang") },
            text = {
                Column {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nama Barang (misal: Beras Rojolele)") })
                    OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Kategori (misal: Sembako/Pakan)") })
                    OutlinedTextField(value = unit, onValueChange = { unit = it }, label = { Text("Satuan (misal: Karung/Ikat/Dus)") })
                    OutlinedTextField(value = purchasePrice, onValueChange = { purchasePrice = it }, label = { Text("Harga Modal (Rp)") })
                    OutlinedTextField(value = sellingPrice, onValueChange = { sellingPrice = it }, label = { Text("Harga Jual (Rp)") })
                    OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Jumlah Stok Saat Ini") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val pPrice = purchasePrice.toDoubleOrNull() ?: 0.0
                    val sPrice = sellingPrice.toDoubleOrNull() ?: 0.0
                    val st = stock.toIntOrNull() ?: 0
                    if (name.isNotBlank()) {
                        viewModel.saveProduct(Product(
                            name = name,
                            categoryName = category,
                            unitName = unit,
                            purchasePrice = pPrice,
                            sellingPrice = sPrice,
                            stock = st
                        ), isNewCategory = true, isNewUnit = true)
                        showAddDialog = false
                    }
                }) {
                    Text("Simpan Barang")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Batal") }
            }
        )
    }
}
