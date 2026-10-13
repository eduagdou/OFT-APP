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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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
fun ExamenesScreen(
    viewModel: ExamenesViewModel,
    usuarioActual: Usuario,
    onExamenClick: (Int) -> Unit
) {
    val examenes by viewModel.examenes.collectAsState()
    val formState by viewModel.formExamenState.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }
    var mostrarDialogoCrear by remember { mutableStateOf(false) }

    val esPaciente = usuarioActual.rol == Rol.PACIENTE
    val puedeCrearExamen = usuarioActual.rol.puedeCrearExamen()

    // Privacidad estricta para el Rol Paciente:
    // Si es PACIENTE, ver únicamente los exámenes correspondientes a su propio nombre.
    val examenesBase = if (esPaciente) {
        examenes.filter { it.pacienteNombre.equals(usuarioActual.nombre, ignoreCase = true) }
    } else {
        examenes
    }

    val examenesFiltrados = examenesBase.filter { examen ->
        val coincideTexto = examen.tipoExamen.contains(textoBusqueda, ignoreCase = true) ||
                examen.pacienteNombre.contains(textoBusqueda, ignoreCase = true) ||
                examen.sucursal.contains(textoBusqueda, ignoreCase = true) ||
                examen.fecha.contains(textoBusqueda, ignoreCase = true) ||
                examen.ojoEvaluado.contains(textoBusqueda, ignoreCase = true)

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
        Box(modifier = Modifier.fillMaxSize()) {
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
                    placeholder = {
                        Text(if (esPaciente) "Buscar en mis exámenes..." else "Buscar examen por tipo, paciente, fecha, sucursal...")
                    },
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

                Spacer(Modifier.height(10.dp))

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

                Spacer(Modifier.height(10.dp))

                // Lista de Exámenes
                if (examenesFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (esPaciente) "No tienes exámenes registrados asociados a tu cuenta." else "No se encontraron exámenes con los criterios ingresados.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextoSecundario
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp),
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
                                    // Ícono Lateral
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
                                                text = examen.tipoExamen,
                                                style = MaterialTheme.typography.titleMedium,
                                                color = TextoPrincipal,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.weight(1f)
                                            )

                                            Surface(
                                                color = fondoEstado,
                                                shape = RoundedCornerShape(12.dp)
                                            ) {
                                                Text(
                                                    text = examen.estado,
                                                    color = colorEstado,
                                                    style = MaterialTheme.typography.labelMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }

                                        Spacer(Modifier.height(4.dp))

                                        Text(
                                            text = "Paciente: ${examen.pacienteNombre}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AzulPrincipal,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Spacer(Modifier.height(2.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "Ojo: ${examen.ojoEvaluado}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextoPrincipal,
                                                fontWeight = FontWeight.Medium
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(12.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = examen.fecha,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextoSecundario
                                            )
                                        }

                                        Spacer(Modifier.height(2.dp))

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(12.dp))
                                            Spacer(Modifier.width(4.dp))
                                            Text(
                                                text = examen.sucursal,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = TextoSecundario
                                            )
                                        }
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

            // Botón flotante para crear examen (Solo roles clínicos/administrativos autorizados)
            if (puedeCrearExamen) {
                FloatingActionButton(
                    onClick = { mostrarDialogoCrear = true },
                    containerColor = AzulPrincipal,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Nuevo Examen", modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Nuevo Examen", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Diálogo para registrar examen
        if (mostrarDialogoCrear) {
            Dialog(onDismissRequest = {
                viewModel.resetFormExamen()
                mostrarDialogoCrear = false
            }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = "Registrar Nuevo Examen",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        Text(
                            text = "Ingrese los datos del examen y valide campos obligatorios.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )

                        Spacer(Modifier.height(14.dp))

                        OutlinedTextField(
                            value = formState.pacienteNombre,
                            onValueChange = viewModel::onPacienteNombreChange,
                            label = { Text("Nombre del Paciente (*)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formState.tipoExamen,
                            onValueChange = viewModel::onTipoExamenChange,
                            label = { Text("Tipo de Examen (ej. Fondo de ojo) (*)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formState.ojoEvaluado,
                            onValueChange = viewModel::onOjoEvaluadoChange,
                            label = { Text("Ojo Evaluado (ej. Ambos Ojos, OD, OI) (*)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formState.fecha,
                            onValueChange = viewModel::onFechaChange,
                            label = { Text("Fecha (ej. 10/11/2026) (*)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formState.sucursal,
                            onValueChange = viewModel::onSucursalChange,
                            label = { Text("Sucursal (*)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formState.profesional,
                            onValueChange = viewModel::onProfesionalChange,
                            label = { Text("Profesional / Especialista (*)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formState.descripcion,
                            onValueChange = viewModel::onDescripcionChange,
                            label = { Text("Descripción del Procedimiento (*)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formState.observacionInicial,
                            onValueChange = viewModel::onObservacionInicialChange,
                            label = { Text("Observación Médica / Clínica Inicial (Opcional)") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        formState.error?.let { err ->
                            Spacer(Modifier.height(10.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = err,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.resetFormExamen()
                                    mostrarDialogoCrear = false
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Cancelar", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    if (viewModel.crearExamen()) {
                                        mostrarDialogoCrear = false
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Text("Guardar", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
