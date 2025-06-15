package com.example.controle_financeiro.model

data class Periodicidade(
    val diaFixo: Int,           // Ex: 5
    val mesInicio: Int? = null, // Ex: 5 (maio), opcional
    val mesFim: Int? = null     // Ex: 10 (outubro), opcional
)
