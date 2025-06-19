package com.example.controle_financeiro

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.controle_financeiro.ui.screens.HomeScreen
import com.example.controle_financeiro.ui.theme.ControlefinanceiroTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val uid = currentUser.uid

        enableEdgeToEdge()
        setContent {
            ControlefinanceiroTheme {
                Scaffold { innerPadding: PaddingValues ->
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        context = this,
                        uid = uid
                    )
                }
            }
        }
    }
}
