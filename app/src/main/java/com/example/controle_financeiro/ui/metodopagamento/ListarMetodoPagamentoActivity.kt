package com.example.controle_financeiro.ui.metodopagamento

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.Cartao
import com.google.firebase.firestore.FirebaseFirestore

class ListarMetodoPagamentoActivity : AppCompatActivity() {

    private lateinit var listViewMetodos: ListView
    private val firestore = FirebaseFirestore.getInstance()

    private val metodosFixos = listOf("Pix", "Boleto", "Transferência", "Dinheiro", "Débito automático")
    private val listaMetodosExibicao = mutableListOf<String>()
    private val listaCartoes = mutableListOf<Cartao>()

    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_metodos_pagamento)
        supportActionBar?.title = "Métodos de Pagamento"

        listViewMetodos = findViewById(R.id.listViewMetodos)
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaMetodosExibicao)
        listViewMetodos.adapter = adapter

        carregarMetodos()

        listViewMetodos.setOnItemClickListener { _, _, position, _ ->
            val itemSelecionado = listaMetodosExibicao[position]

            if (metodosFixos.contains(itemSelecionado)) {
                Toast.makeText(this, "'$itemSelecionado' é um método fixo e não pode ser editado", Toast.LENGTH_SHORT).show()
            } else {
                val cartaoSelecionado = listaCartoes.find { it.nome == itemSelecionado }
                if (cartaoSelecionado != null) {
                    val intent = Intent(this, EditarCartaoActivity::class.java).apply {
                        putExtra("id", cartaoSelecionado.id)
                        putExtra("nome", cartaoSelecionado.nome)
                        putExtra("banco", cartaoSelecionado.banco)
                        putExtra("tipo", cartaoSelecionado.tipo)
                        putExtra("vencimento", cartaoSelecionado.vencimento)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Erro: cartão não encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun carregarMetodos() {
        firestore.collection("cartoes")
            .get()
            .addOnSuccessListener { resultado ->
                listaCartoes.clear()
                listaMetodosExibicao.clear()

                listaMetodosExibicao.addAll(metodosFixos)

                for (documento in resultado) {
                    val id = documento.getString("id") ?: ""
                    val nome = documento.getString("nome") ?: ""
                    val banco = documento.getString("banco") ?: ""
                    val tipo = documento.getString("tipo") ?: "Crédito"
                    val vencimento = documento.getString("vencimento") ?: ""

                    val cartao = Cartao(id, nome, banco, tipo, vencimento)
                    listaCartoes.add(cartao)
                    listaMetodosExibicao.add(nome)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar cartões: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
