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
        supportActionBar?.title = "Gerenciar Despesas"

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

        // Desabilita teclado para editData e abre DatePicker ao clicar
        editData.inputType = android.text.InputType.TYPE_NULL
        editData.setOnClickListener {
            mostrarDatePicker()
        }

        btnSalvar.setOnClickListener {
            val id = UUID.randomUUID().toString()
            val nome = editNome.text.toString()
            val descricao = editDescricao.text.toString()
            val valor = editValor.text.toString().toDoubleOrNull()
            val data = editData.text.toString()
            val categoria = autoCompleteCategoria.text.toString()
            val metodo = autoCompleteMetodoPagamento.text.toString()

            if (nome.isBlank() || valor == null || valor <= 0.0 || data.isBlank() || categoria.isBlank() || metodo.isBlank()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios corretamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val despesa = Despesa(id, descricao, valor, data, categoria, metodo)

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

        // Clique no botão + categoria abre tela para cadastrar categoria
        btnAddCategoria.setOnClickListener {
            startActivity(Intent(this, CadastroCategoriaActivity::class.java))
        }

        // Clique no botão + método abre tela para cadastrar método de pagamento
        btnAddMetodoPagamento.setOnClickListener {
            startActivity(Intent(this, CadastroMetodoPagamentoActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Recarregar listas caso tenha cadastrado algo novo
        carregarCategorias()
        carregarMetodosPagamento()
    }

    private fun mostrarDatePicker() {
        val calendario = Calendar.getInstance()
        val ano = calendario.get(Calendar.YEAR)
        val mes = calendario.get(Calendar.MONTH)
        val dia = calendario.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, { _, anoSelecionado, mesSelecionado, diaSelecionado ->
            val mesFormatado = (mesSelecionado + 1).toString().padStart(2, '0')
            val diaFormatado = diaSelecionado.toString().padStart(2, '0')
            val dataFormatada = "$anoSelecionado-$mesFormatado-$diaFormatado"
            editData.setText(dataFormatada)
        }, ano, mes, dia)

        dpd.show()
    }

    private fun carregarCategorias() {
        firestore.collection("categorias")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { it.getString("nomeCategoria") }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lista)
                autoCompleteCategoria.setAdapter(adapter)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar categorias", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarMetodosPagamento() {
        firestore.collection("metodosPagamento")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.mapNotNull { it.getString("nomeMetodo") }
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, lista)
                autoCompleteMetodoPagamento.setAdapter(adapter)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar métodos de pagamento", Toast.LENGTH_SHORT).show()
            }
    }
}
