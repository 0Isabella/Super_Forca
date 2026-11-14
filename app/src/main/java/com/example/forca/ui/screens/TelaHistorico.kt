package com.example.forca.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forca.data.local.Partida
import com.example.forca.ui.theme.ForcaTheme
import com.example.forca.viewmodel.ItemLista
import com.example.forca.viewmodel.ModoLista
import com.example.forca.viewmodel.RankingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TelaHistorico(
    modifier: Modifier = Modifier,
    viewModel: RankingViewModel = viewModel()
) {
    val itens by viewModel.itens.collectAsState()
    val busca by viewModel.busca.collectAsState()
    val modo by viewModel.modo.collectAsState()
    val lider by viewModel.lider.collectAsState()

    TelaHistoricoConteudo(
        itens = itens,
        busca = busca,
        modo = modo,
        lider = lider,
        onBuscaChange = viewModel::onBuscaChange,
        onModoChange = viewModel::onModoChange,
        modifier = modifier
    )
}

@Composable
fun TelaHistoricoConteudo(
    itens: List<ItemLista>,
    busca: String,
    modo: ModoLista,
    lider: Partida?,
    onBuscaChange: (String) -> Unit,
    onModoChange: (ModoLista) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = if (modo == ModoLista.RANKING) "Ranking" else "Histórico",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = modo == ModoLista.RANKING,
                onClick = { onModoChange(ModoLista.RANKING) },
                label = { Text("Ranking") }
            )
            FilterChip(
                selected = modo == ModoLista.HISTORICO,
                onClick = { onModoChange(ModoLista.HISTORICO) },
                label = { Text("Histórico") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = busca,
            onValueChange = onBuscaChange,
            placeholder = { Text("Pesquisar nome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (modo == ModoLista.RANKING && lider != null) {
            CardLider(lider)
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (itens.isEmpty()) {
            Text(
                text = if (busca.isBlank()) "Nenhuma partida ainda. Vá jogar!"
                else "Nenhum jogador encontrado.",
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(itens, key = { it.partida.id }) { item ->
                    LinhaPartida(item = item, modo = modo)
                }
            }
        }
    }
}

@Composable
fun CardLider(lider: Partida) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "1º LUGAR",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = lider.nomeJogador,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = "${lider.pontuacao} pts",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun LinhaPartida(item: ItemLista, modo: ModoLista) {
    val partida = item.partida
    val ehRanking = modo == ModoLista.RANKING

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            if (ehRanking) {
                Text(
                    text = "${item.posicao}",
                    fontWeight = FontWeight.Bold,
                    color = if (item.posicao <= 3) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.width(32.dp)
                )
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.secondary, CircleShape)
            ) {
                Text(
                    text = partida.nomeJogador.take(1).uppercase(),
                    color = MaterialTheme.colorScheme.onSecondary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = partida.nomeJogador,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (ehRanking) partida.tema
                    else "${partida.tema} • ${formatarData(partida.data)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Text(
                text = "${partida.pontuacao}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

private fun formatarData(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.forLanguageTag("pt-BR")).format(Date(millis))

@Preview(showBackground = true)
@Composable
fun TelaHistoricoPreview() {
    val partidas = listOf(
        Partida(1, "CAT", 500, "Animais"),
        Partida(2, "DOG", 300, "Alimentos"),
        Partida(3, "HEX", 200, "Objetos")
    )
    ForcaTheme {
        TelaHistoricoConteudo(
            itens = partidas.mapIndexed { i, p -> ItemLista(i + 1, p) },
            busca = "",
            modo = ModoLista.RANKING,
            lider = partidas.first(),
            onBuscaChange = {},
            onModoChange = {}
        )
    }
}