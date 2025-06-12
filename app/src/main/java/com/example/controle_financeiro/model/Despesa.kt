package com.example.controle_financeiro.model

data class Despesa(
    val id: String,
    var descricao: String,
    var valor: Double,
    var data: String, // Ex: "2025-06-12"
    var categoria: Categoria,
    var metodoPagamento: MetodoPagamento
)
