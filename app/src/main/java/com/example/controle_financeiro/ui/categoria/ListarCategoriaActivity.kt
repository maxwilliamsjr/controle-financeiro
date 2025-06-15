package com.example.controle_financeiro.ui.categoria

import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.Categoria
import com.google.firebase.firestore.FirebaseFirestore

class ListarCategoriaActivity : AppCompatActivity() {

    private lateinit var listViewCategorias: ListView
    private lateinit var firestore: FirebaseFirestore
    private lateinit var listaCategorias: MutableList<Categoria>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_categorias)
        supportActionBar?.title = "Lista de Categorias"

        listViewCategorias = findViewById(R.id.listViewCategorias)
        firestore = FirebaseFirestore.getInstance()
        listaCategorias = mutableListOf()

        carregarCategorias()
    }

    private fun carregarCategorias() {
        firestore.collection("categorias")
            .get()
            .addOnSuccessListener { result ->
                listaCategorias.clear()
                for (document in result) {
                    val id = document.id
                    val nome = document.getString("nomeCategoria") ?: ""
                    val categoria = Categoria(id = id, nome = nome)
                    listaCategorias.add(categoria)
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_list_item_1,
                    listaCategorias.map { it.nome }
                )
                listViewCategorias.adapter = adapter

                listViewCategorias.setOnItemClickListener { _: AdapterView<*>, _, position: Int, _ ->
                    val categoriaSelecionada = listaCategorias[position]
                    val intent = Intent(this, EditarCategoriaActivity::class.java).apply {
                        putExtra("id", categoriaSelecionada.id)
                        putExtra("nomeCategoria", categoriaSelecionada.nome)
                    }
                    startActivity(intent)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
