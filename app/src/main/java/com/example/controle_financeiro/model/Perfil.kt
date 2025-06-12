package com.example.controle_financeiro.model

data class Perfil(
    val idUsuario: String,
    var preferencias: Map<String, Any> = emptyMap()
)
