package com.example.oftapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.oftapp.ui.theme.AzulContenedor
import com.example.oftapp.ui.theme.AzulPrincipal
import com.example.oftapp.ui.theme.BordeSuave
import com.example.oftapp.ui.theme.FondoApp
import com.example.oftapp.ui.theme.TextoPrincipal
import com.example.oftapp.ui.theme.TextoSecundario
import com.example.oftapp.ui.theme.VerdeContenedor
import com.example.oftapp.ui.theme.VerdeSecundario
import com.example.oftapp.viewmodel.ExamenesViewModel

@Composable
fun ExamenesScreen(
    viewModel: ExamenesViewModel,
    onExamenClick: (Int) -> Unit
) {
    val examenes by viewModel.examenes.collectAsState()
    var textoBusqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    val examenesFiltrados = examenes.filter { examen ->
        val coincideTexto = examen.nombre.contains(textoBusqueda, ignoreCase = true) ||
                examen.descripcion.contains(textoBusqueda, ignoreCase = true)
        val coincideFiltro = when (filtroSeleccionado) {
            "Realizados" -> examen.estado == "Realizado"
            "Pendientes" -> examen.estado == "Pendiente"
            else -> true
        }
        coincideTexto && coincideFiltro
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FondoApp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Bar de Búsqueda
            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar examen por nombre...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = AzulPrincipal)
                },
                trailingIcon = {
                    if (textoBusqueda.isNotEmpty()) {
                        IconButton(onClick = { textoBusqueda = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AzulPrincipal,
                    unfocusedBorderColor = BordeSuave,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(Modifier.height(12.dp))

            // Chips de Filtro
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtros",
                        tint = TextoSecundario,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 4.dp)
                    )
                }

                val opciones = listOf("Todos", "Realizados", "Pendientes")
                items(opciones) { opcion ->
                    val seleccionado = filtroSeleccionado == opcion
                    FilterChip(
                        selected = seleccionado,
                        onClick = { filtroSeleccionado = opcion },
                        label = {
                            Text(
                                text = opcion,
                                fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (opcion == "Realizados") VerdeSecundario else AzulPrincipal,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextoPrincipal
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = seleccionado,
                            borderColor = BordeSuave,
                            selectedBorderColor = Color.Transparent
                        )
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Lista de Tarjetas de Exámenes
            if (examenesFiltrados.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron exámenes con los criterios ingresados.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextoSecundario
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(examenesFiltrados, key = { it.id }) { examen ->
                        val esRealizado = examen.estado == "Realizado"
                        val colorEstado = if (esRealizado) VerdeSecundario else AzulPrincipal
                        val fondoEstado = if (esRealizado) VerdeContenedor else AzulContenedor

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onExamenClick(examen.id) },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BordeSuave),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Ícono Lateral en Contenedor Circular
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(fondoEstado, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = colorEstado,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(Modifier.width(14.dp))

                                // Información Principal
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = examen.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextoPrincipal,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.weight(1f)
                                        )

                                        // Badge de Estado
                                        Surface(
                                            color = fondoEstado,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = examen.estado,
                                                color = colorEstado,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = TextoSecundario,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "Fecha: ${examen.fecha}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextoSecundario
                                        )
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    Text(
                                        text = examen.descripcion,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TextoSecundario,
                                        maxLines = 2
                                    )
                                }

                                Spacer(Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Ver Detalle",
                                    tint = AzulPrincipal,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

