package com.example.controle_financeiro.ui.renda

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R

class RendaActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_renda)
        supportActionBar?.title = "Gerenciar Rendas"
    }
}