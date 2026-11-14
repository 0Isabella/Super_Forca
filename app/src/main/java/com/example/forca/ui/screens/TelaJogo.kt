package com.example.forca.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forca.viewmodel.Fase
import com.example.forca.viewmodel.JogoUiState
import com.example.forca.viewmodel.JogoViewModel
import com.example.forca.viewmodel.MAX_TENTATIVAS
import com.example.forca.viewmodel.MotivoFim
import com.example.forca.viewmodel.TEMPO_PADRAO_SEGUNDOS
import kotlin.math.min

private val VerdeAcerto = Color(0xFF2E7D32)
private val LINHAS_TECLADO = listOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM")

/**
 * Tela do Jogo da Forca.
 *
 * @param temaInicial   se vier preenchido (ex.: da tela de início), pula a escolha de tema.
 * @param tempoMaxSegundos tempo total da partida (a tela Admin poderá alterar isso).
 * @param onFimDeJogo   chamado quando o jogador confirma as iniciais (até 4 letras)
 *                      no popup final. A pessoa que fez Ranking/Histórico salva aqui.
 */
@Composable
fun TelaJogo(
    modifier: Modifier = Modifier,
    temaInicial: String? = null,
    tempoMaxSegundos: Int = TEMPO_PADRAO_SEGUNDOS,
    onFimDeJogo: (iniciais: String, pontos: Int, tema: String) -> Unit = { _, _, _ -> },
    viewModel: JogoViewModel = viewModel()
) {
    val estado by viewModel.uiState.collectAsState()

    LaunchedEffect(temaInicial) {
        if (temaInicial != null && estado.fase == Fase.ESCOLHENDO_TEMA) {
            viewModel.iniciarJogo(temaInicial, tempoMaxSegundos)
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (estado.fase == Fase.ESCOLHENDO_TEMA) {
            EscolherTema(
                temas = viewModel.temas,
                onTemaEscolhido = { viewModel.iniciarJogo(it, tempoMaxSegundos) }
            )
        } else {
            Partida(estado = estado, onLetra = viewModel::tentarLetra)
        }

        if (estado.fase == Fase.FIM) {
            DialogFimDeJogo(
                estado = estado,
                onSalvar = { iniciais ->
                    onFimDeJogo(iniciais, estado.pontos, estado.tema)
                    viewModel.voltarParaTemas()
                }
            )
        }
    }
}

// ============================================================
//  Escolha de tema
// ============================================================
@Composable
private fun EscolherTema(temas: List<String>, onTemaEscolhido: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("ESCOLHA UM TEMA", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))
        temas.forEach { tema ->
            Button(
                onClick = { onTemaEscolhido(tema) },
                modifier = Modifier.fillMaxWidth(0.7f).height(56.dp)
            ) {
                Text(tema.uppercase(), fontSize = 18.sp)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}

// ============================================================
//  Partida (cabeçalho + forca + palavra + teclado)
// ============================================================
@Composable
private fun Partida(estado: JogoUiState, onLetra: (Char) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Cabecalho(estado)
        DesenhoForca(
            erros = estado.erros,
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp)
        )
        PalavraSecreta(estado)
        Spacer(Modifier.height(16.dp))
        Teclado(estado = estado, onLetra = onLetra)
    }
}

@Composable
private fun Cabecalho(estado: JogoUiState) {
    val tempoCritico = estado.tempoRestante <= 10
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "TEMPO: ${estado.tempoRestante}s",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = if (tempoCritico) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "PONTOS: ${estado.pontos}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
    Spacer(Modifier.height(4.dp))
    Text(
        text = "${estado.tema} • Palavra ${estado.numeroPalavra}/${estado.totalPalavras}" +
                " • Tentativas: ${estado.tentativasRestantes}/$MAX_TENTATIVAS",
        fontSize = 14.sp,
        color = MaterialTheme.colorScheme.outline
    )
}

// ============================================================
//  Forca: forca sempre visível; o boneco "aparece" a cada erro
// ============================================================
@Composable
private fun DesenhoForca(erros: Int, modifier: Modifier = Modifier) {
    val corForca = MaterialTheme.colorScheme.onBackground
    val corBoneco = MaterialTheme.colorScheme.primary

    Canvas(modifier = modifier) {
        val lado = min(size.width, size.height)
        val ox = (size.width - lado) / 2f
        val oy = (size.height - lado) / 2f
        val traco = 6.dp.toPx()

        // converte coordenadas 0..1 para pixels dentro de um quadrado centralizado
        fun p(x: Float, y: Float) = Offset(ox + x * lado, oy + y * lado)

        // estrutura da forca (sempre aparece)
        drawLine(corForca, p(0.10f, 0.95f), p(0.90f, 0.95f), traco, StrokeCap.Round) // base
        drawLine(corForca, p(0.25f, 0.95f), p(0.25f, 0.05f), traco, StrokeCap.Round) // poste
        drawLine(corForca, p(0.25f, 0.05f), p(0.65f, 0.05f), traco, StrokeCap.Round) // viga
        drawLine(corForca, p(0.65f, 0.05f), p(0.65f, 0.15f), traco, StrokeCap.Round) // corda

        // partes do boneco, uma por erro
        if (erros >= 1) drawCircle(                                                   // cabeça
            color = corBoneco,
            radius = 0.08f * lado,
            center = p(0.65f, 0.23f),
            style = Stroke(width = traco)
        )
        if (erros >= 2) drawLine(corBoneco, p(0.65f, 0.31f), p(0.65f, 0.60f), traco, StrokeCap.Round) // corpo
        if (erros >= 3) drawLine(corBoneco, p(0.65f, 0.38f), p(0.52f, 0.50f), traco, StrokeCap.Round) // braço esq.
        if (erros >= 4) drawLine(corBoneco, p(0.65f, 0.38f), p(0.78f, 0.50f), traco, StrokeCap.Round) // braço dir.
        if (erros >= 5) drawLine(corBoneco, p(0.65f, 0.60f), p(0.54f, 0.80f), traco, StrokeCap.Round) // perna esq.
        if (erros >= 6) drawLine(corBoneco, p(0.65f, 0.60f), p(0.76f, 0.80f), traco, StrokeCap.Round) // perna dir.
    }
}

