package com.example.oftapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun ExamenesScreen(
    viewModel: ExamenesViewModel,
    onExamenClick: (Int) -> Unit
) {
    val examenes by viewModel.examenes.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(examenes, key = { it.id }) { examen ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExamenClick(examen.id) }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(examen.nombre, style = MaterialTheme.typography.titleLarge)
                    Text("Fecha: ${examen.fecha}", style = MaterialTheme.typography.bodyLarge)
                    Text("Estado: ${examen.estado}", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}
