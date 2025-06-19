package com.example.controle_financeiro.ui.metodopagamento

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class EditarCartaoActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editNomeCartao: EditText
    private lateinit var editBancoCartao: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerBandeira: Spinner
    private lateinit var editVencimento: EditText
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button
    private lateinit var btnCancelar: Button

    private var idMetodo: String = ""
    private var tipoSelecionado = "Crédito"
    private var bandeiraSelecionada = "Visa"
    private var diaVencimentoSelecionado: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_cartao)
        supportActionBar?.title = "Editar Cartão"

        firestore = FirebaseFirestore.getInstance()

        editNomeCartao = findViewById(R.id.editNomeCartao)
        editBancoCartao = findViewById(R.id.editBancoCartao)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        spinnerBandeira = findViewById(R.id.spinnerBandeira)
        editVencimento = findViewById(R.id.editVencimento)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnExcluir = findViewById(R.id.btnExcluir)
        btnCancelar = findViewById(R.id.btnCancelarCartao)

        val tipos = listOf("Crédito", "Débito")
        val bandeiras = listOf("Visa", "Mastercard", "Elo", "American Express", "Hipercard")

        spinnerTipo.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinnerBandeira.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, bandeiras).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        idMetodo = intent.getStringExtra("id") ?: ""
        val nome = intent.getStringExtra("nome") ?: ""
        val banco = intent.getStringExtra("banco") ?: ""
        val tipo = intent.getStringExtra("tipo") ?: "Crédito"
        val bandeira = intent.getStringExtra("bandeira") ?: "Visa"
        val vencimento = intent.getStringExtra("vencimento") ?: ""

        editNomeCartao.setText(nome)
        editBancoCartao.setText(banco)

        diaVencimentoSelecionado = vencimento.toIntOrNull()
        editVencimento.setText(diaVencimentoSelecionado?.toString() ?: "")

        spinnerTipo.setSelection(tipos.indexOf(tipo))
        spinnerBandeira.setSelection(bandeiras.indexOf(bandeira))

        // Impedir edição manual do vencimento e abrir DatePicker
        editVencimento.inputType = 0
        editVencimento.isFocusable = false
        editVencimento.setOnClickListener {
            mostrarDatePicker()
        }

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                tipoSelecionado = tipos[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerBandeira.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                bandeiraSelecionada = bandeiras[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnSalvar.setOnClickListener {
            val nomeAtualizado = editNomeCartao.text.toString().trim()
            val bancoAtualizado = editBancoCartao.text.toString().trim()
            val vencimento = diaVencimentoSelecionado

            if (nomeAtualizado.isEmpty() || bancoAtualizado.isEmpty() || vencimento == null) {
                mostrarDialog("Atenção", "Preencha todos os campos e selecione a data de vencimento.")
                return@setOnClickListener
            }

            if (!isVencimentoValido(vencimento)) {
                mostrarDialog("Vencimento inválido", "O dia informado não é válido para o mês atual.")
                return@setOnClickListener
            }

            val dadosAtualizados = mapOf(
                "nome" to nomeAtualizado,
                "banco" to bancoAtualizado,
                "tipo" to tipoSelecionado,
                "bandeira" to bandeiraSelecionada,
                "vencimento" to vencimento.toString()
            )

            firestore.collection("cartoes")
                .document(idMetodo)
                .update(dadosAtualizados)
                .addOnSuccessListener {
                    mostrarDialogComRetorno("Sucesso", "Cartão atualizado com sucesso!")
                }
                .addOnFailureListener {
                    mostrarDialog("Erro", "Erro ao atualizar: ${it.message}")
                }
        }

        btnExcluir.setOnClickListener {
            firestore.collection("cartoes")
                .document(idMetodo)
                .delete()
                .addOnSuccessListener {
                    mostrarDialogComRetorno("Sucesso", "Cartão excluído com sucesso!")
                }
                .addOnFailureListener {
                    mostrarDialog("Erro", "Erro ao excluir: ${it.message}")
                }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }

    private fun mostrarDatePicker() {
        val hoje = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, _, _, dayOfMonth ->
                diaVencimentoSelecionado = dayOfMonth
                editVencimento.setText(dayOfMonth.toString())
            },
            hoje.get(Calendar.YEAR),
            hoje.get(Calendar.MONTH),
            hoje.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun isVencimentoValido(dia: Int): Boolean {
        if (dia <= 0 || dia >= 32) return false
        val calendario = Calendar.getInstance()
        val ultimoDia = calendario.getActualMaximum(Calendar.DAY_OF_MONTH)
        return dia <= ultimoDia
    }

    private fun mostrarDialog(titulo: String, mensagem: String) {
        AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setCancelable(true)
            .setPositiveButton("OK", null)
            .show()
    }

    private fun mostrarDialogComRetorno(titulo: String, mensagem: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle(titulo)
            .setMessage(mensagem)
            .setCancelable(false)
            .create()

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            val intent = Intent(this, MenuMetodoPagamentoActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }, 2000)
    }
}
