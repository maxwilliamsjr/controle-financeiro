package com.example.controle_financeiro.model

data class Despesa(
    val id: String = "",
    val nome: String = "",
    val descricao: String = "",
    val valor: Double = 0.0,
    val data: String = "",
    val categoria: String = "",
    val metodoPagamento: String = "",
    val userId: String = "" // adiciona esse campo para identificar o dono da despesa
)
