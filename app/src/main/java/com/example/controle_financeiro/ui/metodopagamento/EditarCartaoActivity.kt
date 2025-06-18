package com.example.controle_financeiro.ui.metodopagamento

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.google.firebase.firestore.FirebaseFirestore

class EditarCartaoActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editNomeCartao: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button

    private var idMetodo: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_cartao)
        supportActionBar?.title = "Editar Cartão"

        firestore = FirebaseFirestore.getInstance()

        editNomeCartao = findViewById(R.id.editNomeCartao)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnExcluir = findViewById(R.id.btnExcluir)

        // Recebe dados enviados pelo Intent
        idMetodo = intent.getStringExtra("id") ?: ""
        val nome = intent.getStringExtra("nome") ?: ""

        editNomeCartao.setText(nome)

        btnSalvar.setOnClickListener {
            val nomeAtualizado = editNomeCartao.text.toString().trim()

            if (nomeAtualizado.isEmpty()) {
                Toast.makeText(this, "Informe o nome do cartão", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSalvar.isEnabled = false

            val dadosAtualizados = hashMapOf(
                "nome" to nomeAtualizado
            )

            firestore.collection("cartoes")
                .document(idMetodo)
                .update(dadosAtualizados as Map<String, Any>)
                .addOnSuccessListener {
                    runOnUiThread {
                        Toast.makeText(this, "Cartão atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                        btnSalvar.isEnabled = true
                        finish()
                    }
                }
                .addOnFailureListener {
                    runOnUiThread {
                        Toast.makeText(this, "Erro ao atualizar: ${it.message}", Toast.LENGTH_SHORT).show()
                        btnSalvar.isEnabled = true
                    }
                }
        }

        btnExcluir.setOnClickListener {
            if (idMetodo.isEmpty()) {
                Toast.makeText(this, "ID do cartão inválido para exclusão.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnExcluir.isEnabled = false

            firestore.collection("cartoes")
                .document(idMetodo)
                .delete()
                .addOnSuccessListener {
                    runOnUiThread {
                        Toast.makeText(this, "Cartão excluído com sucesso!", Toast.LENGTH_SHORT).show()
                        btnExcluir.isEnabled = true
                        finish()
                    }
                }
                .addOnFailureListener {
                    runOnUiThread {
                        Toast.makeText(this, "Erro ao excluir: ${it.message}", Toast.LENGTH_SHORT).show()
                        btnExcluir.isEnabled = true
                    }
                }
        }
    }
}
