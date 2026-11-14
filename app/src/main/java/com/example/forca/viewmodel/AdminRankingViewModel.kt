package com.example.forca.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.forca.data.local.AppDatabase
import com.example.forca.data.local.Partida
import com.example.forca.data.repository.RankingAdminRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

const val PONTUACAO_MAXIMA = 999_999

class AdminRankingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RankingAdminRepository(
        AppDatabase.getInstance(application).partidaDao()
    )

    // Já vem ordenado por pontuação (maior primeiro)
    val partidas: StateFlow<List<Partida>> = repository.ranking
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _erro = MutableStateFlow<String?>(null)
    val erro: StateFlow<String?> = _erro.asStateFlow()

    fun alterarPontuacao(partida: Partida, texto: String) {
        val nova = texto.trim().toIntOrNull()
        if (nova == null || nova < 0 || nova > PONTUACAO_MAXIMA) {
            _erro.value = "Digite um número entre 0 e $PONTUACAO_MAXIMA."
            return
        }
        if (nova == partida.pontuacao) return
        executar("Erro ao salvar") { repository.atualizarPontuacao(partida, nova) }
    }

    fun remover(partida: Partida) {
        executar("Erro ao remover") { repository.remover(partida) }
    }

    fun limparErro() {
        _erro.value = null
    }

    private fun executar(msgErro: String, bloco: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                bloco()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _erro.value = "$msgErro: ${e.message}"
            }
        }
    }
}