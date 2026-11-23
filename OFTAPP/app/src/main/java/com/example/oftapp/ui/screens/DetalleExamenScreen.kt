package com.example.oftapp.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.model.Examen
import com.example.oftapp.model.Rol
import com.example.oftapp.model.Usuario
import com.example.oftapp.ui.components.PdfViewerDialog
import com.example.oftapp.ui.components.ZoomableImageDialog
import com.example.oftapp.ui.theme.VerdeSecundario
import com.example.oftapp.util.TextToSpeechHelper
import com.example.oftapp.viewmodel.ExamenesViewModel

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
    var mostrarPdfViewer by remember { mutableStateOf(false) }
    var mostrarImageViewer by remember { mutableStateOf(false) }
    var tituloImagenViewer by remember { mutableStateOf("Topografía Corneal / Retina") }
    var urlImagenViewer by remember { mutableStateOf("https://picsum.photos/800/800") }

    val ttsHelper = remember { TextToSpeechHelper(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    val esPaciente = usuarioActual.rol == Rol.PACIENTE
    val esSuPropioExamen = examen != null && examen.pacienteNombre.equals(usuarioActual.nombre, ignoreCase = true)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (examen == null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Tarjeta de mensaje: Examen no encontrado" },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Ícono de advertencia",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Examen No Encontrado",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.semantics { heading() }
                        )
                    }
                }
            } else if (esPaciente && !esSuPropioExamen) {
                // Restricción de Privacidad para Rol Paciente
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Tarjeta de aviso de privacidad: Acceso restringido a exámenes de terceros" },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Ícono de aviso de privacidad",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Acceso Restringido",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.semantics { heading() }
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Por razones de privacidad y seguridad médica, solo tienes acceso autorizado a tus propios registros de examen.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                val esRealizado = examen.estado == "Realizado"
                val colorEstado = if (esRealizado) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                val fondoEstado = if (esRealizado) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer

                // Tarjeta 1: Información General del Examen
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val txt = "Detalle del examen ${examen.tipoExamen}. Paciente: ${examen.pacienteNombre}. Ojo evaluado: ${examen.ojoEvaluado}. Fecha: ${examen.fecha}. Sucursal: ${examen.sucursal}. Especialista: ${examen.profesional}. Estado: ${examen.estado}."
                            ttsHelper.hablar(txt)
                        }
                        .semantics { contentDescription = "Información general del examen ${examen.tipoExamen} de ${examen.pacienteNombre}" },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .weight(1f)
                                    .semantics { heading() }
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
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = "Ojo evaluado", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Ojo Evaluado: ${examen.ojoEvaluado}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CalendarToday, contentDescription = "Fecha del examen", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Fecha: ${examen.fecha}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Sucursal de atención", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Sucursal: ${examen.sucursal}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(6.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = "Especialista a cargo", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Especialista: ${examen.profesional}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(Icons.Default.Description, contentDescription = "Descripción clínica", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = examen.descripcion,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tarjeta 2: Resumen del Resultado Clínico y Estado de Entrega
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val res = if (examen.resultado != null) "Resumen del resultado: ${examen.resultado.resumen}. Observación del especialista ${examen.resultado.registradoPor}: ${examen.resultado.observacion}" else "Aún no hay resultado registrado."
                            val txt = "Resultado clínico y entrega. Estado de entrega: ${examen.estadoEntrega}. $res"
                            ttsHelper.hablar(txt)
                        }
                        .semantics { contentDescription = "Sección de resultado clínico y lecturas de voz accesibles" },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (esRealizado) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (esRealizado) Icons.Default.CheckCircle else Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "Resultado Clínico y Entrega",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.semantics { heading() }
                                )
                            }
                        }

                        Column(Modifier.padding(20.dp)) {
                            // Badge de Estado de Entrega
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
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
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = examen.estadoEntrega,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
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
                                            modifier = Modifier
                                                .height(38.dp)
                                                .semantics { contentDescription = "Botón para cambiar el estado de entrega del examen" }
                                        ) {
                                            Text("Cambiar Estado", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            if (examen.resultado != null) {
                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Resumen del Resultado",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = MaterialTheme.colorScheme.secondary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = examen.resultado.resumen,
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                Button(
                                    onClick = {
                                        val textoLectura = "Examen ${examen.tipoExamen} de ${examen.pacienteNombre}. Resumen: ${examen.resultado.resumen}. Observación del profesional ${examen.resultado.registradoPor}: ${examen.resultado.observacion}"
                                        ttsHelper.hablar(textoLectura)
                                        Toast.makeText(context, "Reproduciendo voz accesible TalkBack...", Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .semantics { contentDescription = "Botón para escuchar por voz TalkBack accesible la lectura clínica completa del examen" }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Ícono de altavoz de lectura accesible por voz",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text("🔊 Leer Resultados por Voz (TalkBack)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                Spacer(Modifier.height(12.dp))

                                Text(
                                    text = "Observación Médica del Especialista:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = examen.resultado.observacion,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(Modifier.height(6.dp))

                                Text(
                                    text = "Registrado por: ${examen.resultado.registradoPor} (${examen.resultado.fechaObservacion})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                        Spacer(Modifier.width(10.dp))
                                        Text(
                                            text = "Aún no hay resultado ni observación registrada para este examen.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.primary
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
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Revisión Controlada por Especialista",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.semantics { heading() }
                                )
                            }

                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = textoResumenMedico,
                                onValueChange = { textoResumenMedico = it },
                                label = { Text("Resumen de Valores Clínicos") },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics { contentDescription = "Campo para editar el resumen de valores clínicos" }
                            )

                            Spacer(Modifier.height(10.dp))

                            OutlinedTextField(
                                value = textoObservacionMedica,
                                onValueChange = { textoObservacionMedica = it },
                                label = { Text("Observación Médica / Indicaciones") },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .semantics { contentDescription = "Campo para editar las observaciones médicas e indicaciones" }
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
                                    .semantics { contentDescription = "Botón para guardar la observación clínica en la base de datos" }
                            ) {
                                Text("Guardar Observación Clínica", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }

                // Tarjeta 4: Documentos y Visor con Zoom / PDF
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
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
                                Icon(Icons.Default.AttachFile, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Documentos y Evidencias",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.semantics { heading() }
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
                                    modifier = Modifier
                                        .height(38.dp)
                                        .semantics { contentDescription = "Botón para adjuntar nuevo informe PDF" }
                                ) {
                                    Text("+ Adjuntar PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        // Botón de Acceso Rápido a Visor de Imagen con Zoom (Topografía/Retina)
                        OutlinedButton(
                            onClick = {
                                tituloImagenViewer = "Topografía Corneal - ${examen.pacienteNombre}"
                                urlImagenViewer = "https://picsum.photos/id/1025/800/800"
                                mostrarImageViewer = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .semantics { contentDescription = "Botón para abrir el visor de imágenes médicas de topografía o retina con gesto de zoom" }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Image, contentDescription = "Ícono de imagen", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("🔍 Ver Mapa Topográfico / Retina (Zoom)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }

                        Spacer(Modifier.height(12.dp))

                        if (examen.documentos.isEmpty()) {
                            Text(
                                text = "No hay archivos adjuntos adicionales asociados a este examen.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                examen.documentos.forEach { doc ->
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Description,
                                                contentDescription = "Documento ${doc.nombre}",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(24.dp)
                                            )
                                            Spacer(Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = doc.nombre,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = "Tipo: ${doc.tipo} · Tamaño: ${doc.tamanio} · Carga: ${doc.fechaCarga}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }

                                            OutlinedButton(
                                                onClick = {
                                                    if (doc.tipo.equals("Imagen", ignoreCase = true)) {
                                                        tituloImagenViewer = doc.nombre
                                                        urlImagenViewer = "https://picsum.photos/id/1069/800/800"
                                                        mostrarImageViewer = true
                                                    } else {
                                                        mostrarPdfViewer = true
                                                    }
                                                },
                                                shape = RoundedCornerShape(8.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                                modifier = Modifier
                                                    .height(36.dp)
                                                    .semantics { contentDescription = "Botón para ver el documento ${doc.nombre}" }
                                            ) {
                                                Text(if (doc.tipo.equals("Imagen", ignoreCase = true)) "Ver Imagen" else "Ver PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tarjeta 5: Funcionalidades y Acciones
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            text = "Acciones y Sincronización",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.semantics { heading() }
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
                                    .semantics { contentDescription = "Botón para descargar reporte PDF en modo offline" }
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
                                    .semantics { contentDescription = "Botón para notificar al paciente sobre la disponibilidad de sus resultados" }
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

            // Botones de Navegación inferiores
            Button(
                onClick = onIrPerfil,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .semantics { contentDescription = "Botón para navegar a la pantalla de perfil del usuario" }
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
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .semantics { contentDescription = "Botón para regresar a la lista general de exámenes" }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Volver a Lista de Exámenes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Visor de PDF con PdfRenderer Nativo
        if (mostrarPdfViewer) {
            PdfViewerDialog(
                tituloDocumento = examen?.tipoExamen ?: "Informe Oftalmológico",
                onDismiss = { mostrarPdfViewer = false }
            )
        }

        // Visor de Imagen Médica con Zoom interactivo y Coil
        if (mostrarImageViewer) {
            ZoomableImageDialog(
                titulo = tituloImagenViewer,
                imageUrl = urlImagenViewer,
                onDismiss = { mostrarImageViewer = false }
            )
        }
    }
}
