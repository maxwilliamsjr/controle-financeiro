package com.example.controle_financeiro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.controle_financeiro.ui.theme.ControlefinanceiroTheme
import com.example.controle_financeiro.ui.screens.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControlefinanceiroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Context está correto aqui para abrir outras Activities
                    HomeScreen(modifier = Modifier.padding(innerPadding), context = this)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ControlefinanceiroTheme {
        Greeting("Android")
    }
}
