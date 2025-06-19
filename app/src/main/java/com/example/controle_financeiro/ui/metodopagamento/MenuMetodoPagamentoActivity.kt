package com.example.controle_financeiro.ui.metodopagamento

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R

class MenuMetodoPagamentoActivity : AppCompatActivity() {

    private lateinit var btnListarMetodos: Button
    private lateinit var btnCadastrarCartao: Button
    private lateinit var txtMensagem: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_metodo_pagamento)
        supportActionBar?.title = "Menu Método de Pagamento"

        btnListarMetodos = findViewById(R.id.btnListarMetodos)
        btnCadastrarCartao = findViewById(R.id.btnCadastrarCartao)
        txtMensagem = findViewById(R.id.txtMensagemMetodo)

        mostrarMensagemSeHouver()

        btnListarMetodos.setOnClickListener {
            startActivity(Intent(this, ListarMetodoPagamentoActivity::class.java))
        }

        btnCadastrarCartao.setOnClickListener {
            startActivity(Intent(this, CadastrarCartaoActivity::class.java))
        }
    }

    private fun mostrarMensagemSeHouver() {
        val mensagem = intent.getStringExtra("mensagem")
        if (!mensagem.isNullOrEmpty()) {
            txtMensagem.text = mensagem
            txtMensagem.visibility = View.VISIBLE

            // Esconde a mensagem após 1 segundo
            Handler(Looper.getMainLooper()).postDelayed({
                txtMensagem.visibility = View.GONE
            }, 1000)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent) // Atualiza a intent atual para pegar a nova mensagem
        mostrarMensagemSeHouver()
    }
}
