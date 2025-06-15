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
    private lateinit var despesasAdapter: ArrayAdapter<String>
    private val listaDespesasTexto = mutableListOf<String>()
    private val listaDespesas = mutableListOf<Pair<String, Despesa>>() // ID + dados

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_despesas)
        supportActionBar?.title = "Lista de Despesas"

        listView = findViewById(R.id.listViewDespesas)
        firestore = FirebaseFirestore.getInstance()

        despesasAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaDespesasTexto)
        listView.adapter = despesasAdapter

        carregarDespesas()

        // Clique em item da lista
        listView.setOnItemClickListener { _, _, position, _ ->
            val (idDespesa, despesaSelecionada) = listaDespesas[position]
            val intent = Intent(this, EditarDespesaActivity::class.java)
            intent.putExtra("ID_DESPESA", idDespesa)
            startActivity(intent)
        }
    }

    private fun carregarDespesas() {
        firestore.collection("despesas")
            .get()
            .addOnSuccessListener { result ->
                listaDespesas.clear()
                listaDespesasTexto.clear()
                for (doc in result) {
                    val despesa = doc.toObject(Despesa::class.java)
                    val id = doc.id
                    listaDespesas.add(Pair(id, despesa))
                    val texto = "${despesa.descricao} - R$ ${despesa.valor} em ${despesa.data}"
                    listaDespesasTexto.add(texto)
                }
                despesasAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar despesas: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        carregarDespesas() // Atualiza ao voltar da tela de edição
    }
}
