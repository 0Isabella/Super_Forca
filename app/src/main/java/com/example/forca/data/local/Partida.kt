package com.example.forca.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "partidas")
data class Partida(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nomeJogador: String,
    val pontuacao: Int,
    val tema: String,
    val data: Long = System.currentTimeMillis()
)