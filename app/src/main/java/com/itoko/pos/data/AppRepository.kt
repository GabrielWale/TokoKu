package com.itoko.pos.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    val allProducts: Flow<List<Product>> = appDao.getAllProducts()
    val allCategories: Flow<List<Category>> = appDao.getAllCategories()
    val allUnits: Flow<List<UnitType>> = appDao.getAllUnits()
    val allTransactions: Flow<List<SalesTransaction>> = appDao.getAllTransactions()

    suspend fun insertProduct(product: Product) = appDao.insertProduct(product)
    suspend fun updateProduct(product: Product) = appDao.updateProduct(product)
    suspend fun deleteProduct(product: Product) = appDao.deleteProduct(product)

    suspend fun insertCategory(category: Category) {
        val trimmed = category.name.trim()
        if(trimmed.isNotEmpty()) appDao.insertCategory(Category(trimmed))
    }
    
    suspend fun insertUnit(unit: UnitType) {
        val trimmed = unit.name.trim()
        if(trimmed.isNotEmpty()) appDao.insertUnit(UnitType(trimmed))
    }

    suspend fun insertTransactionWithItems(
        transaction: SalesTransaction,
        items: List<TransactionItem>
    ) {
        val txnId = appDao.insertTransaction(transaction).toInt()
        val itemsWithTxnId = items.map { it.copy(transactionId = txnId) }
        appDao.insertTransactionItems(itemsWithTxnId)
    }
    
    fun getDailySalesTotal(startOfDay: Long, endOfDay: Long): Flow<Double?> {
        return appDao.getDailySalesTotal(startOfDay, endOfDay)
    }
}
