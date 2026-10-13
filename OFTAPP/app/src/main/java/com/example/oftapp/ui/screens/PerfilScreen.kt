package com.example.oftapp.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun PerfilScreen(
    usuario: Usuario,
    listaPacientes: List<Paciente>,
    onCerrarSesion: () -> Unit
) {
    val esPaciente = usuario.rol == Rol.PACIENTE

    // Buscar o crear la información específica del paciente si el usuario es Paciente
    val pacienteRelacionado = if (esPaciente) {
        listaPacientes.firstOrNull { it.nombre.equals(usuario.nombre, ignoreCase = true) }
            ?: Paciente(
                id = usuario.id,
                nombre = usuario.nombre,
                rutFicticio = "18.432.109-K",
                edad = 34,
                telefonoFicticio = "+56 9 8765 4321",
                antecedentes = "Miopía leve (-1.5D OD, -1.25D OI), Astigmatismo"
            )
    } else null

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FondoApp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Tarjeta con Datos Principales del Usuario Activo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeSuave),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar con iniciales
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(AzulPrincipal, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = usuario.nombre.take(2).uppercase(),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Text(
                        text = usuario.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipal,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = TextoSecundario,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = usuario.correo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = TextoSecundario,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = usuario.sucursalDefault,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Surface(
                        color = AzulContenedor,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Rol Activo: ${usuario.rol.titulo}",
                            color = AzulPrincipal,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 2. Tarjeta con Información Específica del Perfil Seleccionado
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeSuave)
            ) {
                Column {
                    val colorEncabezado = if (esPaciente) VerdeSecundario else AzulPrincipal

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorEncabezado)
                            .padding(14.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (esPaciente) Icons.Default.HealthAndSafety else Icons.Default.MedicalInformation,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = when (usuario.rol) {
                                    Rol.PACIENTE -> "Ficha Médica del Paciente"
                                    Rol.MEDICO_OFTALMOLOGO -> "Ficha del Especialista Médico"
                                    Rol.TECNOLOGO_MEDICO -> "Ficha del Tecnólogo Médico"
                                    Rol.PERSONAL_ADMINISTRATIVO -> "Ficha del Funcionario Administrativo"
                                    Rol.ADMINISTRADOR -> "Ficha de Administrador del Sistema"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(Modifier.padding(16.dp)) {
                        when (usuario.rol) {
                            Rol.PACIENTE -> {
                                val p = pacienteRelacionado!!
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = VerdeSecundario, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "${p.nombre} (${p.edad} años)",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = TextoPrincipal,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "RUT Ficticio: ${p.rutFicticio} · Tel: ${p.telefonoFicticio}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextoSecundario
                                    )
                                }

                                Spacer(Modifier.height(8.dp))

                                Surface(
                                    color = VerdeContenedor,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(Modifier.padding(10.dp)) {
                                        Text(
                                            text = "Antecedentes Diagnosticados:",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = VerdeSecundario,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(Modifier.height(2.dp))
                                        Text(
                                            text = p.antecedentes,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextoPrincipal,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Rol.MEDICO_OFTALMOLOGO -> {
                                DetailRow("Especialidad", "Oftalmología Clínica y Cirugía Refractiva")
                                DetailRow("Registro MINSAL", "N° 483.920")
                                DetailRow("Cargo Actual", "Médico Oftalmólogo Senior")
                                DetailRow("Sucursal Principal", usuario.sucursalDefault)
                                DetailRow("Experiencia Clinical", "14 años de ejercicio profesional")
                            }

                            Rol.TECNOLOGO_MEDICO -> {
                                DetailRow("Especialidad", "Tecnología Médica Mención Oftalmología")
                                DetailRow("Registro MINSAL", "N° 592.011")
                                DetailRow("Cargo Actual", "Coordinadora de Exámenes Diagnósticos")
                                DetailRow("Sucursal Principal", usuario.sucursalDefault)
                                DetailRow("Área de Diagnóstico", "Tomografía OCT, Campimetría y Tonometría")
                            }

                            Rol.PERSONAL_ADMINISTRATIVO -> {
                                DetailRow("Cargo", "Encargado de Admisión y Recepción de Pacientes")
                                DetailRow("Código Funcionario", "ADM-2024-08")
                                DetailRow("Departamento", "Atención al Paciente y Entrega de Resultados")
                                DetailRow("Sucursal Asignada", usuario.sucursalDefault)
                                DetailRow("Nivel de Sistema", "Gestión de Entrega y Consultas")
                            }

                            Rol.ADMINISTRADOR -> {
                                DetailRow("Cargo", "Administrador General del Sistema OftApp")
                                DetailRow("Código Funcionario", "SYS-ADMIN-001")
                                DetailRow("Departamento", "Coordinación Académica DSY1105")
                                DetailRow("Alcance de Permisos", "Todas las Sucursales y Catálogos")
                                DetailRow("Nivel de Seguridad", "Acceso Total / Configuración Global")
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 3. Tarjeta de Atribuciones del Rol Activo
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeSuave)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = AzulPrincipal)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Permisos Habilitados para el Rol",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextoPrincipal
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = usuario.rol.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )

                    Spacer(Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "• Registrar atención médica: ${if (usuario.rol.puedeCrearAtencion()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeCrearAtencion()) VerdeSecundario else TextoSecundario,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Crear exámenes simulados: ${if (usuario.rol.puedeCrearExamen()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeCrearExamen()) VerdeSecundario else TextoSecundario,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Adjuntar evidencias PDF/Imagen: ${if (usuario.rol.puedeAdjuntarDocumentos()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeAdjuntarDocumentos()) VerdeSecundario else TextoSecundario,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Editar observación médica: ${if (usuario.rol.puedeEditarObservacionMedica()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeEditarObservacionMedica()) VerdeSecundario else TextoSecundario,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Actualizar estado de entrega: ${if (usuario.rol.puedeActualizarEstadoEntrega()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeActualizarEstadoEntrega()) VerdeSecundario else TextoSecundario,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // 4. Cerrar Sesión
            OutlinedButton(
                onClick = onCerrarSesion,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.error),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión / Cambiar de Rol",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, valStr: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextoSecundario,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = valStr,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoPrincipal,
            fontWeight = FontWeight.SemiBold
        )
    }
}
