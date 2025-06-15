package com.example.controle_financeiro.ui.metodopagamento

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.MetodoPagamento
import com.google.firebase.firestore.FirebaseFirestore

class ListarMetodoPagamentoActivity : AppCompatActivity() {

    private lateinit var listViewMetodos: ListView
    private val firestore = FirebaseFirestore.getInstance()
    private val listaMetodos = mutableListOf<MetodoPagamento>()
    private lateinit var adapter: ArrayAdapter<String>
    private val listaNomesMetodos = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_metodos_pagamento)
        supportActionBar?.title = "Métodos de Pagamento"

        listViewMetodos = findViewById(R.id.listViewMetodos)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaNomesMetodos)
        listViewMetodos.adapter = adapter

        carregarMetodos()

        listViewMetodos.setOnItemClickListener { _, _, position, _ ->
            val metodoSelecionado = listaMetodos[position]
            val intent = Intent(this, EditarMetodoPagamentoActivity::class.java).apply {
                putExtra("id", metodoSelecionado.id)
                putExtra("nomeMetodo", metodoSelecionado.nome)
            }
            startActivity(intent)
        }
    }

    private fun carregarMetodos() {
        firestore.collection("metodosPagamento")
            .get()
            .addOnSuccessListener { resultado ->
                listaMetodos.clear()
                listaNomesMetodos.clear()
                for (documento in resultado) {
                    val id = documento.getString("id") ?: ""
                    val nome = documento.getString("nomeMetodo") ?: ""
                    val metodo = MetodoPagamento(id, nome)
                    listaMetodos.add(metodo)
                    listaNomesMetodos.add(nome)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar métodos: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
