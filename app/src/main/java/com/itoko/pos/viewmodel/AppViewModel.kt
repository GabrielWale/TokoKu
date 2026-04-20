package com.itoko.pos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.itoko.pos.data.*
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val repository: AppRepository) : ViewModel() {

    // Database Flows
    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<Category>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val units: StateFlow<List<UnitType>> = repository.allUnits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val transactions: StateFlow<List<SalesTransaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // POS Cart State: Product -> Quantity
    private val _cart = MutableStateFlow<Map<Product, Int>>(emptyMap())
    val cart: StateFlow<Map<Product, Int>> = _cart
    
    fun addToCart(product: Product) {
        val current = _cart.value.toMutableMap()
        val currentQty = current[product] ?: 0
        if (currentQty < product.stock) {
            current[product] = currentQty + 1
            _cart.value = current
        }
    }
    
    fun removeFromCart(product: Product) {
        val current = _cart.value.toMutableMap()
        current.remove(product)
        _cart.value = current
    }

    fun clearCart() {
        _cart.value = emptyMap()
    }

    fun checkout(paymentAmount: Double) {
        val currentCart = _cart.value
        if (currentCart.isEmpty()) return

        val totalAmount = currentCart.entries.sumOf { it.key.sellingPrice * it.value }
        val changeAmount = paymentAmount - totalAmount
        
        viewModelScope.launch {
            val transaction = SalesTransaction(
                timestamp = System.currentTimeMillis(),
                totalAmount = totalAmount,
                paymentAmount = paymentAmount,
                changeAmount = changeAmount
            )
            
            val items = currentCart.map { (product, qty) ->
                TransactionItem(
                    transactionId = 0,
                    productId = product.id,
                    productName = product.name,
                    quantity = qty,
                    priceAtTimeOfSale = product.sellingPrice
                )
            }
            
            repository.insertTransactionWithItems(transaction, items)
            
            // Kurangi stok barang secara otomatis setelah terjual
            currentCart.forEach { (product, qty) ->
                val newStock = product.stock - qty
                repository.updateProduct(product.copy(stock = newStock))
            }
            
            clearCart() // Kosongkan keranjang untuk pelanggan selanjutnya
        }
    }

    // Manajemen Produk (Inventory)
    fun saveProduct(product: Product, isNewCategory: Boolean = false, isNewUnit: Boolean = false) {
        viewModelScope.launch {
            repository.insertProduct(product)
            if (isNewCategory) repository.insertCategory(Category(product.categoryName))
            if (isNewUnit) repository.insertUnit(UnitType(product.unitName))
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { repository.deleteProduct(product) }
    }
}

class AppViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
