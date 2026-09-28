package com.example.oftapp.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oftapp.viewmodel.ExamenesViewModel

@Composable
fun HomeScreen(
    viewModel: ExamenesViewModel,
    nombreUsuario: String,
    onVerExamenes: () -> Unit
) {
    val examenes by viewModel.examenes.collectAsState()
    val pendientes = examenes.count { it.estado == "Pendiente" }
    val atencion = viewModel.proximaAtencion

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Hola, $nombreUsuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Próxima atención", style = MaterialTheme.typography.titleLarge)
                Text("${atencion.fecha} · ${atencion.motivo}", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Exámenes pendientes", style = MaterialTheme.typography.titleLarge)
                Text("$pendientes por realizar", style = MaterialTheme.typography.bodyLarge)
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onVerExamenes,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Ver mis exámenes", style = MaterialTheme.typography.titleMedium)
        }
    }
}
