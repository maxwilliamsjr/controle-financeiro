package com.example.controle_financeiro.ui.metodopagamento

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class CadastrarCartaoActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var editNomeCartao: EditText
    private lateinit var editBancoCartao: EditText
    private lateinit var spinnerTipo: Spinner
    private lateinit var editVencimento: EditText
    private lateinit var btnSalvarCartao: Button

    private var tipoSelecionado = "Crédito"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastrar_cartao)
        supportActionBar?.title = "Cadastrar Cartão"

        firestore = FirebaseFirestore.getInstance()

        editNomeCartao = findViewById(R.id.editNomeCartao)
        editBancoCartao = findViewById(R.id.editBancoCartao)
        spinnerTipo = findViewById(R.id.spinnerTipo)
        editVencimento = findViewById(R.id.editVencimento)
        btnSalvarCartao = findViewById(R.id.btnSalvarCartao)

        val tipos = listOf("Crédito", "Débito")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, tipos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipo.adapter = adapter

        spinnerTipo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long
            ) {
                tipoSelecionado = tipos[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnSalvarCartao.setOnClickListener {
            val nome = editNomeCartao.text.toString().trim()
            val banco = editBancoCartao.text.toString().trim()
            val vencimento = editVencimento.text.toString().trim()

            if (nome.isEmpty() || banco.isEmpty() || vencimento.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSalvarCartao.isEnabled = false // Desabilita botão para evitar múltiplos cliques

            val id = UUID.randomUUID().toString()
            val cartao = hashMapOf(
                "id" to id,
                "nome" to nome,
                "banco" to banco,
                "tipo" to tipoSelecionado,
                "vencimento" to vencimento,
                "faturaAtual" to 0.0
            )

            firestore.collection("cartoes")
                .document(id)
                .set(cartao)
                .addOnSuccessListener {
                    Log.d("CadastrarCartao", "Cartão salvo com sucesso: $id")
                    runOnUiThread {
                        Toast.makeText(this, "Cartão salvo com sucesso!", Toast.LENGTH_SHORT).show()
                        // Limpa campos
                        editNomeCartao.setText("")
                        editBancoCartao.setText("")
                        editVencimento.setText("")
                        spinnerTipo.setSelection(0)
                        btnSalvarCartao.isEnabled = true
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CadastrarCartao", "Erro ao salvar cartão", e)
                    runOnUiThread {
                        Toast.makeText(this, "Erro ao salvar cartão: ${e.message}", Toast.LENGTH_SHORT).show()
                        btnSalvarCartao.isEnabled = true
                    }
                }
        }
    }
}
