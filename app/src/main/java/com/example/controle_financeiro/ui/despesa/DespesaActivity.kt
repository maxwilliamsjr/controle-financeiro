package com.example.controle_financeiro.ui.despesa

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.Despesa
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class DespesaActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore

    private lateinit var editNome: EditText
    private lateinit var editDescricao: EditText
    private lateinit var editValor: EditText
    private lateinit var editData: EditText
    private lateinit var autoCompleteCategoria: AutoCompleteTextView
    private lateinit var autoCompleteMetodoPagamento: AutoCompleteTextView
    private lateinit var btnSalvar: Button
    private lateinit var btnAddCategoria: ImageButton
    private lateinit var btnAddMetodoPagamento: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_despesa)
        supportActionBar?.title = "Cadastrar Despesa"

        firestore = FirebaseFirestore.getInstance()

        editNome = findViewById(R.id.editNome)
        editDescricao = findViewById(R.id.editDescricao)
        editValor = findViewById(R.id.editValor)
        editData = findViewById(R.id.editData)
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria)
        autoCompleteMetodoPagamento = findViewById(R.id.autoCompleteMetodoPagamento)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnAddCategoria = findViewById(R.id.btnAddCategoria)
        btnAddMetodoPagamento = findViewById(R.id.btnAddMetodoPagamento)

        carregarCategorias()
        carregarMetodosPagamento()

        editData.inputType = android.text.InputType.TYPE_NULL
        editData.setOnClickListener { mostrarDatePicker() }

        btnSalvar.setOnClickListener {
            val id = UUID.randomUUID().toString()
            val nome = editNome.text.toString().trim()
            val descricao = editDescricao.text.toString().trim()
            val valor = editValor.text.toString().toDoubleOrNull()
            val data = editData.text.toString().trim()
            val categoria = autoCompleteCategoria.text.toString().trim()
            val metodo = autoCompleteMetodoPagamento.text.toString().trim()

            if (nome.isBlank() || valor == null || valor <= 0.0 || data.isBlank() || categoria.isBlank() || metodo.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios corretamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val despesa = Despesa(id, nome, descricao, valor, data, categoria, metodo)

            firestore.collection("despesas")
                .document(id)
                .set(despesa)
                .addOnSuccessListener {
                    Toast.makeText(this, "Despesa salva com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        btnAddCategoria.setOnClickListener {
            startActivity(Intent(this, com.example.controle_financeiro.ui.categoria.CategoriaActivity::class.java))
        }

        btnAddMetodoPagamento.setOnClickListener {
            startActivity(Intent(this, com.example.controle_financeiro.ui.metodopagamento.CadastrarCartaoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        carregarCategorias()
        carregarMetodosPagamento()
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(this, { _, a, m, d ->
            val data = String.format("%04d-%02d-%02d", a, m + 1, d)
            editData.setText(data)
        }, ano, mes, dia).show()
    }

    private fun carregarCategorias() {
        firestore.collection("categorias")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { it.getString("nomeCategoria") }
                autoCompleteCategoria.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lista))
            }
    }

    private fun carregarMetodosPagamento() {
        firestore.collection("metodosPagamento")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { it.getString("nomeMetodo") }
                autoCompleteMetodoPagamento.setAdapter(ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lista))
            }
    }
}
