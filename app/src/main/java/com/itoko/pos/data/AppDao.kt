package com.itoko.pos.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // ---- PRODUCTS ----
    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    // ---- CATEGORIES ----
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun getAllCategories(): Flow<List<Category>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    // ---- UNITS ----
    @Query("SELECT * FROM units ORDER BY name ASC")
    fun getAllUnits(): Flow<List<UnitType>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUnit(unit: UnitType)

    @Delete
    suspend fun deleteUnit(unit: UnitType)

    // ---- TRANSACTIONS ----
    @Insert
    suspend fun insertTransaction(transaction: SalesTransaction): Long

    @Insert
    suspend fun insertTransactionItems(items: List<TransactionItem>)

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<SalesTransaction>>

    @Query("SELECT * FROM transaction_items WHERE transactionId = :txnId")
    suspend fun getTransactionItems(txnId: Int): List<TransactionItem>
    
    // Laporan Penjualan (Total berdasarkan rentang waktu hari ini)
    @Query("SELECT sum(totalAmount) FROM transactions WHERE timestamp >= :startOfDay AND timestamp <= :endOfDay")
    fun getDailySalesTotal(startOfDay: Long, endOfDay: Long): Flow<Double?>
}
