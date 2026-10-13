package com.example.oftapp.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.model.Examen
import com.example.oftapp.model.Rol
import com.example.oftapp.model.Usuario
import com.example.oftapp.viewmodel.ExamenesViewModel
import com.example.oftapp.ui.theme.AzulContenedor
import com.example.oftapp.ui.theme.AzulPrincipal
import com.example.oftapp.ui.theme.BordeSuave
import com.example.oftapp.ui.theme.FondoApp
import com.example.oftapp.ui.theme.TextoPrincipal
import com.example.oftapp.ui.theme.TextoSecundario
import com.example.oftapp.ui.theme.VerdeContenedor
import com.example.oftapp.ui.theme.VerdeSecundario

@Composable
fun DetalleExamenScreen(
    examen: Examen?,
    usuarioActual: Usuario,
    viewModel: ExamenesViewModel,
    onVolver: () -> Unit,
    onIrPerfil: () -> Unit
) {
    val context = LocalContext.current

    var textoResumenMedico by remember { mutableStateOf(examen?.resultado?.resumen ?: "") }
    var textoObservacionMedica by remember { mutableStateOf(examen?.resultado?.observacion ?: "") }

    val esPaciente = usuarioActual.rol == Rol.PACIENTE
    val esSuPropioExamen = examen != null && examen.pacienteNombre.equals(usuarioActual.nombre, ignoreCase = true)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FondoApp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (examen == null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Examen No Encontrado",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextoPrincipal
                        )
                    }
                }
            } else if (esPaciente && !esSuPropioExamen) {
                // Restricción de Privacidad para Rol Paciente
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Acceso Restringido",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Por razones de privacidad y seguridad médica, solo tienes acceso autorizado a tus propios registros de examen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }
                }
            } else {
                val esRealizado = examen.estado == "Realizado"
                val colorEstado = if (esRealizado) VerdeSecundario else AzulPrincipal
                val fondoEstado = if (esRealizado) VerdeContenedor else AzulContenedor

                // Tarjeta 1: Información General del Examen
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BordeSuave),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = examen.tipoExamen,
                                style = MaterialTheme.typography.headlineSmall,
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
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Paciente: ${examen.pacienteNombre}",
                            style = MaterialTheme.typography.titleMedium,
                            color = AzulPrincipal,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Ojo Evaluado: ${examen.ojoEvaluado}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = TextoPrincipal
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Fecha: ${examen.fecha}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Sucursal: ${examen.sucursal}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Especialista: ${examen.profesional}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Description, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = examen.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoPrincipal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tarjeta 2: Resumen del Resultado Clínico y Estado de Entrega
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BordeSuave),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (esRealizado) VerdeSecundario else AzulPrincipal)
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (esRealizado) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Resultado Clínico y Entrega",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(Modifier.padding(20.dp)) {
                            // Badge de Estado de Entrega
                            Surface(
                                color = AzulContenedor,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Estado de Entrega de Resultados:",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = TextoSecundario
                                        )
                                        Text(
                                            text = examen.estadoEntrega,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = AzulPrincipal
                                        )
                                    }

                                    if (usuarioActual.rol.puedeActualizarEstadoEntrega()) {
                                        OutlinedButton(
                                            onClick = {
                                                val nuevoEst = if (examen.estadoEntrega == "Disponible en portal") "Entregado en sucursal" else "Disponible en portal"
                                                viewModel.actualizarEstadoEntrega(examen.id, nuevoEst)
                                                Toast.makeText(context, "Estado de entrega actualizado: $nuevoEst", Toast.LENGTH_SHORT).show()
                                            },
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.height(38.dp)
                                        ) {
                                            Text("Cambiar Estado", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            if (examen.resultado != null) {
                                Surface(
                                    color = VerdeContenedor,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Resumen del Resultado",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = VerdeSecundario,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = examen.resultado.resumen,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = TextoPrincipal,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                Text(
                                    text = "Observación Médica del Especialista:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextoPrincipal,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = examen.resultado.observacion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextoSecundario
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = "Registrado por: ${examen.resultado.registradoPor} (${examen.resultado.fechaObservacion})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextoSecundario
                                )
                            } else {
                                Surface(
                                    color = AzulContenedor,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = AzulPrincipal)
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = "Aún no hay resultado ni observación registrada para este examen.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AzulPrincipal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tarjeta 3: Revisión y Edición Controlada por Médico Oftalmólogo
                if (usuarioActual.rol.puedeEditarObservacionMedica()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BordeSuave)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = AzulPrincipal)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Revisión Controlada por Especialista",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoPrincipal
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = textoResumenMedico,
                                onValueChange = { textoResumenMedico = it },
                                label = { Text("Resumen de Valores Clínicos") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = textoObservacionMedica,
                                onValueChange = { textoObservacionMedica = it },
                                label = { Text("Observación Médica / Indicaciones") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    viewModel.actualizarObservacionMedica(
                                        examenId = examen.id,
                                        resumen = textoResumenMedico,
                                        observacion = textoObservacionMedica,
                                        profesionalNombre = usuarioActual.nombre
                                    )
                                    Toast.makeText(context, "Observación guardada con éxito", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = VerdeSecundario),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text("Guardar Observación Clínica", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // Tarjeta 4: Documentos Asociados Simulados
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
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = AzulPrincipal)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Documentos y Evidencias",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextoPrincipal
                                )
                            }

                            if (usuarioActual.rol.puedeAdjuntarDocumentos()) {
                                OutlinedButton(
                                    onClick = {
                                        viewModel.adjuntarDocumentoSimulado(
                                            examenId = examen.id,
                                            nombreArchivo = "Informe_Anexo_${examen.tipoExamen.replace(" ", "")}.pdf",
                                            tipoArchivo = "PDF"
                                        )
                                        Toast.makeText(context, "Documento simulado adjuntado exitosamente", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                    modifier = Modifier.height(38.dp)
                                ) {
                                    Text("+ Adjuntar PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        if (examen.documentos.isEmpty()) {
                            Text(
                                text = "No hay archivos adjuntos asociados a este examen.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                examen.documentos.forEach { doc ->
                                    Surface(
                                        color = AzulContenedor,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = null,
                                                tint = AzulPrincipal,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = doc.nombre,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextoPrincipal
                                                )
                                                Text(
                                                    text = "Tipo: ${doc.tipo} · Tamaño: ${doc.tamanio} · Carga: ${doc.fechaCarga}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = TextoSecundario
                                                )
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    Toast.makeText(context, "Visualizando contenido de: ${doc.nombre}", Toast.LENGTH_LONG).show()
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier.height(36.dp)
                                            ) {
                                                Text("Ver", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tarjeta 5: Funcionalidades Simuladas
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BordeSuave)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Acciones y Sincronización",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )

                        Spacer(Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Simulando descarga offline de PDF de ${examen.tipoExamen}...", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Descarga PDF", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            OutlinedButton(
                                onClick = {
                                    Toast.makeText(context, "Notificación enviada al paciente ${examen.pacienteNombre}", Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(6.dp))
                                    Text("Notificar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Botones de Navegación inferiores con formato y alineación profesional
            Button(
                onClick = onIrPerfil,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulPrincipal,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Ir a Perfil de Usuario", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onVolver,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, AzulPrincipal),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AzulPrincipal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Volver a Lista de Exámenes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
