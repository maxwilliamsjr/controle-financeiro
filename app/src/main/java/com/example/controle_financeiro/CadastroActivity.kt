package com.example.controle_financeiro

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var editNome: EditText
    private lateinit var editSobrenome: EditText
    private lateinit var editNascimento: EditText
    private lateinit var editCidade: EditText
    private lateinit var editEstado: EditText
    private lateinit var editEmail: EditText
    private lateinit var editSenha: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var btnCancelar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        editNome = findViewById(R.id.editNome)
        editSobrenome = findViewById(R.id.editSobrenome)
        editNascimento = findViewById(R.id.editNascimento)
        editCidade = findViewById(R.id.editCidade)
        editEstado = findViewById(R.id.editEstado)
        editEmail = findViewById(R.id.editEmail)
        editSenha = findViewById(R.id.editSenha)
        btnCadastrar = findViewById(R.id.btnCadastrar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Configura teclado para iniciar com maiúscula nas palavras (nome, sobrenome, cidade, estado)
        editNome.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        editSobrenome.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        editCidade.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
        editEstado.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS

        // email já está ok (minúscula)
        editEmail.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        // Máscara para nascimento dd/mm/yyyy
        editNascimento.inputType = InputType.TYPE_CLASS_NUMBER
        editNascimento.addTextChangedListener(object : TextWatcher {
            private var isUpdating = false
            private val mask = "##/##/####"

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) {
                    isUpdating = false
                    return
                }

                val str = s.toString().replace("[^\\d]".toRegex(), "")
                var formatted = ""

                var i = 0
                for (m in mask.toCharArray()) {
                    if (m != '#') {
                        formatted += m
                        continue
                    }
                    if (i >= str.length) break
                    formatted += str[i]
                    i++
                }

                isUpdating = true
                editNascimento.setText(formatted)
                editNascimento.setSelection(formatted.length)
            }
        })

        btnCadastrar.setOnClickListener {
            val nome = editNome.text.toString().trim()
            val sobrenome = editSobrenome.text.toString().trim()
            val nascimento = editNascimento.text.toString().trim()
            val cidade = editCidade.text.toString().trim()
            val estado = editEstado.text.toString().trim()
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString()

            if (
                nome.isBlank() || sobrenome.isBlank() || nascimento.isBlank() ||
                cidade.isBlank() || estado.isBlank() || email.isBlank() || senha.length < 6
            ) {
                Toast.makeText(this, "Preencha todos os campos corretamente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.error = "Email inválido"
                editEmail.requestFocus()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val uid = user.uid
                            val userData = hashMapOf(
                                "nome" to nome,
                                "sobrenome" to sobrenome,
                                "nascimento" to nascimento,
                                "cidade" to cidade,
                                "estado" to estado,
                                "email" to email
                            )
                            firestore.collection("usuarios").document(uid)
                                .set(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                                    finish() // volta para login
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        Toast.makeText(this, "Erro ao cadastrar: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        btnCancelar.setOnClickListener {
            finish()
        }
    }
}
