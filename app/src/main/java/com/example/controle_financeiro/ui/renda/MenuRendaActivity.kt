package com.example.controle_financeiro.ui.renda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controle_financeiro.ui.theme.ControlefinanceiroTheme

class MenuRendaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlefinanceiroTheme {
                MenuRendaScreen(context = this)
            }
        }
    }
}

@Composable
fun MenuRendaScreen(context: Context) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Menu Renda", style = MaterialTheme.typography.headlineSmall)

        Button(onClick = {
            context.startActivity(Intent(context, RendaActivity::class.java))
        }) {
            Text("Cadastrar Renda")
        }

        Button(onClick = {
            context.startActivity(Intent(context, ListarRendaActivity::class.java))
        }) {
            Text("Listar Rendas")
        }
    }
}
