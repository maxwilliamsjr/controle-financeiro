package com.example.controle_financeiro.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.controle_financeiro.LoginActivity
import com.example.controle_financeiro.ui.categoria.MenuCategoriaActivity
import com.example.controle_financeiro.ui.despesa.MenuDespesasActivity
import com.example.controle_financeiro.ui.metodopagamento.MenuMetodoPagamentoActivity
import com.example.controle_financeiro.ui.planejamento.PlanejamentoActivity
import com.example.controle_financeiro.ui.renda.MenuRendaActivity // <- atualizado aqui
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

        Button(onClick = {
            val intent = Intent(context, MenuDespesasActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Gerenciar Despesas")
        }

        Button(onClick = {
            val intent = Intent(context, MenuRendaActivity::class.java) // <- corrigido aqui
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
            val intent = Intent(context, MenuCategoriaActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Gerenciar Categorias")
        }

        Button(onClick = {
            val intent = Intent(context, MenuMetodoPagamentoActivity::class.java)
            context.startActivity(intent)
        }) {
            Text(text = "Gerenciar Métodos de Pagamento")
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = "Bem-vindo ao controle financeiro")

        Button(onClick = { /* Preview, não faz nada */ }) {
            Text(text = "Gerenciar Despesas")
        }

        Button(onClick = { }) {
            Text(text = "Gerenciar Rendas")
        }

        Button(onClick = { }) {
            Text(text = "Planejamento Mensal")
        }

        Button(onClick = { }) {
            Text(text = "Gerenciar Categorias")
        }

        Button(onClick = { }) {
            Text(text = "Gerenciar Métodos de Pagamento")
        }

        Button(onClick = { }) {
            Text(text = "Sair")
        }
    }
}
