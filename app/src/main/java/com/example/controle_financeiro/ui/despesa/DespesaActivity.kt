package com.example.controle_financeiro.ui.despesa

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.Despesa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.NumberFormat
import java.util.*

class DespesaActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var editNome: EditText
    private lateinit var editDescricao: EditText
    private lateinit var editValor: EditText
    private lateinit var editData: EditText
    private lateinit var autoCompleteCategoria: AutoCompleteTextView
    private lateinit var autoCompleteMetodoPagamento: AutoCompleteTextView
    private lateinit var btnSalvar: Button
    private lateinit var btnAddCategoria: ImageButton
    private lateinit var btnAddMetodoPagamento: ImageButton
    private lateinit var btnDropdownCategoria: ImageButton
    private lateinit var btnDropdownMetodo: ImageButton
    private lateinit var btnCancelar: Button

    private val categorias = mutableListOf<String>()
    private val metodosPagamento = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_despesa)
        supportActionBar?.title = "Cadastrar Despesa"

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        editNome = findViewById(R.id.editNome)
        editDescricao = findViewById(R.id.editDescricao)
        editValor = findViewById(R.id.editValor)
        editData = findViewById(R.id.editData)
        autoCompleteCategoria = findViewById(R.id.autoCompleteCategoria)
        autoCompleteMetodoPagamento = findViewById(R.id.autoCompleteMetodoPagamento)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnAddCategoria = findViewById(R.id.btnAddCategoria)
        btnAddMetodoPagamento = findViewById(R.id.btnAddMetodoPagamento)
        btnDropdownCategoria = findViewById(R.id.btnDropdownCategoria)
        btnDropdownMetodo = findViewById(R.id.btnDropdownMetodo)
        btnCancelar = findViewById(R.id.btnCancelar)

        editData.inputType = android.text.InputType.TYPE_NULL
        editData.setOnClickListener { mostrarDatePicker() }

        carregarCategorias()
        carregarMetodosPagamento()

        // Configura dropdown manual para categorias
        btnDropdownCategoria.setOnClickListener {
            autoCompleteCategoria.showDropDown()
        }

        // Configura dropdown manual para métodos de pagamento
        btnDropdownMetodo.setOnClickListener {
            autoCompleteMetodoPagamento.showDropDown()
        }

        // Botão adicionar categoria leva para cadastro de categoria
        btnAddCategoria.setOnClickListener {
            startActivity(Intent(this, com.example.controle_financeiro.ui.categoria.CategoriaActivity::class.java))
        }

        // Botão adicionar método leva para cadastro de método de pagamento
        btnAddMetodoPagamento.setOnClickListener {
            startActivity(Intent(this, com.example.controle_financeiro.ui.metodopagamento.CadastrarCartaoActivity::class.java))
        }

        // Formatação do valor com vírgula e milhar enquanto digita
        editValor.addTextChangedListener(object : TextWatcher {
            private var current = ""
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s.toString() != current) {
                    editValor.removeTextChangedListener(this)

                    val cleanString = s.toString().replace("[R$,.\\s]".toRegex(), "")
                    if (cleanString.isNotEmpty()) {
                        val parsed = cleanString.toDouble() / 100
                        val formatted = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(parsed)
                        current = formatted
                        editValor.setText(formatted)
                        editValor.setSelection(formatted.length)
                    } else {
                        current = ""
                        editValor.setText("")
                    }

                    editValor.addTextChangedListener(this)
                }
            }
        })

        btnSalvar.setOnClickListener {
            salvarDespesa()
        }

        btnCancelar.setOnClickListener {
            Toast.makeText(this, "Cadastro cancelado", Toast.LENGTH_SHORT).show()
            finish()
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
            val dataFormatada = String.format("%04d-%02d-%02d", a, m + 1, d)
            editData.setText(dataFormatada)
        }, ano, mes, dia).show()
    }

    private fun carregarCategorias() {
        firestore.collection("categorias")
            .get()
            .addOnSuccessListener { result ->
                categorias.clear()
                categorias.addAll(result.documents.mapNotNull { it.getString("nomeCategoria") })
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categorias)
                autoCompleteCategoria.setAdapter(adapter)
            }
    }

    private fun carregarMetodosPagamento() {
        firestore.collection("metodosPagamento")
            .get()
            .addOnSuccessListener { result ->
                metodosPagamento.clear()
                metodosPagamento.addAll(result.documents.mapNotNull { it.getString("nomeMetodo") })
                val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, metodosPagamento)
                autoCompleteMetodoPagamento.setAdapter(adapter)
            }
    }

    private fun salvarDespesa() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val nome = editNome.text.toString().trim()
        val descricao = editDescricao.text.toString().trim()
        val valorStr = editValor.text.toString().replace("[R$,.\\s]".toRegex(), "").trim()
        val data = editData.text.toString().trim()
        val categoria = autoCompleteCategoria.text.toString().trim()
        val metodo = autoCompleteMetodoPagamento.text.toString().trim()

        // Validar valor convertido para double
        val valor = valorStr.toDoubleOrNull()?.div(100)

        // Verificações
        if (nome.isBlank() || valor == null || valor <= 0.0 || data.isBlank() || categoria.isBlank() || metodo.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios corretamente", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar se categoria existe na lista cadastrada
        if (!categorias.contains(categoria)) {
            Toast.makeText(this, "Categoria não cadastrada", Toast.LENGTH_SHORT).show()
            return
        }

        // Verificar se método existe na lista cadastrada
        if (!metodosPagamento.contains(metodo)) {
            Toast.makeText(this, "Método de pagamento não cadastrado", Toast.LENGTH_SHORT).show()
            return
        }

        val id = UUID.randomUUID().toString()
        val despesa = Despesa(id, nome, descricao, valor, data, categoria, metodo, userId)

        firestore.collection("despesas")
            .document(id)
            .set(despesa)
            .addOnSuccessListener {
                Toast.makeText(this, "Despesa cadastrada com sucesso!", Toast.LENGTH_SHORT).show()
                limparCampos()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun limparCampos() {
        editNome.text.clear()
        editDescricao.text.clear()
        editValor.text.clear()
        editData.text = null
        autoCompleteCategoria.text.clear()
        autoCompleteMetodoPagamento.text.clear()
    }
}
