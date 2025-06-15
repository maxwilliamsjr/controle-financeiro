package com.example.controle_financeiro.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controle_financeiro.LoginActivity
import com.example.controle_financeiro.ui.despesa.MenuDespesasActivity
import com.example.controle_financeiro.ui.planejamento.PlanejamentoActivity
import com.example.controle_financeiro.ui.renda.RendaActivity
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(modifier: Modifier = Modifier, context: Context) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Bem-vindo ao controle financeiro")

        // ✅ Atualizado para abrir MenuDespesasActivity
        Button(onClick = {
            val intent = Intent(context, MenuDespesasActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Gerenciar Despesas")
        }

        Button(onClick = {
            val intent = Intent(context, RendaActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Gerenciar Rendas")
        }

        Button(onClick = {
            val intent = Intent(context, PlanejamentoActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Planejamento Mensal")
        }

        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }) {
            Text(text = "Sair")
        }
    }
}
