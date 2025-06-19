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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CadastrarCartaoActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private lateinit var editNomeCartao: EditText
    private lateinit var editBancoCartao: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var spinnerBandeira: Spinner
    private lateinit var editVencimento: EditText
    private lateinit var btnSalvarCartao: Button
    private lateinit var btnCancelar: Button

    private var tipoSelecionado = "Crédito"
    private var bandeiraSelecionada = "Visa"
    private var diaVencimentoSelecionado: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_cartao)
        supportActionBar?.title = "Cadastrar Cartão"

        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        editNomeCartao = findViewById(R.id.editNomeCartao)
        editBancoCartao = findViewById(R.id.editBancoCartao)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        spinnerBandeira = findViewById(R.id.spinnerBandeira)
        editVencimento = findViewById(R.id.editVencimento)
        btnSalvarCartao = findViewById(R.id.btnSalvarCartao)
        btnCancelar = findViewById(R.id.btnCancelarCartao)

        // Impedir edição manual do vencimento, abrir DatePicker
        editVencimento.inputType = 0
        editVencimento.isFocusable = false
        editVencimento.setOnClickListener {
            mostrarDatePicker()
        }

        val tipos = listOf("Crédito", "Débito")
        val adapterTipo = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapterTipo

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                tipoSelecionado = tipos[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        val bandeiras = listOf("Visa", "Mastercard", "Elo", "American Express", "Hipercard")
        val adapterBandeira = ArrayAdapter(this, android.R.layout.simple_spinner_item, bandeiras)
        adapterBandeira.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBandeira.adapter = adapterBandeira

        spinnerBandeira.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                bandeiraSelecionada = bandeiras[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnSalvarCartao.setOnClickListener {
            val nome = editNomeCartao.text.toString().trim()
            val banco = editBancoCartao.text.toString().trim()
            val vencimento = diaVencimentoSelecionado

            if (nome.isEmpty() || banco.isEmpty() || vencimento == null) {
                mostrarDialog("Atenção", "Por favor, preencha todos os campos e selecione a data de vencimento.")
                return@setOnClickListener
            }

            if (!isVencimentoValido(vencimento)) {
                mostrarDialog("Vencimento inválido", "O dia informado não é válido para o mês atual.")
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid
            if (userId == null) {
                mostrarDialog("Erro", "Usuário não autenticado.")
                return@setOnClickListener
            }

            val id = UUID.randomUUID().toString()
            val cartao = hashMapOf(
                "id" to id,
                "userId" to userId,
                "nome" to nome,
                "banco" to banco,
                "tipo" to tipoSelecionado,
                "bandeira" to bandeiraSelecionada,
                "vencimento" to vencimento.toString(),
                "faturaAtual" to 0.0,
                "dataFechamento" to 0
            )

            firestore.collection("cartoes")
                .document(id)
                .set(cartao)
                .addOnSuccessListener {
                    mostrarDialogSucesso("Cartão cadastrado com sucesso!")
                }
                .addOnFailureListener { e ->
                    mostrarDialog("Erro", "Erro ao salvar cartão: ${e.message}")
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
        // Limitar para escolher só o dia do mês (não mexer no mês/ano)
        // Não é possível esconder mês e ano no DatePickerDialog nativo, mas usuário pode escolher qualquer data, ok?
        // Podemos simplesmente ignorar mês e ano, pois só o dia importa.

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

    private fun mostrarDialogSucesso(mensagem: String) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Sucesso")
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
