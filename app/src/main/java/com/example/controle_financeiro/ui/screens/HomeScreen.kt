package com.example.controle_financeiro.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.controle_financeiro.ui.despesa.DespesaActivity
import com.example.controle_financeiro.ui.planejamento.PlanejamentoActivity
import com.example.controle_financeiro.ui.renda.RendaActivity

@Composable
fun HomeScreen(modifier: Modifier = Modifier, context: Context) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Bem-vindo ao controle financeiro")

        Button(onClick = {
            val intent = Intent(context, DespesaActivity::class.java)
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
    }
}
