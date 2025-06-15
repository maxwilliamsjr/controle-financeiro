package com.example.controle_financeiro.ui.renda

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.controle_financeiro.model.Renda
import com.google.firebase.firestore.FirebaseFirestore
import com.example.controle_financeiro.ui.theme.ControlefinanceiroTheme

class ListarRendaActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ControlefinanceiroTheme {
                ListarRendaScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListarRendaScreen() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var rendas by remember { mutableStateOf<List<Renda>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("rendas")
            .get()
            .addOnSuccessListener { result ->
                rendas = result.documents.mapNotNull { it.toObject(Renda::class.java) }
            }
            .addOnFailureListener {
                // Trate erros aqui se desejar
            }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Rendas Cadastradas") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(rendas) { renda ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            context.startActivity(
                                Intent(context, EditarRendaActivity::class.java).apply {
                                    putExtra("rendaId", renda.id)
                                }
                            )
                        },
                    colors = CardDefaults.cardColors()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Tipo: ${renda.tipo}", style = MaterialTheme.typography.titleMedium)
                        Text("Fonte: ${renda.fontePagadora}")
                        Text("Valor: R$ %.2f".format(renda.valor))
                        Text("Data: ${renda.dataRecebimento}")
                        Text("Categoria: ${renda.categoria.nome}")
                        if (!renda.descricao.isNullOrBlank()) {
                            Text("Descrição: ${renda.descricao}")
                        }
                        renda.periodicidade?.let { p ->
                            Text("Periodicidade: Dia ${p.diaFixo}, de ${p.mesInicio ?: "início"} até ${p.mesFim ?: "fim"}")
                        }
                    }
                }
            }
        }
    }
}
