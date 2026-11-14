package com.example.forca.data.remote

import com.example.forca.data.model.Palavra
import com.example.forca.data.model.Tema
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PalavraFirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun colecao(tema: Tema): CollectionReference = db.collection(tema.colecao)

    // READ: escuta em tempo real
    fun observar(tema: Tema): Flow<List<Palavra>> = callbackFlow {
        val listener = colecao(tema)
            .orderBy("texto")
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.map { doc ->
                    Palavra(id = doc.id, texto = doc.getString("texto") ?: "")
                } ?: emptyList()
                trySend(lista)
            }
        awaitClose { listener.remove() }
    }

    // CREATE
    suspend fun adicionar(tema: Tema, texto: String) {
        colecao(tema).add(mapOf("texto" to texto)).await()
    }

    // UPDATE
    suspend fun atualizar(tema: Tema, id: String, texto: String) {
        colecao(tema).document(id).update("texto", texto).await()
    }

    // DELETE
    suspend fun deletar(tema: Tema, id: String) {
        colecao(tema).document(id).delete().await()
    }

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
}