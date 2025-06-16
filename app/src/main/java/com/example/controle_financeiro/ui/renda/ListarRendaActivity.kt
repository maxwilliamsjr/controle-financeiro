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
import com.example.controle_financeiro.model.RendaSimplificada
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
    var rendas by remember { mutableStateOf<List<RendaSimplificada>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("rendas")
            .get()
            .addOnSuccessListener { result ->
                rendas = result.documents.mapNotNull { doc ->
                    try {
                        val id = doc.getString("id") ?: return@mapNotNull null
                        val tipo = doc.getString("tipo") ?: ""
                        val fonte = doc.getString("fontePagadora") ?: ""
                        val valor = doc.getDouble("valor") ?: 0.0
                        val data = doc.getString("dataRecebimento") ?: ""
                        val categoriaNome = doc.get("categoria.nome") as? String ?: ""
                        val descricao = doc.getString("descricao")
                        val periodicidade = doc.get("periodicidade") as? Map<*, *>

                        val diaFixo = (periodicidade?.get("diaFixo") as? Long)?.toInt()
                        val mesInicio = (periodicidade?.get("mesInicio") as? Long)?.toInt()
                        val anoInicio = (periodicidade?.get("anoInicio") as? Long)?.toInt()
                        val mesFim = (periodicidade?.get("mesFim") as? Long)?.toInt()
                        val anoFim = (periodicidade?.get("anoFim") as? Long)?.toInt()

                        RendaSimplificada(
                            id = id,
                            tipo = tipo,
                            fontePagadora = fonte,
                            valor = valor,
                            dataRecebimento = data,
                            categoriaNome = categoriaNome,
                            descricao = descricao,
                            diaFixo = diaFixo,
                            mesInicio = mesInicio,
                            anoInicio = anoInicio,
                            mesFim = mesFim,
                            anoFim = anoFim
                        )
                    } catch (e: Exception) {
                        null
                    }
                }
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
                        Text("Categoria: ${renda.categoriaNome}")
                        if (!renda.descricao.isNullOrBlank()) {
                            Text("Descrição: ${renda.descricao}")
                        }
                        if (renda.diaFixo != null && renda.mesInicio != null && renda.mesFim != null) {
                            Text("Periodicidade: Dia ${renda.diaFixo}, de ${renda.mesInicio} até ${renda.mesFim}")
                        }
                    }
                }
            }
        }
    }
}
