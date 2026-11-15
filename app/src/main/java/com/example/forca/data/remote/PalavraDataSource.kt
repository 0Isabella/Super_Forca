package com.example.forca.data.remote

import com.example.forca.data.model.Palavra
import com.example.forca.data.model.Tema
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

// Fonte de palavras: Firestore (usada pelo jogo e pela administração).
class PalavraFirestoreDataSource(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private fun colecao(tema: Tema): CollectionReference =
        db.collection(tema.colecao)

    /** Busca uma vez só os textos das palavras do tema (usado pelo jogo). */
    suspend fun buscar(tema: Tema): List<String> =
        colecao(tema).get().await()
            .documents
            .mapNotNull { it.getString("texto") }

    /** Acompanha a lista em tempo real (usado pela tela Admin). */
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