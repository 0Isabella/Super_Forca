package com.example.forca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forca.data.model.MAX_LETRAS_PALAVRA
import com.example.forca.data.model.Palavra
import com.example.forca.data.model.Tema
import com.example.forca.data.repository.PalavraAdminRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer

data class AdminUiState(
    // tema ausente no mapa = ainda carregando
    val palavras: Map<Tema, List<Palavra>> = emptyMap(),
    val erro: String? = null
)

class AdminViewModel(
    private val repository: PalavraAdminRepository = PalavraAdminRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        Tema.entries.forEach { observar(it) }
    }

    private fun observar(tema: Tema) {
        viewModelScope.launch {
            repository.observar(tema)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            erro = "Erro ao carregar ${tema.titulo}: ${e.message}",
                            palavras = if (tema in it.palavras) it.palavras
                            else it.palavras + (tema to emptyList())
                        )
                    }
                }
                .collect { lista ->
                    _uiState.update { it.copy(palavras = it.palavras + (tema to lista)) }
                }
        }
    }

    fun adicionar(tema: Tema, texto: String) {
        val limpo = normalizar(texto)
        validar(tema, limpo, ignorarId = null)?.let { return mostrarErro(it) }
        executar("Erro ao adicionar") { repository.adicionar(tema, limpo) }
    }

    fun editar(tema: Tema, id: String, texto: String) {
        val limpo = normalizar(texto)
        validar(tema, limpo, ignorarId = id)?.let { return mostrarErro(it) }
        executar("Erro ao editar") { repository.atualizar(tema, id, limpo) }
    }

    fun excluir(tema: Tema, id: String) {
        executar("Erro ao excluir") { repository.deletar(tema, id) }
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }

    // ---------- helpers ----------

    // Regras do jogo: MAIÚSCULAS, sem acento ("FEIJÃO" vira "FEIJAO"), sem espaço, até 10 letras.
    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.trim(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .uppercase()

    private fun validar(tema: Tema, texto: String, ignorarId: String?): String? {
        if (texto.isEmpty()) return "Digite uma palavra."
        if (texto.contains(' ')) return "Use uma palavra só, sem espaços."
        if (!Regex("^[A-Z]+$").matches(texto)) return "Use apenas letras (sem números ou símbolos)."
        if (texto.length > MAX_LETRAS_PALAVRA) return "No máximo $MAX_LETRAS_PALAVRA letras."
        val jaExiste = _uiState.value.palavras[tema].orEmpty()
            .any { it.texto == texto && it.id != ignorarId }
        if (jaExiste) return "\"$texto\" já existe em ${tema.titulo}."
        return null
    }

    private fun mostrarErro(msg: String) {
        _uiState.update { it.copy(erro = msg) }
    }

    private fun executar(msgErro: String, bloco: suspend () -> Unit) {
        viewModelScope.launch {
            try {
                bloco()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                mostrarErro("$msgErro: ${e.message}")
            }
        }
    }
}