package com.example.forca.data.repository

import com.example.forca.data.local.Partida
import com.example.forca.data.local.PartidaDao
import kotlinx.coroutines.flow.Flow

class PartidaRepository(private val dao: PartidaDao) {

    val ranking: Flow<List<Partida>> = dao.listarRanking()
    val historico: Flow<List<Partida>> = dao.listarHistorico()

    fun buscarPorNome(nome: String): Flow<List<Partida>> = dao.buscarPorNome(nome)

    suspend fun salvar(partida: Partida) = dao.inserir(partida)

    suspend fun deletar(partida: Partida) = dao.deletar(partida)
}