package com.example.forca.data.model

// Os títulos são idênticos aos usados no jogo (JogoViewModel/TelaJogo).
enum class Tema(val titulo: String, val colecao: String) {
    ALIMENTOS("Alimentos", "palavras_alimentos"),
    ANIMAIS("Animais", "palavras_animais"),
    OBJETOS("Objetos", "palavras_objetos")
}