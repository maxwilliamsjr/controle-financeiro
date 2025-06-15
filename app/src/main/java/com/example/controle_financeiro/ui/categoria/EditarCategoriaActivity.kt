package com.example.controle_financeiro.ui.categoria

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.google.firebase.firestore.FirebaseFirestore

class EditarCategoriaActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editNomeCategoria: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button

    private var idCategoria: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_categoria)
        supportActionBar?.title = "Editar Categoria"

        firestore = FirebaseFirestore.getInstance()

        editNomeCategoria = findViewById(R.id.editNomeCategoria)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnExcluir = findViewById(R.id.btnExcluir)

        idCategoria = intent.getStringExtra("id") ?: ""
        val nome = intent.getStringExtra("nomeCategoria") ?: ""
        editNomeCategoria.setText(nome)

        btnSalvar.setOnClickListener {
            val nomeAtualizado = editNomeCategoria.text.toString().trim()
            if (nomeAtualizado.isEmpty()) {
                Toast.makeText(this, "Informe o nome da categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dadosAtualizados = mapOf(
                "id" to idCategoria,
                "nomeCategoria" to nomeAtualizado
            )

            firestore.collection("categorias")
                .document(idCategoria)
                .set(dadosAtualizados)
                .addOnSuccessListener {
                    Toast.makeText(this, "Categoria atualizada!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnExcluir.setOnClickListener {
            firestore.collection("categorias")
                .document(idCategoria)
                .delete()
                .addOnSuccessListener {
                    Toast.makeText(this, "Categoria excluída!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao excluir: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
