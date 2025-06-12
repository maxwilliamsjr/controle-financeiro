package com.example.controle_financeiro.model

sealed class MetodoPagamento {
    object Dinheiro : MetodoPagamento()
    data class CartaoCredito(val cartao: Cartao) : MetodoPagamento()
    object Pix : MetodoPagamento()
    object Debito : MetodoPagamento()
}
