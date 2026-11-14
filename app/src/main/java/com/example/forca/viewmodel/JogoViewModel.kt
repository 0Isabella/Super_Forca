package com.example.forca.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.forca.data.repository.PalavraRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ---- Regras do jogo (altere aqui se a equipe quiser mudar) ----
const val MAX_TENTATIVAS = 6          // = 6 partes do corpo
const val PONTOS_POR_PALAVRA = 100
const val TEMPO_PADRAO_SEGUNDOS = 120
const val PALAVRAS_POR_PARTIDA = 5
private const val PAUSA_ENTRE_PALAVRAS_MS = 800L

enum class Fase { ESCOLHENDO_TEMA, JOGANDO, FIM }

enum class MotivoFim { TEMPO_ESGOTADO, SEM_TENTATIVAS, TODAS_ACERTADAS }

/** Tudo que a tela precisa saber para se desenhar. */
data class JogoUiState(
    val fase: Fase = Fase.ESCOLHENDO_TEMA,
    val tema: String = "",
    val palavraAtual: String = "",
    val numeroPalavra: Int = 0,               // 1, 2, 3...
    val totalPalavras: Int = 0,
    val letrasCertas: Set<Char> = emptySet(),
    val letrasErradas: Set<Char> = emptySet(),
    val tentativasRestantes: Int = MAX_TENTATIVAS,
    val pontos: Int = 0,
    val tempoRestante: Int = TEMPO_PADRAO_SEGUNDOS,
    val aguardandoProxima: Boolean = false,   // pausa curta após acertar a palavra
    val motivoFim: MotivoFim? = null
) {
    /** Quantas partes do corpo já aparecem (0 a 6). */
    val erros: Int get() = MAX_TENTATIVAS - tentativasRestantes

    val palavraResolvida: Boolean
        get() = palavraAtual.isNotEmpty() && palavraAtual.all { it in letrasCertas }
}

class JogoViewModel : ViewModel() {

    private val repository = PalavraRepository()

    private val _uiState = MutableStateFlow(JogoUiState())
    val uiState: StateFlow<JogoUiState> = _uiState.asStateFlow()

    val temas: List<String> = repository.listarTemas()

    private var palavras: List<String> = emptyList()
    private var cronometro: Job? = null

    /** Começa uma partida nova no [tema] escolhido. */
    fun iniciarJogo(tema: String, tempoSegundos: Int = TEMPO_PADRAO_SEGUNDOS) {
        cronometro?.cancel()
        palavras = repository.sortearPalavras(tema, PALAVRAS_POR_PARTIDA)
        if (palavras.isEmpty()) return

        _uiState.value = JogoUiState(
            fase = Fase.JOGANDO,
            tema = tema,
            palavraAtual = palavras[0],
            numeroPalavra = 1,
            totalPalavras = palavras.size,
            tempoRestante = tempoSegundos
        )
        iniciarCronometro()
    }

    /** Chamado quando o jogador toca numa tecla do teclado. */
    fun tentarLetra(letra: Char) {
        val estado = _uiState.value
        if (estado.fase != Fase.JOGANDO || estado.aguardandoProxima) return
        if (letra in estado.letrasCertas || letra in estado.letrasErradas) return

        if (letra in estado.palavraAtual) {
            // ACERTO
            val novo = estado.copy(letrasCertas = estado.letrasCertas + letra)
            if (novo.palavraResolvida) {
                _uiState.value = novo.copy(
                    pontos = novo.pontos + PONTOS_POR_PALAVRA,
                    aguardandoProxima = true
                )
                avancarParaProximaPalavra()
            } else {
                _uiState.value = novo
            }
        } else {
            // ERRO
            val restantes = estado.tentativasRestantes - 1
            _uiState.value = estado.copy(
                letrasErradas = estado.letrasErradas + letra,
                tentativasRestantes = restantes
            )
            if (restantes <= 0) finalizar(MotivoFim.SEM_TENTATIVAS)
        }
    }

    /** Volta para a escolha de tema (usado depois de salvar a pontuação). */
    fun voltarParaTemas() {
        cronometro?.cancel()
        _uiState.value = JogoUiState()
    }

    // ---------------- internos ----------------

    private fun iniciarCronometro() {
        cronometro?.cancel()
        cronometro = viewModelScope.launch {
            while (_uiState.value.tempoRestante > 0) {
                delay(1000)
                _uiState.update { it.copy(tempoRestante = it.tempoRestante - 1) }
            }
            finalizar(MotivoFim.TEMPO_ESGOTADO)
        }
    }

    /** Mostra a palavra completa por um instante e então troca (ou encerra). */
    private fun avancarParaProximaPalavra() {
        viewModelScope.launch {
            delay(PAUSA_ENTRE_PALAVRAS_MS)
            val estado = _uiState.value
            if (estado.fase != Fase.JOGANDO) return@launch // tempo acabou na pausa

            if (estado.numeroPalavra >= estado.totalPalavras) {
                finalizar(MotivoFim.TODAS_ACERTADAS)
            } else {
                _uiState.value = estado.copy(
                    palavraAtual = palavras[estado.numeroPalavra],
                    numeroPalavra = estado.numeroPalavra + 1,
                    letrasCertas = emptySet(),
                    letrasErradas = emptySet(),
                    tentativasRestantes = MAX_TENTATIVAS, // "volta ao normal" ao acertar
                    aguardandoProxima = false
                )
            }
        }
    }

    private fun finalizar(motivo: MotivoFim) {
        cronometro?.cancel()
        _uiState.update {
            if (it.fase == Fase.JOGANDO) {
                it.copy(fase = Fase.FIM, motivoFim = motivo, aguardandoProxima = false)
            } else it
        }
    }
}