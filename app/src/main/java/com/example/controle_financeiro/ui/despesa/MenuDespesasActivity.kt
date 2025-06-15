package com.example.controle_financeiro.ui.despesa

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R

class MenuDespesasActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_despesas)
        supportActionBar?.title = "Menu de Despesas"

        val btnCadastrar = findViewById<Button>(R.id.btnCadastrarDespesa)
        val btnListar = findViewById<Button>(R.id.btnListarDespesas)

        btnCadastrar.setOnClickListener {
            startActivity(Intent(this, DespesaActivity::class.java))
        }

        btnListar.setOnClickListener {
            startActivity(Intent(this, ListarDespesasActivity::class.java))
        }
    }
}
