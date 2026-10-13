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
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.oftapp.model.Paciente
import com.example.oftapp.model.Rol
import com.example.oftapp.model.Usuario
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
fun HistorialPacienteScreen(
    viewModel: ExamenesViewModel,
    usuarioActual: Usuario,
    onExamenClick: (Int) -> Unit
) {
    val esPaciente = usuarioActual.rol == Rol.PACIENTE
    val todosPacientes = viewModel.listaPacientes

    // Si es PACIENTE, filtrar la lista para que SOLO pueda ver su propio perfil
    val pacientesDisponibles = if (esPaciente) {
        todosPacientes.filter { it.nombre.equals(usuarioActual.nombre, ignoreCase = true) }
    } else {
        todosPacientes
    }

    var pacienteSeleccionado by remember {
        mutableStateOf<Paciente>(pacientesDisponibles.firstOrNull() ?: todosPacientes[0])
    }

    val historialExamenes = viewModel.obtenerHistorialPaciente(pacienteSeleccionado.nombre)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FondoApp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(AzulContenedor, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = AzulPrincipal)
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (esPaciente) "Mi Historial Médico Personal" else "Historial por Paciente",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )
                    Text(
                        text = if (esPaciente) "Revisa el registro histórico de tus exámenes oftalmológicos." else "Seleccione un paciente para revisar su historial clínico completo.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextoSecundario
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Selector de Pacientes (Solo si NO es rol Paciente)
            if (!esPaciente && pacientesDisponibles.size > 1) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(pacientesDisponibles) { pac ->
                        val esSelec = pac.id == pacienteSeleccionado.id
                        FilterChip(
                            selected = esSelec,
                            onClick = { pacienteSeleccionado = pac },
                            label = {
                                Text(
                                    text = pac.nombre,
                                    fontWeight = if (esSelec) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AzulPrincipal,
                                selectedLabelColor = Color.White,
                                containerColor = Color.White,
                                labelColor = TextoPrincipal
                            )
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
            }

            // Tarjeta de Antecedentes del Paciente Seleccionado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeSuave)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = pacienteSeleccionado.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        Surface(
                            color = AzulContenedor,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "RUT: ${pacienteSeleccionado.rutFicticio}",
                                color = AzulPrincipal,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = "Edad: ${pacienteSeleccionado.edad} años · Tel: ${pacienteSeleccionado.telefonoFicticio}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )

                    Spacer(Modifier.height(8.dp))

                    Surface(
                        color = VerdeContenedor,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Antecedentes Diagnosticados: ${pacienteSeleccionado.antecedentes}",
                            style = MaterialTheme.typography.bodySmall,
                            color = VerdeSecundario,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (esPaciente) "Mis Exámenes Previos (${historialExamenes.size})" else "Exámenes Registrados del Paciente (${historialExamenes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(Modifier.height(10.dp))

            if (historialExamenes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay registros de exámenes previos disponibles.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextoSecundario
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(historialExamenes, key = { it.id }) { examen ->
                        val esRealizado = examen.estado == "Realizado"
                        val colorEstado = if (esRealizado) VerdeSecundario else AzulPrincipal
                        val fondoEstado = if (esRealizado) VerdeContenedor else AzulContenedor

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onExamenClick(examen.id) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, BordeSuave),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(fondoEstado, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = colorEstado,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = examen.tipoExamen,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextoPrincipal
                                        )
                                        Surface(
                                            color = fondoEstado,
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Text(
                                                text = examen.estado,
                                                color = colorEstado,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(2.dp))

                                    Text(
                                        text = "Ojo: ${examen.ojoEvaluado} · Fecha: ${examen.fecha}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextoSecundario
                                    )

                                    if (examen.resultado != null) {
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = "Resultado: ${examen.resultado.resumen}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AzulPrincipal,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(Modifier.width(8.dp))

                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = AzulPrincipal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
