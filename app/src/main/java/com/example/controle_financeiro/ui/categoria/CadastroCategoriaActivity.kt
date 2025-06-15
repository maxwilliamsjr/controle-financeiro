package com.example.controle_financeiro.ui.categoria

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CadastroCategoriaActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editNomeCategoria: EditText
    private lateinit var btnSalvarCategoria: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_categoria)
        supportActionBar?.title = "Cadastrar Categoria"

        firestore = FirebaseFirestore.getInstance()
        editNomeCategoria = findViewById(R.id.editNomeCategoria)
        btnSalvarCategoria = findViewById(R.id.btnSalvarCategoria)

        btnSalvarCategoria.setOnClickListener {
            val nome = editNomeCategoria.text.toString().trim()
            if (nome.isBlank()) {
                Toast.makeText(this, "Informe o nome da categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val id = UUID.randomUUID().toString()
            val categoria = hashMapOf("nomeCategoria" to nome)

            firestore.collection("categorias")
                .document(id)
                .set(categoria)
                .addOnSuccessListener {
                    Toast.makeText(this, "Categoria salva com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar categoria: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
