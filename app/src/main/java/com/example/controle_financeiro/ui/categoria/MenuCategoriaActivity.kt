package com.example.controle_financeiro.ui.categoria

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.controle_financeiro.R

class MenuCategoriaActivity : AppCompatActivity() {

    private lateinit var btnCadastrarCategoria: Button
    private lateinit var btnListarCategorias: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_categoria)
        supportActionBar?.title = "Menu Categoria"

        btnCadastrarCategoria = findViewById(R.id.btnCadastrarCategoria)
        btnListarCategorias = findViewById(R.id.btnListarCategorias)

        btnCadastrarCategoria.setOnClickListener {
            startActivity(Intent(this, CategoriaActivity::class.java))
        }

        btnListarCategorias.setOnClickListener {
            startActivity(Intent(this, ListarCategoriaActivity::class.java))
        }
    }
}
