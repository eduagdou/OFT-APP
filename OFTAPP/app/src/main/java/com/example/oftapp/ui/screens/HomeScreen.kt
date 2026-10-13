package com.example.oftapp.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.oftapp.viewmodel.ExamenesViewModel

@Composable
fun HomeScreen(
    usuarioActual: Usuario,
    examenesViewModel: ExamenesViewModel,
    atencionesViewModel: AtencionesViewModel,
    onVerExamenes: () -> Unit,
    onVerAtenciones: () -> Unit,
    onVerHistorial: () -> Unit
) {
    val context = LocalContext.current
    val todosExamenes by examenesViewModel.examenes.collectAsState()
    val todasAtenciones by atencionesViewModel.atenciones.collectAsState()
    val syncState by examenesViewModel.syncState.collectAsState()

    val esPaciente = usuarioActual.rol == Rol.PACIENTE

    // Restricción de Privacidad para Rol Paciente:
    // Un paciente SOLO ve sus propias atenciones y sus propios exámenes.
    val atencionesVisibles = if (esPaciente) {
        todasAtenciones.filter { it.pacienteNombre.equals(usuarioActual.nombre, ignoreCase = true) }
    } else todasAtenciones

    val examenesVisibles = if (esPaciente) {
        todosExamenes.filter { it.pacienteNombre.equals(usuarioActual.nombre, ignoreCase = true) }
    } else todosExamenes

    val pendientes = examenesVisibles.count { it.estado == "Pendiente" }
    val realizados = examenesVisibles.count { it.estado == "Realizado" }
    val atencionProx = atencionesVisibles.firstOrNull { it.estado == "Programada" } ?: atencionesVisibles.firstOrNull()

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
            // Saludo e Identificación de Usuario y Rol (RF01)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Hola, ${usuarioActual.nombre} 👋",
                        style = MaterialTheme.typography.displayMedium,
                        color = TextoPrincipal,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (esPaciente) "Tu Portal Personal de Seguimiento Vista" else "Panel General de Gestión Clínica",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Badge de Rol Activo
            Surface(
                color = AzulContenedor,
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, AzulPrincipal.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "Rol activo: ${usuarioActual.rol.titulo}",
                    color = AzulPrincipal,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            // Tarjeta Hero: Próxima Atención Médica
            if (atencionProx != null) {
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
                                .background(AzulPrincipal)
                                .padding(14.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (esPaciente) "Tu Próxima Atención Médica" else "Próxima Atención Registrada",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.weight(1f))
                                Surface(
                                    color = VerdeSecundario,
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Text(
                                        text = atencionProx.estado,
                                        color = Color.White,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.MedicalServices,
                                    contentDescription = null,
                                    tint = AzulPrincipal,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = atencionProx.motivo,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextoPrincipal,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(Modifier.height(6.dp))

                            Text(
                                text = "Paciente: ${atencionProx.pacienteNombre} · Fecha: ${atencionProx.fecha}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AzulPrincipal,
                                fontWeight = FontWeight.SemiBold
                            )

                            Text(
                                text = "Sucursal: ${atencionProx.sucursal} · Prof: ${atencionProx.profesional}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tarjetas de Resumen de Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Pendientes
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulContenedor),
                    border = BorderStroke(1.dp, AzulPrincipal.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(AzulPrincipal, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PendingActions, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "$pendientes",
                            style = MaterialTheme.typography.displayMedium,
                            color = AzulPrincipal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        Text(
                            text = if (esPaciente) "Mis Pendientes" else "Exámenes Pendientes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoPrincipal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Realizados
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VerdeContenedor),
                    border = BorderStroke(1.dp, VerdeSecundario.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(VerdeSecundario, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "$realizados",
                            style = MaterialTheme.typography.displayMedium,
                            color = VerdeSecundario,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        )
                        Text(
                            text = if (esPaciente) "Mis Realizados" else "Exámenes Realizados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoPrincipal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Botones de Accesos Rápidos
            Text(
                text = "Módulos de Consulta y Gestión",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal
            )

            Spacer(Modifier.height(10.dp))

            Button(
                onClick = onVerExamenes,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal, contentColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Assignment, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (esPaciente) "Ver Mis Exámenes Clínicos" else "Gestión de Exámenes Clínicos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onVerAtenciones,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, AzulPrincipal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (esPaciente) "Mis Atenciones Médicas" else "Consultar y Registrar Atenciones",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = onVerHistorial,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, AzulPrincipal),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.History, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = if (esPaciente) "Mi Historial Médico Personal" else "Historial por Paciente",
                        color = AzulPrincipal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = AzulPrincipal, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(Modifier.height(20.dp))

            // Banner Sincronización API de prueba
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
                            Icon(Icons.Default.CloudSync, contentDescription = null, tint = AzulPrincipal)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Sincronización con Servidor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal,
                                fontSize = 14.sp
                            )
                        }

                        Button(
                            onClick = {
                                examenesViewModel.simularSincronizacion()
                                Toast.makeText(context, "Datos sincronizados correctamente", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeSecundario),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Sincronizar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    syncState.mensajeSync?.let { msj ->
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = msj,
                            style = MaterialTheme.typography.bodySmall,
                            color = VerdeSecundario,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
