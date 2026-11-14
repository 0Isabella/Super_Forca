package com.example.forca.data.repository

import com.example.forca.data.local.Partida
import com.example.forca.data.local.PartidaDao
import kotlinx.coroutines.flow.Flow

// Usa o DAO do ranking (Room) direto, sem alterar o PartidaRepository do Jhonatan.
class RankingAdminRepository(private val dao: PartidaDao) {

    val ranking: Flow<List<Partida>> = dao.listarRanking()

    suspend fun atualizarPontuacao(partida: Partida, novaPontuacao: Int) =
        dao.atualizar(partida.copy(pontuacao = novaPontuacao))

    suspend fun remover(partida: Partida) = dao.deletar(partida)
}