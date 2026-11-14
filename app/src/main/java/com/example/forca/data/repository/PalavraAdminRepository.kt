package com.example.forca.data.repository

import com.example.forca.data.model.Palavra
import com.example.forca.data.model.Tema
import com.example.forca.data.remote.PalavraFirestoreDataSource
import kotlinx.coroutines.flow.Flow

class PalavraAdminRepository(
    private val dataSource: PalavraFirestoreDataSource = PalavraFirestoreDataSource()
) {
    fun observar(tema: Tema): Flow<List<Palavra>> = dataSource.observar(tema)

    suspend fun adicionar(tema: Tema, texto: String) = dataSource.adicionar(tema, texto)

    suspend fun atualizar(tema: Tema, id: String, texto: String) =
        dataSource.atualizar(tema, id, texto)

    suspend fun deletar(tema: Tema, id: String) = dataSource.deletar(tema, id)
}