package com.example.forca.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forca.data.model.MAX_LETRAS_PALAVRA
import com.example.forca.data.model.Palavra
import com.example.forca.data.model.Tema
import com.example.forca.viewmodel.AdminViewModel

private sealed interface Dialogo {
    data object Adicionar : Dialogo
    data class Editar(val palavra: Palavra) : Dialogo
    data class Excluir(val palavra: Palavra) : Dialogo
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdmin(
    onVoltar: () -> Unit = {},
    onAbrirRanking: () -> Unit = {},
    viewModel: AdminViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var abaSelecionada by rememberSaveable { mutableIntStateOf(0) }
    var dialogo by remember { mutableStateOf<Dialogo?>(null) }
    val snackbar = remember { SnackbarHostState() }
    val tema = Tema.entries[abaSelecionada]

    LaunchedEffect(state.erro) {
        state.erro?.let {
            snackbar.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administração") },
                navigationIcon = { TextButton(onClick = onVoltar) { Text("Voltar") } },
                actions = { TextButton(onClick = onAbrirRanking) { Text("Ranking") } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = { dialogo = Dialogo.Adicionar }) {
                Text("+ Palavra")
            }
        },
        snackbarHost = { SnackbarHost(snackbar) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            TabRow(selectedTabIndex = abaSelecionada) {
                Tema.entries.forEachIndexed { index, t ->
                    Tab(
                        selected = index == abaSelecionada,
                        onClick = { abaSelecionada = index },
                        text = { Text(t.titulo) }
                    )
                }
            }

            val lista = state.palavras[tema]
            when {
                lista == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                lista.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhuma palavra em ${tema.titulo}.\nToque em \"+ Palavra\" para adicionar.")
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(
                        start = 16.dp, top = 16.dp, end = 16.dp, bottom = 88.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(lista, key = { it.id }) { palavra ->
                        ItemPalavra(
                            palavra = palavra,
                            onEditar = { dialogo = Dialogo.Editar(palavra) },
                            onExcluir = { dialogo = Dialogo.Excluir(palavra) }
                        )
                    }
                }
            }
        }
    }

    when (val d = dialogo) {
        Dialogo.Adicionar -> DialogoTexto(
            titulo = "Nova palavra (${tema.titulo})",
            valorInicial = "",
            textoConfirmar = "Adicionar",
            onConfirmar = { viewModel.adicionar(tema, it); dialogo = null },
            onCancelar = { dialogo = null }
        )

        is Dialogo.Editar -> DialogoTexto(
            titulo = "Editar palavra",
            valorInicial = d.palavra.texto,
            textoConfirmar = "Salvar",
            onConfirmar = { viewModel.editar(tema, d.palavra.id, it); dialogo = null },
            onCancelar = { dialogo = null }
        )

        is Dialogo.Excluir -> AlertDialog(
            onDismissRequest = { dialogo = null },
            title = { Text("Excluir palavra") },
            text = { Text("Excluir \"${d.palavra.texto}\" de ${tema.titulo}?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.excluir(tema, d.palavra.id)
                    dialogo = null
                }) { Text("Excluir") }
            },
            dismissButton = { TextButton(onClick = { dialogo = null }) { Text("Cancelar") } }
        )

        null -> Unit
    }
}

@Composable
private fun ItemPalavra(
    palavra: Palavra,
    onEditar: () -> Unit,
    onExcluir: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = palavra.texto,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = onEditar) { Text("Editar") }
            TextButton(onClick = onExcluir) { Text("Excluir") }
        }
    }
}

@Composable
private fun DialogoTexto(
    titulo: String,
    valorInicial: String,
    textoConfirmar: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var texto by remember { mutableStateOf(valorInicial) }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(titulo) },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { novo -> texto = novo.filter { it != ' ' }.take(MAX_LETRAS_PALAVRA) },
                label = { Text("Palavra (até $MAX_LETRAS_PALAVRA letras)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters)
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(texto) }, enabled = texto.isNotBlank()) {
                Text(textoConfirmar)
            }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } }
    )
}