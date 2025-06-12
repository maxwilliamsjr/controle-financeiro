package com.example.controle_financeiro.model

data class Renda(
    val id: String,
    var fonte: String,
    var valor: Double,
    var dataRecebimento: String, // Ex: "2025-06-12"
    var categoria: Categoria
)
