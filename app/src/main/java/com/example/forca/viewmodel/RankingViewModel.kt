package com.example.forca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.forca.data.local.AppDatabase
import com.example.forca.data.local.Partida
import com.example.forca.data.repository.PartidaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ModoLista { RANKING, HISTORICO }

data class ItemLista(val posicao: Int, val partida: Partida)

class RankingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PartidaRepository(
        AppDatabase.getInstance(application).partidaDao()
    )

    private val _busca = MutableStateFlow("")
    val busca: StateFlow<String> = _busca

    private val _modo = MutableStateFlow(ModoLista.RANKING)
    val modo: StateFlow<ModoLista> = _modo

    val itens: StateFlow<List<ItemLista>> = combine(
        _modo, _busca, repository.ranking, repository.historico
    ) { modo, busca, ranking, historico ->
        val base = if (modo == ModoLista.RANKING) ranking else historico
        base
            .mapIndexed { i, partida -> ItemLista(i + 1, partida) }
            .filter { it.partida.nomeJogador.contains(busca.trim(), ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lider: StateFlow<Partida?> = repository.ranking
        .map { it.firstOrNull() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onBuscaChange(texto: String) {
        _busca.value = texto
    }

    fun onModoChange(novoModo: ModoLista) {
        _modo.value = novoModo
    }

    fun salvarPartida(iniciais: String, pontos: Int, tema: String) {
        viewModelScope.launch {
            repository.salvar(Partida(nomeJogador = iniciais, pontuacao = pontos, tema = tema))
        }
        _busca.value = ""
        _modo.value = ModoLista.RANKING
    }
}