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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.forca.ui.viewmodel.RankingViewModel

@Composable
fun TelaHistorico(
    modifier: Modifier = Modifier,
    viewModel: RankingViewModel = viewModel()
) {
    val partidas by viewModel.partidas.collectAsState()
    val busca by viewModel.busca.collectAsState()

    TelaHistoricoConteudo(
        partidas = partidas,
        busca = busca,
        onBuscaChange = viewModel::onBuscaChange,
        onInserirTeste = viewModel::inserirTeste,
        modifier = modifier
    )
}

@Composable
fun TelaHistoricoConteudo(
    partidas: List<Partida>,
    busca: String,
    onBuscaChange: (String) -> Unit,
    onInserirTeste: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Histórico",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = busca,
            onValueChange = onBuscaChange,
            placeholder = { Text("Pesquisar nome") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (partidas.isEmpty()) {
            Text(
                text = "Nenhuma partida encontrada.",
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                itemsIndexed(partidas, key = { _, p -> p.id }) { index, partida ->
                    LinhaPartida(posicao = index + 1, partida = partida)
                }
            }
        }

        Button(onClick = onInserirTeste, modifier = Modifier.fillMaxWidth()) {
            Text("Inserir partida de teste")
        }
    }
}

@Composable
fun LinhaPartida(posicao: Int, partida: Partida) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = "$posicao",
                fontWeight = FontWeight.Bold,
                color = if (posicao <= 3) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(32.dp)
            )

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

            Text(
                text = partida.nomeJogador,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = "${partida.pontuacao}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TelaHistoricoPreview() {
    ForcaTheme {
        TelaHistoricoConteudo(
            partidas = listOf(
                Partida(1, "CAT", 5320, "gato"),
                Partida(2, "DOG", 4300, "cachorro"),
                Partida(3, "HEX", 4532, "bruxa")
            ),
            busca = "",
            onBuscaChange = {},
            onInserirTeste = {}
        )
    }
}