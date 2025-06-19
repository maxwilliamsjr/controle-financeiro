package com.example.controle_financeiro.ui.despesa

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.TextUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.example.controle_financeiro.model.Despesa
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditarDespesaActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var editNome: EditText
    private lateinit var editDescricao: EditText
    private lateinit var editValor: EditText
    private lateinit var editData: EditText
    private lateinit var autoCompleteCategoria: AutoCompleteTextView
    private lateinit var autoCompleteMetodoPagamento: AutoCompleteTextView
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button
    private lateinit var btnDropdownCategoria: ImageButton
    private lateinit var btnDropdownMetodo: ImageButton

    private var idDespesa: String? = null
    private val categorias = mutableListOf<String>()
    private val metodosPagamento = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_despesa)
        supportActionBar?.title = "Editar Despesa"

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        editNome = findViewById(R.id.editNome)
        editDescricao = findViewById(R.id.editDescricao)
        editValor = findViewById(R.id.editValor)
        editData = findViewById(R.id.editData)
        autoCompleteCategoria = findViewById(R.id.editCategoria)
        autoCompleteMetodoPagamento = findViewById(R.id.editMetodoPagamento)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnExcluir = findViewById(R.id.btnExcluir)

        // Os botões para dropdown na edição devem estar no layout para usar (adicione se não tiver)
        btnDropdownCategoria = findViewById(R.id.btnDropdownCategoria) // verifique se tem no xml
        btnDropdownMetodo = findViewById(R.id.btnDropdownMetodo)       // verifique se tem no xml

        idDespesa = intent.getStringExtra("ID_DESPESA")

        if (idDespesa == null) {
            Toast.makeText(this, "Erro ao carregar despesa", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        carregarCategorias()
        carregarMetodosPagamento()

        editData.inputType = android.text.InputType.TYPE_NULL
        editData.setOnClickListener { mostrarDatePicker() }

        btnDropdownCategoria.setOnClickListener { autoCompleteCategoria.showDropDown() }
        btnDropdownMetodo.setOnClickListener { autoCompleteMetodoPagamento.showDropDown() }

        carregarDespesa()

        btnSalvar.setOnClickListener { atualizarDespesa() }
        btnExcluir.setOnClickListener { excluirDespesa() }
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

    private fun carregarDespesa() {
        firestore.collection("despesas").document(idDespesa!!)
            .get()
            .addOnSuccessListener { doc ->
                val despesa = doc.toObject(Despesa::class.java)
                val userId = auth.currentUser?.uid
                if (despesa != null && despesa.userId == userId) {
                    editNome.setText(despesa.nome)
                    editDescricao.setText(despesa.descricao)
                    editValor.setText(despesa.valor.toString())
                    editData.setText(despesa.data)
                    autoCompleteCategoria.setText(despesa.categoria, false)
                    autoCompleteMetodoPagamento.setText(despesa.metodoPagamento, false)
                } else {
                    Toast.makeText(this, "Despesa não encontrada ou acesso negado", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar: ${it.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun atualizarDespesa() {
        val userId = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        val nome = editNome.text.toString().trim()
        val descricao = editDescricao.text.toString().trim()
        val valor = editValor.text.toString().toDoubleOrNull() ?: 0.0
        val data = editData.text.toString().trim()
        val categoria = autoCompleteCategoria.text.toString().trim()
        val metodo = autoCompleteMetodoPagamento.text.toString().trim()

        if (nome.isBlank() || valor <= 0.0 || TextUtils.isEmpty(data) || categoria.isBlank() || metodo.isBlank()) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios corretamente", Toast.LENGTH_SHORT).show()
            return
        }

        if (!categorias.contains(categoria)) {
            Toast.makeText(this, "Categoria não cadastrada", Toast.LENGTH_SHORT).show()
            return
        }

        if (!metodosPagamento.contains(metodo)) {
            Toast.makeText(this, "Método de pagamento não cadastrado", Toast.LENGTH_SHORT).show()
            return
        }

        val novaDespesa = Despesa(
            id = idDespesa!!,
            nome = nome,
            descricao = descricao,
            valor = valor,
            data = data,
            categoria = categoria,
            metodoPagamento = metodo,
            userId = userId
        )

        firestore.collection("despesas").document(idDespesa!!)
            .set(novaDespesa)
            .addOnSuccessListener {
                Toast.makeText(this, "Despesa atualizada", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao atualizar: ${it.message}", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Erro ao excluir: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
