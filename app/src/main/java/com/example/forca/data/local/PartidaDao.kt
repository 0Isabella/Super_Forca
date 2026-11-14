package com.example.forca.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PartidaDao {

    @Insert
    suspend fun inserir(partida: Partida)

    @Query("SELECT * FROM partidas ORDER BY pontuacao DESC")
    fun listarRanking(): Flow<List<Partida>>

    @Query("SELECT * FROM partidas ORDER BY data DESC")
    fun listarHistorico(): Flow<List<Partida>>

    @Query("SELECT * FROM partidas WHERE nomeJogador LIKE '%' || :nome || '%' ORDER BY pontuacao DESC")
    fun buscarPorNome(nome: String): Flow<List<Partida>>

    @Update
    suspend fun atualizar(partida: Partida)

    @Delete
    suspend fun deletar(partida: Partida)

    @Query("DELETE FROM partidas")
    suspend fun limparTudo()
}