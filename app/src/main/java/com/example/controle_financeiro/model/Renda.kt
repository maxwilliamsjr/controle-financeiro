package com.example.controle_financeiro.model

data class Renda(
    val id: String,
    var tipo: String,               // Novo campo (ex: salário, bônus)
    var fontePagadora: String,      // Novo campo (ex: nome da empresa)
    var valor: Double,
    var dataRecebimento: String = "",    // Ex: "2025-06-12"
    var categoria: Categoria,
    var descricao: String? = null,  // Opcional
    var periodicidade: Periodicidade? = null // A ser definida
)
