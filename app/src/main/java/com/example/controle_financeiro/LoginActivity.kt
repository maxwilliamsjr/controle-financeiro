package com.example.controle_financeiro

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editSenha = findViewById<EditText>(R.id.editSenha)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        val imgToggleSenha = findViewById<ImageView>(R.id.imgToggleSenha)

        // Botão "olhinho" para visualizar senha
        var senhaVisivel = false
        imgToggleSenha.setOnClickListener {
            senhaVisivel = !senhaVisivel
            if (senhaVisivel) {
                editSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                imgToggleSenha.setImageResource(R.drawable.ic_eye_off) // ícone olho fechado
            } else {
                editSenha.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                imgToggleSenha.setImageResource(R.drawable.ic_eye) // ícone olho aberto
            }
            // Mantém cursor no final
            editSenha.setSelection(editSenha.text.length)
        }

        btnLogin.setOnClickListener {
            val email = editEmail.text.toString().trim()
            val senha = editSenha.text.toString().trim()

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                editEmail.error = "Email inválido"
                editEmail.requestFocus()
                return@setOnClickListener
            }

            if (senha.length < 6) {
                editSenha.error = "Senha deve ter ao menos 6 caracteres"
                editSenha.requestFocus()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                        val user = auth.currentUser
                        if (user != null) {
                            val uid = user.uid
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                    } else {
                        Toast.makeText(this, "Erro ao fazer login: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }

        btnCadastrar.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
        }
    }
}
