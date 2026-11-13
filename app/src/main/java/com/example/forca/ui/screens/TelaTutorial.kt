package com.example.forca.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun TelaTutorial(onFechar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Como jogar") },
        text = { Text("Escolha uma letra por vez. Acerte a palavra antes de o tempo ou as tentativas acabarem.") },
        confirmButton = { Button(onClick = onFechar) { Text("Entendi") } }
    )
}