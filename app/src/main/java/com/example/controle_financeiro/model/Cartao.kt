package com.example.controle_financeiro.model

data class Cartao(
    val id: String = "",
    var nome: String = "",
    var banco: String = "",
    var tipo: String = "", // "Crédito" ou "Débito"
    var vencimento: String = "", // Ex: "15" (dia do vencimento)
    var faturaAtual: Double = 0.0
)
