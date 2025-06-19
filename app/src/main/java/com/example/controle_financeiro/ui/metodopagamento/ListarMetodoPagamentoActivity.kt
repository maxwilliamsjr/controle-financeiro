package com.example.controle_financeiro.ui.metodopagamento

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.Cartao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class ListarMetodoPagamentoActivity : AppCompatActivity() {

    private lateinit var listViewMetodos: ListView
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
                val cartaoSelecionado = listaCartoes.getOrNull(position - metodosFixos.size)
                if (cartaoSelecionado != null) {
                    val intent = Intent(this, EditarCartaoActivity::class.java).apply {
                        putExtra("id", cartaoSelecionado.id)
                        putExtra("nome", cartaoSelecionado.nome)
                        putExtra("banco", cartaoSelecionado.banco)
                        putExtra("tipo", cartaoSelecionado.tipo)
                        putExtra("bandeira", cartaoSelecionado.bandeira)
                        putExtra("vencimento", cartaoSelecionado.vencimento)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Erro: cartão não encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        carregarMetodos()
    }

    private fun carregarMetodos() {
        val userId = auth.currentUser?.uid ?: return

        firestore.collection("cartoes")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { resultado ->
                listaCartoes.clear()
                listaMetodosExibicao.clear()

                listaMetodosExibicao.addAll(metodosFixos)

                for (documento in resultado) {
                    val id = documento.getString("id") ?: continue
                    val nome = documento.getString("nome") ?: continue
                    val banco = documento.getString("banco") ?: ""
                    val tipo = documento.getString("tipo") ?: "Crédito"
                    val bandeira = documento.getString("bandeira") ?: "Visa"
                    val vencimento = documento.getString("vencimento") ?: ""
                    val dataFechamento = documento.getLong("dataFechamento")?.toInt() ?: 0

                    val cartao = Cartao(id, nome, banco, tipo, vencimento, bandeira, 0.0, dataFechamento)
                    listaCartoes.add(cartao)

                    val display = "$tipo $banco $nome $bandeira"
                    listaMetodosExibicao.add(display)
                }

                adapter.notifyDataSetChanged()

                verificarAtualizacaoDataFechamento()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar cartões: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun verificarAtualizacaoDataFechamento() {
        val hoje = Calendar.getInstance()

        for (cartao in listaCartoes) {
            val fechamentoDia = cartao.dataFechamento
            if (fechamentoDia == 0) {
                mostrarDialogAtualizarDataFechamento(cartao)
                break
            } else {
                // Verificar se o fechamento já passou neste mês
                val fechamentoData = Calendar.getInstance()
                fechamentoData.set(Calendar.DAY_OF_MONTH, fechamentoDia)

                // Se hoje é maior ou igual ao fechamento, considerar para o próximo mês (atualizar)
                if (hoje.get(Calendar.DAY_OF_MONTH) >= fechamentoDia) {
                    mostrarDialogAtualizarDataFechamento(cartao)
                    break
                }
            }
        }
    }

    private fun mostrarDialogAtualizarDataFechamento(cartao: Cartao) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Atualizar data de fechamento!")
        builder.setMessage("Informe o dia do mês para fechamento da fatura do cartão do próximo mês.\n\"${cartao.tipo} ${cartao.nome} ${cartao.bandeira} ${cartao.banco}\":")

        // Usar DatePickerDialog em vez do EditText para escolher dia

        val hoje = Calendar.getInstance()
        val dialogDatePicker = DatePickerDialog(
            this,
            { _, _, _, dayOfMonth ->
                atualizarDataFechamento(cartao, dayOfMonth)
            },
            hoje.get(Calendar.YEAR),
            hoje.get(Calendar.MONTH),
            hoje.get(Calendar.DAY_OF_MONTH)
        )
        // Opcional: Limitar seleção para dias 1 a 31 (não obrigatório, DatePicker já faz)

        dialogDatePicker.show()
    }

    private fun atualizarDataFechamento(cartao: Cartao, novoDia: Int) {
        if (novoDia !in 1..31) {
            Toast.makeText(this, "Dia inválido.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("cartoes").document(cartao.id)
            .update("dataFechamento", novoDia)
            .addOnSuccessListener {
                Toast.makeText(this, "Data de fechamento atualizada.", Toast.LENGTH_SHORT).show()
                carregarMetodos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar data: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
