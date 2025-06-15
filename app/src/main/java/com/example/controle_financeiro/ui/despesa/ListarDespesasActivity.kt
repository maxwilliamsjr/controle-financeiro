package com.example.controle_financeiro.ui.despesa

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.Despesa
import com.google.firebase.firestore.FirebaseFirestore

class ListarDespesasActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var adapter: ArrayAdapter<String>
    private val listaTexto = mutableListOf<String>()
    private val listaDespesas = mutableListOf<Pair<String, Despesa>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_despesas)
        supportActionBar?.title = "Lista de Despesas"

        listView = findViewById(R.id.listViewDespesas)
        firestore = FirebaseFirestore.getInstance()
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaTexto)
        listView.adapter = adapter

        carregarDespesas()

        listView.setOnItemClickListener { _, _, position, _ ->
            val (id, _) = listaDespesas[position]
            val intent = Intent(this, EditarDespesaActivity::class.java)
            intent.putExtra("ID_DESPESA", id)
            startActivity(intent)
        }
    }

    private fun carregarDespesas() {
        firestore.collection("despesas")
            .get()
            .addOnSuccessListener { result ->
                listaTexto.clear()
                listaDespesas.clear()
                for (doc in result) {
                    val despesa = doc.toObject(Despesa::class.java)
                    listaDespesas.add(Pair(doc.id, despesa))
                    val texto = "${despesa.nome} - R$ ${despesa.valor} em ${despesa.data}"
                    listaTexto.add(texto)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar despesas", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        carregarDespesas()
    }
}
