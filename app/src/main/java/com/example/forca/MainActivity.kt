package com.example.forca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forca.ui.screens.TelaAdmin
import com.example.forca.ui.screens.TelaAdminRanking
import com.example.forca.ui.screens.TelaHistorico
import com.example.forca.ui.theme.ForcaTheme

// TEMPORÁRIO, só para testar. Antes de commitar: git restore neste arquivo.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForcaTheme {
                var tela by rememberSaveable { mutableStateOf("palavras") }

                Box(Modifier.fillMaxSize()) {
                    when (tela) {
                        "ranking" -> TelaAdminRanking(onVoltar = { tela = "palavras" })
                        "historico" -> TelaHistorico(
                            modifier = Modifier.statusBarsPadding().padding(bottom = 56.dp)
                        )
                        else -> TelaAdmin(
                            onVoltar = { finish() },
                            onAbrirRanking = { tela = "ranking" }
                        )
                    }
                    TextButton(
                        onClick = { tela = if (tela == "historico") "ranking" else "historico" },
                        modifier = Modifier.align(Alignment.BottomStart).navigationBarsPadding()
                    ) {
                        Text(if (tela == "historico") "← Voltar" else "Histórico (teste)")
                    }
                }
            }
        }
    }
}