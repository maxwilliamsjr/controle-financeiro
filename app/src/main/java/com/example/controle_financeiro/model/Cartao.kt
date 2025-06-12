package com.example.controle_financeiro.model

data class Cartao(
    val id: String,
    var nome: String,
    var numero: String,
    var limite: Double,
    var vencimento: String, // Ex: "15" para dia 15
    var faturaAtual: Double = 0.0
)
