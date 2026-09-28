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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.oftapp.model.Examen

@Composable
fun DetalleExamenScreen(
    examen: Examen?,
    onVolver: () -> Unit,
    onIrPerfil: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (examen == null) {
            Text("Examen no encontrado", style = MaterialTheme.typography.titleLarge)
        } else {
            Text(examen.nombre, style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text(examen.descripcion, style = MaterialTheme.typography.bodyLarge)
            Text("Fecha: ${examen.fecha}", style = MaterialTheme.typography.bodyLarge)
            Text("Estado: ${examen.estado}", style = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.height(16.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Resultado (simulado)", style = MaterialTheme.typography.titleLarge)
                    if (examen.resultado != null) {
                        Text(examen.resultado.resumen, style = MaterialTheme.typography.bodyLarge)
                        Text(examen.resultado.observacion, style = MaterialTheme.typography.bodyLarge)
                    } else {
                        Text("Aún no hay resultado disponible.", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onIrPerfil,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) { Text("Ir a mi perfil", style = MaterialTheme.typography.titleMedium) }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onVolver,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) { Text("Volver al listado", style = MaterialTheme.typography.titleMedium) }
    }
}
