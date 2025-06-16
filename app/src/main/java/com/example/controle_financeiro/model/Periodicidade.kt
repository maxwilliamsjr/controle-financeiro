package com.example.controle_financeiro.model

data class Periodicidade(
    val diaFixo: Int,             // Ex: 5
    val mesInicio: Int? = null,   // Ex: 5 (maio)
    val anoInicio: Int? = null,   // Ex: 2025
    val mesFim: Int? = null,      // Ex: 10 (outubro)
    val anoFim: Int? = null       // Ex: 2026
)
