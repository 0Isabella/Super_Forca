package com.example.forca.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forca.data.local.Partida
import com.example.forca.viewmodel.AdminRankingViewModel
import com.example.forca.viewmodel.PONTUACAO_MAXIMA
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val formatoData = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR"))

private sealed interface DialogoRanking {
    data class EditarPontos(val partida: Partida) : DialogoRanking
    data class Remover(val partida: Partida) : DialogoRanking
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdminRanking(
    onVoltar: () -> Unit = {},
    viewModel: AdminRankingViewModel = viewModel()
) {
    val partidas by viewModel.partidas.collectAsState()
    val erro by viewModel.erro.collectAsState()
    var dialogo by remember { mutableStateOf<DialogoRanking?>(null) }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(erro) {
        erro?.let {
            snackbar.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin: Ranking") },
                navigationIcon = { TextButton(onClick = onVoltar) { Text("Voltar") } }
            )
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { innerPadding ->
        if (partidas.isEmpty()) {
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Nenhuma partida no ranking ainda.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(partidas, key = { _, p -> p.id }) { index, partida ->
                    ItemRanking(
                        posicao = index + 1,
                        partida = partida,
                        onEditar = { dialogo = DialogoRanking.EditarPontos(partida) },
                        onRemover = { dialogo = DialogoRanking.Remover(partida) }
                    )
                }
            }
        }
    }

    when (val d = dialogo) {
        is DialogoRanking.EditarPontos -> DialogoPontuacao(
            nome = d.partida.nomeJogador,
            pontuacaoAtual = d.partida.pontuacao,
            onConfirmar = { viewModel.alterarPontuacao(d.partida, it); dialogo = null },
            onCancelar = { dialogo = null }
        )

        is DialogoRanking.Remover -> AlertDialog(
            onDismissRequest = { dialogo = null },
            title = { Text("Remover do ranking") },
            text = { Text("Remover ${d.partida.nomeJogador} (${d.partida.pontuacao} pts)? Isso também some do histórico.") },
            confirmButton = {
                TextButton(onClick = { viewModel.remover(d.partida); dialogo = null }) { Text("Remover") }
            },
            dismissButton = { TextButton(onClick = { dialogo = null }) { Text("Cancelar") } }
        )

        null -> Unit
    }
}

@Composable
private fun ItemRanking(
    posicao: Int,
    partida: Partida,
    onEditar: () -> Unit,
    onRemover: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${posicao}º",
                fontWeight = FontWeight.Bold,
                color = if (posicao <= 3) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(44.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(partida.nomeJogador, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${partida.pontuacao} pts · ${formatoData.format(Date(partida.data))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
            TextButton(onClick = onEditar) { Text("Editar") }
            TextButton(onClick = onRemover) { Text("Remover") }
        }
    }
}

@Composable
private fun DialogoPontuacao(
    nome: String,
    pontuacaoAtual: Int,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var texto by remember { mutableStateOf(pontuacaoAtual.toString()) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Pontuação de $nome") },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { novo ->
                    texto = novo.filter { it.isDigit() }.take(PONTUACAO_MAXIMA.toString().length)
                },
                label = { Text("Pontos") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(texto) }, enabled = texto.isNotEmpty()) {
                Text("Salvar")
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}