// ============================================================
//  Palavra com traços:  _ A _ _ O
// ============================================================
@Composable
private fun PalavraSecreta(estado: JogoUiState) {
    val revelarTudo = estado.fase == Fase.FIM // ao perder, mostra a palavra
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally)
    ) {
        estado.palavraAtual.forEach { letra ->
            val acertada = letra in estado.letrasCertas
            Column(
                modifier = Modifier.width(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (acertada || revelarTudo) letra.toString() else " ",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    color = if (acertada) MaterialTheme.colorScheme.onBackground
                    else MaterialTheme.colorScheme.primary
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .background(MaterialTheme.colorScheme.onBackground)
                )
            }
        }
    }
}

// ============================================================
//  Teclado: certa = verde e bloqueada | errada = vermelha e bloqueada
// ============================================================
private enum class EstadoTecla { NORMAL, CERTA, ERRADA }

@Composable
private fun Teclado(estado: JogoUiState, onLetra: (Char) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        LINHAS_TECLADO.forEach { linha ->
            Row(
                // cada linha ocupa a fração certa da largura para as teclas ficarem do mesmo tamanho
                modifier = Modifier.fillMaxWidth(linha.length / 10f),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                linha.forEach { letra ->
                    val situacao = when (letra) {
                        in estado.letrasCertas -> EstadoTecla.CERTA
                        in estado.letrasErradas -> EstadoTecla.ERRADA
                        else -> EstadoTecla.NORMAL
                    }
                    Tecla(
                        letra = letra,
                        situacao = situacao,
                        habilitada = estado.fase == Fase.JOGANDO && !estado.aguardandoProxima,
                        onClick = { onLetra(letra) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun Tecla(
    letra: Char,
    situacao: EstadoTecla,
    habilitada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cor = when (situacao) {
        EstadoTecla.NORMAL -> MaterialTheme.colorScheme.secondary
        EstadoTecla.CERTA -> VerdeAcerto
        EstadoTecla.ERRADA -> MaterialTheme.colorScheme.primary
    }
    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cor)
            .clickable(enabled = habilitada && situacao == EstadoTecla.NORMAL, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letra.toString(),
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// ============================================================
//  Popup final estilo arcade (até 4 letras)
// ============================================================
@Composable
private fun DialogFimDeJogo(estado: JogoUiState, onSalvar: (String) -> Unit) {
    var iniciais by rememberSaveable { mutableStateOf("") }

    val titulo = when (estado.motivoFim) {
        MotivoFim.TEMPO_ESGOTADO -> "TEMPO ESGOTADO!"
        MotivoFim.SEM_TENTATIVAS -> "FIM DE JOGO"
        MotivoFim.TODAS_ACERTADAS -> "VOCÊ VENCEU!"
        null -> "FIM"
    }

    AlertDialog(
        onDismissRequest = { /* obriga o jogador a salvar */ },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
        title = { Text(titulo, fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (estado.motivoFim == MotivoFim.SEM_TENTATIVAS) {
                    Text("A palavra era: ${estado.palavraAtual}")
                    Spacer(Modifier.height(8.dp))
                }
                Text("${estado.pontos} PONTOS", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(
                    value = iniciais,
                    onValueChange = { novo ->
                        iniciais = novo.uppercase().filter { it in 'A'..'Z' }.take(4)
                    },
                    modifier = Modifier.width(170.dp),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 30.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 4.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        keyboardType = KeyboardType.Ascii,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { if (iniciais.isNotEmpty()) onSalvar(iniciais) }
                    ),
                    placeholder = {
                        Text(
                            "AAAA",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 30.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                )
                Spacer(Modifier.height(4.dp))
                Text("Digite até 4 letras", fontSize = 12.sp, color = MaterialTheme.colorScheme.outline)
            }
        },
        confirmButton = {
            Button(onClick = { onSalvar(iniciais) }, enabled = iniciais.isNotEmpty()) {
                Text("SALVAR")
            }
        }
    )
}
