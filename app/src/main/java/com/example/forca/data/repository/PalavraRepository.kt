package com.example.forca.data.repository

import com.example.forca.data.model.Tema
import com.example.forca.data.remote.PalavraFirestoreDataSource

/**
 * Camada que o jogo usa para pedir temas e palavras.
 * As palavras vêm do Firestore (as mesmas que a tela Admin edita).
 */
class PalavraRepository(
    private val dataSource: PalavraFirestoreDataSource = PalavraFirestoreDataSource()
) {

    /** Nomes dos temas disponíveis (ex.: Alimentos, Animais, Objetos). */
    fun listarTemas(): List<String> = Tema.entries.map { it.titulo }

    /** Busca as palavras do [tema] no Firestore e sorteia [quantidade], em MAIÚSCULAS. */
    suspend fun sortearPalavras(tema: String, quantidade: Int): List<String> {
        val temaEnum = Tema.entries.firstOrNull { it.titulo == tema } ?: return emptyList()

        return dataSource.buscar(temaEnum)
            .map { it.trim().uppercase() }
            .filter { it.isNotEmpty() }
            .shuffled()
            .take(quantidade)
    }
}