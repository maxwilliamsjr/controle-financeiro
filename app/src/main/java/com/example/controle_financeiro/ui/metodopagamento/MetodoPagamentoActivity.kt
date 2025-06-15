package com.example.controle_financeiro.ui.metodopagamento

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MetodoPagamentoActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editNomeMetodo: EditText
    private lateinit var btnSalvarMetodo: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_metodo_pagamento)
        supportActionBar?.title = "Cadastrar Método de Pagamento"

        firestore = FirebaseFirestore.getInstance()

        editNomeMetodo = findViewById(R.id.editNomeMetodo)
        btnSalvarMetodo = findViewById(R.id.btnSalvarMetodo)

        btnSalvarMetodo.setOnClickListener {
            val nome = editNomeMetodo.text.toString().trim()
            if (nome.isEmpty()) {
                Toast.makeText(this, "Informe o nome do método", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = UUID.randomUUID().toString()
            val metodo = hashMapOf(
                "id" to id,
                "nomeMetodo" to nome
            )

            firestore.collection("metodosPagamento")
                .document(id)
                .set(metodo)
                .addOnSuccessListener {
                    Toast.makeText(this, "Método salvo com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar método: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
