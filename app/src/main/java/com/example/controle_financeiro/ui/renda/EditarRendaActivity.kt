package com.example.controle_financeiro.ui.renda

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.controle_financeiro.model.Categoria
import com.example.controle_financeiro.model.Periodicidade
import com.example.controle_financeiro.model.Renda
import com.google.firebase.firestore.FirebaseFirestore

class EditarRendaActivity : ComponentActivity() {
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rendaId = intent.getStringExtra("rendaId")
        if (rendaId == null) {
            finish() // fecha se não tiver id
            return
        }

        setContent {
            EditarRendaScreen(rendaId)
        }
    }

    @Composable
    fun EditarRendaScreen(rendaId: String) {
        val context = LocalContext.current

        var tipo by remember { mutableStateOf("") }
        var fontePagadora by remember { mutableStateOf("") }
        var valor by remember { mutableStateOf("") }
        var dataRecebimento by remember { mutableStateOf("") }
        var categoria by remember { mutableStateOf("") }
        var descricao by remember { mutableStateOf("") }
        var diaFixo by remember { mutableStateOf("") }
        var mesInicio by remember { mutableStateOf("") }
        var mesFim by remember { mutableStateOf("") }

        var isLoading by remember { mutableStateOf(true) }

        // Busca a renda no Firestore uma vez
        LaunchedEffect(rendaId) {
            firestore.collection("rendas").document(rendaId).get()
                .addOnSuccessListener { doc ->
                    val renda = doc.toObject(Renda::class.java)
                    if (renda != null) {
                        tipo = renda.tipo
                        fontePagadora = renda.fontePagadora
                        valor = renda.valor.toString()
                        dataRecebimento = renda.dataRecebimento
                        categoria = renda.categoria.nome
                        descricao = renda.descricao ?: ""
                        diaFixo = renda.periodicidade?.diaFixo?.toString() ?: ""
                        mesInicio = renda.periodicidade?.mesInicio?.toString() ?: ""
                        mesFim = renda.periodicidade?.mesFim?.toString() ?: ""
                    }
                    isLoading = false
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erro ao carregar renda", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Editar Renda", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = tipo,
                onValueChange = { tipo = it },
                label = { Text("Tipo (Ex: Salário, Bônus...)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = fontePagadora,
                onValueChange = { fontePagadora = it },
                label = { Text("Fonte Pagadora (Empresa, etc)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valor,
                onValueChange = { valor = it },
                label = { Text("Valor") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = dataRecebimento,
                onValueChange = { dataRecebimento = it },
                label = { Text("Data de Recebimento (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = categoria,
                onValueChange = { categoria = it },
                label = { Text("Categoria") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth()
            )

            Divider()
            Text("Periodicidade", style = MaterialTheme.typography.bodyLarge)

            OutlinedTextField(
                value = diaFixo,
                onValueChange = { diaFixo = it },
                label = { Text("Dia Fixo (Ex: 5)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = mesInicio,
                onValueChange = { mesInicio = it },
                label = { Text("Mês de Início (1-12)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = mesFim,
                onValueChange = { mesFim = it },
                label = { Text("Mês de Fim (1-12)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = {
                if (tipo.isBlank() || fontePagadora.isBlank() || valor.isBlank() || dataRecebimento.isBlank() || categoria.isBlank()) {
                    Toast.makeText(context, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val rendaEditada = Renda(
                    id = rendaId,
                    tipo = tipo,
                    fontePagadora = fontePagadora,
                    valor = valor.toDouble(),
                    dataRecebimento = dataRecebimento,
                    categoria = Categoria(id = "", nome = categoria, descricao = ""),
                    descricao = descricao,
                    periodicidade = Periodicidade(
                        diaFixo = diaFixo.toIntOrNull() ?: 1,
                        mesInicio = mesInicio.toIntOrNull(),
                        mesFim = mesFim.toIntOrNull()
                    )
                )

                firestore.collection("rendas").document(rendaId).set(rendaEditada)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Renda atualizada com sucesso", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Erro ao atualizar", Toast.LENGTH_SHORT).show()
                    }
            }) {
                Text("Salvar Alterações")
            }
        }
    }
}
