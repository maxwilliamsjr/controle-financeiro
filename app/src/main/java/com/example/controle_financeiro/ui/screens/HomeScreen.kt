package com.example.controle_financeiro.ui.screens

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.controle_financeiro.LoginActivity
import com.example.controle_financeiro.ui.categoria.MenuCategoriaActivity
import com.example.controle_financeiro.ui.despesa.MenuDespesasActivity
import com.example.controle_financeiro.ui.metodopagamento.MenuMetodoPagamentoActivity
import com.example.controle_financeiro.ui.planejamento.PlanejamentoActivity
import com.example.controle_financeiro.ui.renda.MenuRendaActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    context: Context,
    uid: String
) {
    var nome by remember { mutableStateOf<String?>(null) }

    // Buscar o nome do usuário no Firestore
    LaunchedEffect(uid) {
        val db = FirebaseFirestore.getInstance()
        db.collection("usuarios").document(uid)
            .get()
            .addOnSuccessListener { doc ->
                val nomeCompleto = doc.getString("nome") ?: ""
                nome = nomeCompleto.split(" ").firstOrNull()?.replaceFirstChar { it.uppercase() } ?: ""
            }
            .addOnFailureListener {
                nome = ""
            }
    }

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Bem-vindo ao controle financeiro${if (!nome.isNullOrEmpty()) ", $nome" else ""}!",
            style = MaterialTheme.typography.titleMedium
        )

        Button(onClick = {
            context.startActivity(Intent(context, MenuDespesasActivity::class.java))
        }) {
            Text("Gerenciar Despesas")
        }

        Button(onClick = {
            context.startActivity(Intent(context, MenuRendaActivity::class.java))
        }) {
            Text("Gerenciar Rendas")
        }

        Button(onClick = {
            context.startActivity(Intent(context, PlanejamentoActivity::class.java))
        }) {
            Text("Planejamento Mensal")
        }

        Button(onClick = {
            context.startActivity(Intent(context, MenuCategoriaActivity::class.java))
        }) {
            Text("Gerenciar Categorias")
        }

        Button(onClick = {
            context.startActivity(Intent(context, MenuMetodoPagamentoActivity::class.java))
        }) {
            Text("Gerenciar Métodos de Pagamento")
        }

        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            (context as? Activity)?.finish()
        }) {
            Text("Sair")
        }
    }
}
