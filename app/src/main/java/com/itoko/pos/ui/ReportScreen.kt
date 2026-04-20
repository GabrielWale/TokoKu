package com.itoko.pos.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.itoko.pos.viewmodel.AppViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportScreen(navController: NavController, viewModel: AppViewModel) {
    val transactions by viewModel.transactions.collectAsState()
    
    val formatter = SimpleDateFormat("dd MMM yy HH:mm", Locale.getDefault())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Laporan & Grafik Penjualan") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Grafik Transaksi Terakhir", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(16.dp))
            
            val amounts = transactions.take(7).map { it.totalAmount }
            if (amounts.isNotEmpty()) {
                val maxAmount = amounts.maxOrNull() ?: 1.0
                Card(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    Canvas(modifier = Modifier.fillMaxWidth().height(150.dp).padding(16.dp)) {
                        val barWidth = size.width / (amounts.size * 2)
                        amounts.forEachIndexed { index, amount ->
                            val barHeight = (amount / maxAmount).toFloat() * size.height
                            drawRect(
                                color = Color.Blue,
                                topLeft = Offset(
                                    x = index * (barWidth * 2) + barWidth / 2,
                                    y = size.height - barHeight
                                ),
                                size = Size(barWidth, barHeight)
                            )
                        }
                    }
                }
            } else {
                Text("Belum ada data grafik yang dapat ditampilkan.")
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Text("Riwayat Transaksi (Terbaru di atas)", style = MaterialTheme.typography.titleMedium)
            
            LazyColumn {
                items(transactions) { txn ->
                    ListItem(
                        headlineContent = { Text("Pemasukan: Rp ${txn.totalAmount}") },
                        supportingContent = { Text(formatter.format(Date(txn.timestamp))) }
                    )
                    Divider()
                }
            }
        }
    }
}
