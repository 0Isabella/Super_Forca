package com.example.forca

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.forca.ui.screens.TelaJogo
import com.example.forca.ui.theme.ForcaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForcaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // TEMPORÁRIO (branch do jogo): abre direto na TelaJogo para testar.
                    // Quando a navegação ficar pronta, essa chamada vai para o NavHost.
                    TelaJogo(
                        modifier = Modifier.padding(innerPadding),
                        onFimDeJogo = { iniciais, pontos, tema ->
                            // Teste: só mostra o que seria salvo no ranking/histórico.
                            Toast.makeText(
                                this@MainActivity,
                                "Salvar: $iniciais | $pontos pts | $tema",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    )
                }
            }
        }
    }
}