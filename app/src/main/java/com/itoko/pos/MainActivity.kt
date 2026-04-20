package com.itoko.pos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.itoko.pos.data.AppDatabase
import com.itoko.pos.data.AppRepository
import com.itoko.pos.ui.*
import com.itoko.pos.viewmodel.AppViewModel
import com.itoko.pos.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Inisialisasi Database (Offline Room DB)
        val database = AppDatabase.getDatabase(this)
        val repository = AppRepository(database.appDao())
        
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory(repository))
                    
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { HomeScreen(navController, appViewModel) }
                        composable("pos") { PosScreen(navController, appViewModel) }
                        composable("inventory") { InventoryScreen(navController, appViewModel) }
                        composable("report") { ReportScreen(navController, appViewModel) }
                    }
                }
            }
        }
    }
}
