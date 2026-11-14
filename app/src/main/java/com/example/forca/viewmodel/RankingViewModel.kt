package com.example.forca.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.forca.data.local.AppDatabase
import com.example.forca.data.local.Partida
import com.example.forca.data.repository.PartidaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RankingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PartidaRepository(
        AppDatabase.getInstance(application).partidaDao()
    )

    private val _busca = MutableStateFlow("")
    val busca: StateFlow<String> = _busca

    @OptIn(ExperimentalCoroutinesApi::class)
    val partidas: StateFlow<List<Partida>> = _busca
        .flatMapLatest { texto ->
            if (texto.isBlank()) repository.ranking else repository.buscarPorNome(texto)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onBuscaChange(texto: String) {
        _busca.value = texto
    }

    fun inserirTeste() {
        viewModelScope.launch {
            repository.salvar(Partida(nomeJogador = "CAT", pontuacao = 5320, palavra = "gato"))
        }
    }
}