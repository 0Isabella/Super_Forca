package com.example.forca

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.forca.ui.screens.TelaAdmin
import com.example.forca.ui.screens.TelaAdminRanking
import com.example.forca.ui.screens.TelaHistorico
import com.example.forca.ui.screens.TelaInicio
import com.example.forca.ui.screens.TelaJogo
import com.example.forca.ui.theme.ForcaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ForcaTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0)
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {

                        TelaInicio(
                            onJogar = {},
                            onAdmin = {},
                            onJogador = {},
                            onRanking = {},
                            onHistorico = {},
                            modifier = Modifier.systemBarsPadding()
                        )

                        /*TelaJogo(
                            modifier = Modifier.systemBarsPadding(),
                            onFimDeJogo = { iniciais, pontos, tema ->
                                Toast.makeText(
                                    this@MainActivity,
                                    "Salvar: $iniciais | $pontos pts | $tema",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        )*/

                        //TelaHistorico(modifier = Modifier.systemBarsPadding())

                        //TelaAdmin(onVoltar = { finish() }, onAbrirRanking = {})

                        //TelaAdminRanking(onVoltar = { finish() })
                    }
                }
            }
        }
    }
}