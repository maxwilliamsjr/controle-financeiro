package com.example.controle_financeiro.ui.metodopagamento

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R

class MenuMetodoPagamentoActivity : AppCompatActivity() {

    private lateinit var btnListarMetodos: Button
    private lateinit var btnCadastrarMetodo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_metodo_pagamento)
        supportActionBar?.title = "Menu Método de Pagamento"

        btnListarMetodos = findViewById(R.id.btnListarMetodos)
        btnCadastrarMetodo = findViewById(R.id.btnCadastrarMetodo)

        btnListarMetodos.setOnClickListener {
            startActivity(Intent(this, ListarMetodoPagamentoActivity::class.java))
        }

        btnCadastrarMetodo.setOnClickListener {
            startActivity(Intent(this, CadastrarCartaoActivity::class.java))
        }
    }
}
