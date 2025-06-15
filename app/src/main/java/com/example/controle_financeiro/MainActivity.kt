package com.example.controle_financeiro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.controle_financeiro.ui.screens.HomeScreen
import com.example.controle_financeiro.ui.theme.ControlefinanceiroTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Verificação de login
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Usuário não está logado, redireciona para LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // ✅ Continua para a tela principal se estiver logado
        enableEdgeToEdge()
        setContent {
            ControlefinanceiroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeScreen(modifier = Modifier.padding(innerPadding), context = this)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    androidx.compose.material3.Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ControlefinanceiroTheme {
        Greeting("Android")
    }
}
