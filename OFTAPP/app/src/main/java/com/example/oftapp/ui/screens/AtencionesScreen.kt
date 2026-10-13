package com.example.oftapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.oftapp.viewmodel.AtencionesViewModel

@Composable
fun AtencionesScreen(
    viewModel: AtencionesViewModel,
    usuarioActual: Usuario
) {
    val atenciones by viewModel.atenciones.collectAsState()
    val formState by viewModel.formState.collectAsState()

    var textoBusqueda by remember { mutableStateOf("") }
    var mostrarDialogoRegistro by remember { mutableStateOf(false) }

    val esPaciente = usuarioActual.rol == Rol.PACIENTE
    val puedeRegistrar = usuarioActual.rol.puedeCrearAtencion()

    // Privacidad estricta para el Rol Paciente:
    // Si es PACIENTE, filtrar la lista para que SOLO contenga atenciones de su propio nombre.
    val atencionesBase = if (esPaciente) {
        atenciones.filter { it.pacienteNombre.equals(usuarioActual.nombre, ignoreCase = true) }
    } else {
        atenciones
    }

    val atencionesFiltradas = atencionesBase.filter { at ->
        at.pacienteNombre.contains(textoBusqueda, ignoreCase = true) ||
                at.sucursal.contains(textoBusqueda, ignoreCase = true) ||
                at.profesional.contains(textoBusqueda, ignoreCase = true) ||
                at.fecha.contains(textoBusqueda, ignoreCase = true) ||
                at.motivo.contains(textoBusqueda, ignoreCase = true)
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
                // Buscador de atenciones
                OutlinedTextField(
                    value = textoBusqueda,
                    onValueChange = { textoBusqueda = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(if (esPaciente) "Buscar en mis atenciones..." else "Buscar atención por paciente, fecha, sucursal, profesional...")
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

                Spacer(Modifier.height(12.dp))

                // Encabezado de la lista
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (esPaciente) "Mis Atenciones Médicas (${atencionesFiltradas.size})" else "Atenciones Registradas (${atencionesFiltradas.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextoPrincipal
                    )

                    if (esPaciente) {
                        Surface(
                            color = AzulContenedor,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Acceso Personal Protegido",
                                color = AzulPrincipal,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (atencionesFiltradas.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (esPaciente) "No tienes atenciones registradas asociadas a tu cuenta." else "No se encontraron atenciones médicas registradas.",
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
                        items(atencionesFiltradas, key = { it.id }) { atencion ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                border = BorderStroke(1.dp, BordeSuave),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .background(AzulContenedor, shape = CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.MedicalServices,
                                                contentDescription = null,
                                                tint = AzulPrincipal,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }

                                        Spacer(Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = atencion.motivo,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = TextoPrincipal
                                            )
                                            Text(
                                                text = "Paciente: ${atencion.pacienteNombre}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = AzulPrincipal,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        val esCompletada = atencion.estado == "Completada"
                                        Surface(
                                            color = if (esCompletada) VerdeContenedor else AzulContenedor,
                                            shape = RoundedCornerShape(12.dp)
                                        ) {
                                            Text(
                                                text = atencion.estado,
                                                color = if (esCompletada) VerdeSecundario else AzulPrincipal,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(12.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarMonth,
                                            contentDescription = null,
                                            tint = TextoSecundario,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "Fecha: ${atencion.fecha}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextoSecundario
                                        )

                                        Spacer(Modifier.width(16.dp))

                                        Icon(
                                            imageVector = Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = TextoSecundario,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = atencion.sucursal,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextoSecundario
                                        )
                                    }

                                    Spacer(Modifier.height(6.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = TextoSecundario,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(Modifier.width(6.dp))
                                        Text(
                                            text = "Profesional: ${atencion.profesional}",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextoSecundario
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Botón flotante para registrar atención (Solo roles clínicos/administrativos)
            if (puedeRegistrar) {
                FloatingActionButton(
                    onClick = { mostrarDialogoRegistro = true },
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
                        Icon(Icons.Default.Add, contentDescription = "Registrar Atención", modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Registrar Atención", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        // Diálogo para registrar atención
        if (mostrarDialogoRegistro) {
            Dialog(onDismissRequest = {
                viewModel.resetForm()
                mostrarDialogoRegistro = false
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
                            text = "Registrar Nueva Atención",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                        Text(
                            text = "Complete todos los campos obligatorios antes de guardar.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )

                        Spacer(Modifier.height(14.dp))

                        OutlinedTextField(
                            value = formState.pacienteNombre,
                            onValueChange = viewModel::onPacienteNombreChange,
                            label = { Text("Paciente Ficticio (*)") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(10.dp))

                        OutlinedTextField(
                            value = formState.fecha,
                            onValueChange = viewModel::onFechaChange,
                            label = { Text("Fecha (ej. 25/10/2026) (*)") },
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
                            value = formState.motivo,
                            onValueChange = viewModel::onMotivoChange,
                            label = { Text("Motivo de la Atención (*)") },
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
                                    viewModel.resetForm()
                                    mostrarDialogoRegistro = false
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
                                    if (viewModel.registrarAtencion()) {
                                        mostrarDialogoRegistro = false
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
