package com.example.forca.data.remote

/**
 * FONTE DE DADOS TEMPORÁRIA (apenas para testar o jogo).
 *
 * Quando o banco de dados/tela Admin estiver pronto, basta trocar o corpo de
 * [carregarPalavras] para ler do banco (ou criar outra fonte) — o resto do
 * jogo não precisa mudar, pois só conversa com o PalavraRepository.
 *
 * REGRAS para as palavras: MAIÚSCULAS, sem acento, sem espaço, até 10 letras.
 */
class PalavraDataSource {

    fun carregarPalavras(): Map<String, List<String>> = mapOf(
        "Alimentos" to listOf(
            "PIZZA", "BANANA", "CHOCOLATE", "ARROZ", "FEIJAO",
            "MELANCIA", "SORVETE", "QUEIJO", "MACARRAO", "BOLO"
        ),
        "Animais" to listOf(
            "GATO", "CACHORRO", "ELEFANTE", "GIRAFA", "TARTARUGA",
            "PINGUIM", "COELHO", "LEAO", "MACACO", "BORBOLETA"
        ),
        "Objetos" to listOf(
            "CADEIRA", "TELEFONE", "MOCHILA", "CANETA", "RELOGIO",
            "GARFO", "LAMPADA", "TESOURA", "ESPELHO", "JANELA"
        )
    )
}