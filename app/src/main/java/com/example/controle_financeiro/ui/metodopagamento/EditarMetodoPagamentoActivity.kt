package com.example.controle_financeiro.ui.metodopagamento

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.google.firebase.firestore.FirebaseFirestore

class EditarMetodoPagamentoActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editNomeMetodo: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button

    private var idMetodo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_metodo_pagamento)
        supportActionBar?.title = "Editar Método de Pagamento"

        firestore = FirebaseFirestore.getInstance()

        editNomeMetodo = findViewById(R.id.editNomeMetodo)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnExcluir = findViewById(R.id.btnExcluir)

        // Recebendo dados da Intent
        idMetodo = intent.getStringExtra("id") ?: ""
        val nome = intent.getStringExtra("nomeMetodo") ?: ""
        editNomeMetodo.setText(nome)

        btnSalvar.setOnClickListener {
            val nomeAtualizado = editNomeMetodo.text.toString().trim()
            if (nomeAtualizado.isEmpty()) {
                Toast.makeText(this, "Informe o nome do método", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dadosAtualizados = hashMapOf(
                "id" to idMetodo,
                "nomeMetodo" to nomeAtualizado
            )

            firestore.collection("metodosPagamento")
                .document(idMetodo)
                .set(dadosAtualizados)
                .addOnSuccessListener {
                    Toast.makeText(this, "Método atualizado!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnExcluir.setOnClickListener {
            firestore.collection("metodosPagamento")
                .document(idMetodo)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Método excluído!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao excluir: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
