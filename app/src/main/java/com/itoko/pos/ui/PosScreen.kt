package com.itoko.pos.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
fun PosScreen(navController: NavController, viewModel: AppViewModel) {
    val products by viewModel.products.collectAsState()
    val cart by viewModel.cart.collectAsState()
    var showCheckout by remember { mutableStateOf(false) }
    
    val totalAmount = cart.entries.sumOf { it.key.sellingPrice * it.value }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kasir Toko (POS)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                BottomAppBar {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total: Rp $totalAmount", style = MaterialTheme.typography.titleMedium)
                        Button(onClick = { showCheckout = true }) {
                            Text("Bayar")
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(contentPadding = padding, modifier = Modifier.fillMaxSize()) {
            items(products) { product ->
                ListItem(
                    headlineContent = { Text(product.name) },
                    supportingContent = { Text("Stok: ${product.stock} ${product.unitName} | Harga: Rp ${product.sellingPrice}") },
                    trailingContent = {
                        val qty = cart[product] ?: 0
                        if (qty > 0) {
                            Text("x$qty di Keranjang", color = MaterialTheme.colorScheme.primary)
                        } else null
                    },
                    modifier = Modifier.clickable { viewModel.addToCart(product) }
                )
            }
        }
    }
    
    if (showCheckout) {
        var paymentStr by remember { mutableStateOf("") }
        var resultMessage by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { 
                if (resultMessage.contains("Berhasil")) showCheckout = false 
            },
            title = { Text("Pembayaran") },
            text = {
                Column {
                    Text("Total Belanja: Rp $totalAmount")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = paymentStr,
                        onValueChange = { paymentStr = it },
                        label = { Text("Uang Dibayar (Rp)") }
                    )
                    Spacer(Modifier.height(8.dp))
                    if (resultMessage.isNotEmpty()) {
                        Text(resultMessage, color = MaterialTheme.colorScheme.primary)
                    }
                }
            },
            confirmButton = {
                if (!resultMessage.contains("Berhasil")) {
                    Button(onClick = {
                        val payment = paymentStr.toDoubleOrNull() ?: 0.0
                        if (payment >= totalAmount) {
                            val change = payment - totalAmount
                            viewModel.checkout(payment)
                            resultMessage = "Berhasil! Kembalian: Rp $change"
                        } else {
                            resultMessage = "Uang tidak cukup!"
                        }
                    }) {
                        Text("Konfirmasi Penjualan")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showCheckout = false }) { Text("Tutup") }
            }
        )
    }
}
