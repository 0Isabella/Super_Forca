package com.example.forca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.forca.navigation.AppNavigation
import com.example.forca.navigation.Rotas
import com.example.forca.ui.theme.ForcaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ForcaTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = { BarraInferior(navController) }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun BarraInferior(navController: NavHostController) {
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    fun irPara(rota: String) {
        navController.navigate(rota) {
            popUpTo(Rotas.inicio)
            launchSingleTop = true
        }
    }

    NavigationBar {
        NavigationBarItem(
            selected = rotaAtual == Rotas.inicio,
            onClick = { irPara(Rotas.inicio) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
            label = { Text("Início") }
        )
        NavigationBarItem(
            selected = rotaAtual == Rotas.historico,
            onClick = { irPara(Rotas.historico) },
            icon = { Icon(Icons.Default.Menu, contentDescription = "Histórico") },
            label = { Text("Histórico") }
        )
        NavigationBarItem(
            selected = rotaAtual == Rotas.admin || rotaAtual == Rotas.adminRanking,
            onClick = { irPara(Rotas.admin) },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Admin") },
            label = { Text("Admin") }
        )
    }
}