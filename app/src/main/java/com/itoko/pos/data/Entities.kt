package com.itoko.pos.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val categoryName: String,
    val unitName: String,
    val purchasePrice: Double, // Harga Modal
    val sellingPrice: Double,  // Harga Jual
    val stock: Int
)

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey val name: String
)

@Entity(tableName = "units")
data class UnitType(
    @PrimaryKey val name: String
)

@Entity(tableName = "transactions")
data class SalesTransaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val timestamp: Long,
    val totalAmount: Double,
    val paymentAmount: Double,
    val changeAmount: Double
)

@Entity(tableName = "transaction_items")
data class TransactionItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val transactionId: Int,
    val productId: Int,
    val productName: String,
    val quantity: Int,
    val priceAtTimeOfSale: Double
)
