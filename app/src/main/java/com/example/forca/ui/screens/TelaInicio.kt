package com.example.forca.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.forca.ui.theme.cinzaClaro
import com.example.forca.ui.theme.cinzaEsverdeado
import com.example.forca.ui.theme.textoClaro
import com.example.forca.ui.theme.vermelhoClaro
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.ui.text.style.TextAlign

@Composable
fun TelaInicio(
    onJogar: () -> Unit,
    onAdmin: () -> Unit,
    onHistorico: () -> Unit,
    modifier: Modifier = Modifier
) {
    var mostrarTutorial by remember { mutableStateOf(false) }

    val transicao = rememberInfiniteTransition(label = "brilho")
    val brilho by transicao.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "brilho"
    )

    Column(modifier = modifier.fillMaxSize().padding(24.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onAdmin) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Admin",
                    tint = cinzaClaro,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Admin", color = cinzaClaro)
            }
        }

        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ForcaAnimada(modifier = Modifier.size(200.dp))
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SUPER FORCA",
                style = MaterialTheme.typography.displaySmall,
                color = textoClaro,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
            Button(
                onClick = onJogar,
                shape = RoundedCornerShape(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = vermelhoClaro,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .drawBehind {
                        val raio = 40.dp.toPx()
                        val camadas = 6
                        for (i in camadas downTo 1) {
                            val expansao = i * 3.dp.toPx()
                            val alfa = brilho * 0.35f * (1f - i / (camadas + 1f))
                            drawRoundRect(
                                color = vermelhoClaro.copy(alpha = alfa),
                                topLeft = Offset(-expansao, -expansao + 6.dp.toPx()),
                                size = Size(size.width + expansao * 2, size.height + expansao * 2),
                                cornerRadius = CornerRadius(raio + expansao)
                            )
                        }
                    }
            ) {
                Text("JOGAR", style = MaterialTheme.typography.titleLarge)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onHistorico,
                    border = BorderStroke(1.dp, cinzaEsverdeado),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textoClaro),
                    modifier = Modifier.weight(1f)
                ) { Text("Histórico") }

                OutlinedButton(
                    onClick = { mostrarTutorial = true },
                    border = BorderStroke(1.dp, cinzaEsverdeado),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textoClaro),
                    modifier = Modifier.weight(1f)
                ) { Text("Como jogar") }
            }
        }
    }

    if (mostrarTutorial) {
        TelaTutorial(onFechar = { mostrarTutorial = false })
    }
}

@Composable
private fun ForcaAnimada(modifier: Modifier = Modifier) {
    val transicao = rememberInfiniteTransition(label = "forca")
    val passo by transicao.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing)
        ),
        label = "passo"
    )

    Canvas(modifier = modifier) {
        val partes = passo.toInt()
        val w = size.width
        val h = size.height
        val traco = 4.dp.toPx()

        val estrutura = listOf(
            Offset(0.15f * w, 0.95f * h) to Offset(0.65f * w, 0.95f * h),
            Offset(0.30f * w, 0.95f * h) to Offset(0.30f * w, 0.05f * h),
            Offset(0.30f * w, 0.05f * h) to Offset(0.70f * w, 0.05f * h),
            Offset(0.70f * w, 0.05f * h) to Offset(0.70f * w, 0.18f * h)
        )
        estrutura.forEach { (ini, fim) ->
            drawLine(cinzaClaro, ini, fim, strokeWidth = traco, cap = StrokeCap.Round)
        }

        if (partes >= 1) {
            drawCircle(
                color = textoClaro,
                radius = 0.07f * h,
                center = Offset(0.70f * w, 0.25f * h),
                style = Stroke(width = traco, cap = StrokeCap.Round)
            )
        }
        val corpo = listOf(
            2 to (Offset(0.70f * w, 0.32f * h) to Offset(0.70f * w, 0.60f * h)),
            3 to (Offset(0.70f * w, 0.38f * h) to Offset(0.60f * w, 0.50f * h)),
            4 to (Offset(0.70f * w, 0.38f * h) to Offset(0.80f * w, 0.50f * h)),
            5 to (Offset(0.70f * w, 0.60f * h) to Offset(0.62f * w, 0.80f * h)),
            6 to (Offset(0.70f * w, 0.60f * h) to Offset(0.78f * w, 0.80f * h))
        )
        corpo.forEach { (minimo, linha) ->
            if (partes >= minimo) {
                drawLine(textoClaro, linha.first, linha.second, strokeWidth = traco, cap = StrokeCap.Round)
            }
        }
    }
}