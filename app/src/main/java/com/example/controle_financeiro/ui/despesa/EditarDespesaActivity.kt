package com.example.controle_financeiro.ui.despesa

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.Despesa
import com.google.firebase.firestore.FirebaseFirestore

class EditarDespesaActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editDescricao: EditText
    private lateinit var editValor: EditText
    private lateinit var editData: EditText
    private lateinit var editCategoria: EditText
    private lateinit var editMetodo: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button

    private var idDespesa: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_despesa)
        supportActionBar?.title = "Editar Despesa"

        firestore = FirebaseFirestore.getInstance()

        editDescricao = findViewById(R.id.editDescricao)
        editValor = findViewById(R.id.editValor)
        editData = findViewById(R.id.editData)
        editCategoria = findViewById(R.id.editCategoria)
        editMetodo = findViewById(R.id.editMetodoPagamento)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnExcluir = findViewById(R.id.btnExcluir)

        idDespesa = intent.getStringExtra("ID_DESPESA")

        if (idDespesa == null) {
            Toast.makeText(this, "Erro ao carregar despesa", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarDespesa()

        btnSalvar.setOnClickListener { atualizarDespesa() }
        btnExcluir.setOnClickListener { excluirDespesa() }
    }

    private fun carregarDespesa() {
        firestore.collection("despesas").document(idDespesa!!)
            .get()
            .addOnSuccessListener { doc ->
                val despesa = doc.toObject(Despesa::class.java)
                if (despesa != null) {
                    editDescricao.setText(despesa.descricao)
                    editValor.setText(despesa.valor.toString())
                    editData.setText(despesa.data)
                    editCategoria.setText(despesa.categoria)
                    editMetodo.setText(despesa.metodoPagamento)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar: ${it.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun atualizarDespesa() {
        val novaDespesa = Despesa(
            id = idDespesa!!,
            descricao = editDescricao.text.toString(),
            valor = editValor.text.toString().toDoubleOrNull() ?: 0.0,
            data = editData.text.toString(),
            categoria = editCategoria.text.toString(),
            metodoPagamento = editMetodo.text.toString()
        )

        firestore.collection("despesas").document(idDespesa!!)
            .set(novaDespesa)
            .addOnSuccessListener {
                Toast.makeText(this, "Despesa atualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun excluirDespesa() {
        firestore.collection("despesas").document(idDespesa!!)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Despesa excluída", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
