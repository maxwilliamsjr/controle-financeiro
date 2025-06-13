package com.example.controle_financeiro.ui.despesa

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R

class DespesaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_despesa)
        supportActionBar?.title = "Gerenciar Despesas"
    }
}
