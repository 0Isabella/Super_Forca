
package com.example.forca.data.remote

import com.example.forca.data.model.Palavra
import com.example.forca.data.model.Tema
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Fonte local utilizada pelo jogo.
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

// Fonte remota utilizada pela administração.
class PalavraFirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun colecao(tema: Tema): CollectionReference =
        db.collection(tema.colecao)

    fun observar(tema: Tema): Flow<List<Palavra>> = callbackFlow {
        val listener = colecao(tema)
            .orderBy("texto")
            .addSnapshotListener { snapshot, erro ->
                if (erro != null) {
                    close(erro)
                    return@addSnapshotListener
                }

                val lista = snapshot?.documents?.map { doc ->
                    Palavra(
                        id = doc.id,
                        texto = doc.getString("texto") ?: ""
                    )
                } ?: emptyList()

                trySend(lista)
            }

        awaitClose { listener.remove() }
    }

    suspend fun adicionar(tema: Tema, texto: String) {
        colecao(tema).add(mapOf("texto" to texto)).await()
    }

    suspend fun atualizar(tema: Tema, id: String, texto: String) {
        colecao(tema).document(id).update("texto", texto).await()
    }

    suspend fun deletar(tema: Tema, id: String) {
        colecao(tema).document(id).delete().await()
    }
}
