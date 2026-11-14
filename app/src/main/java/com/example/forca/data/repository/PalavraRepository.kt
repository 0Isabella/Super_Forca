package com.example.forca.data.repository

import com.example.forca.data.remote.PalavraDataSource

/**
 * Camada que o jogo usa para pedir temas e palavras.
 * Esconde de onde os dados vêm (hoje: lista fixa; amanhã: banco de dados).
 */
class PalavraRepository(
    private val dataSource: PalavraDataSource = PalavraDataSource()
) {

    /** Nomes dos temas disponíveis (ex.: Alimentos, Animais, Objetos). */
    fun listarTemas(): List<String> = dataSource.carregarPalavras().keys.toList()

    /** Sorteia [quantidade] palavras diferentes do [tema], já em MAIÚSCULAS. */
    fun sortearPalavras(tema: String, quantidade: Int): List<String> =
        dataSource.carregarPalavras()[tema]
            .orEmpty()
            .map { it.trim().uppercase() }
            .filter { it.isNotEmpty() }
            .shuffled()
            .take(quantidade)
}