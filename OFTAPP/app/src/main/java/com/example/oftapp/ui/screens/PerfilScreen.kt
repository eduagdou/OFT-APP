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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.ui.unit.sp
import com.example.oftapp.model.Paciente
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
    paciente: Paciente,
    onCerrarSesion: () -> Unit
) {
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
            // Header del Usuario con Avatar
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
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar con las iniciales del usuario
                    Box(
                        modifier = Modifier
                            .size(72.dp)
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

                    Spacer(Modifier.height(12.dp))

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

                    Spacer(Modifier.height(12.dp))

                    Surface(
                        color = AzulContenedor,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Paciente Activo",
                            color = AzulPrincipal,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tarjeta de Información Clínica
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeSuave),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    // Header con Verde Secundario
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(VerdeSecundario)
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HealthAndSafety,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Información Clínica del Paciente",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = VerdeSecundario,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Edad:",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipal,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "${paciente.edad} años",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoPrincipal
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Surface(
                            color = VerdeContenedor,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(Modifier.padding(14.dp)) {
                                Text(
                                    text = "Antecedentes Médicos Diagnostados:",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = VerdeSecundario,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = paciente.antecedentes,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextoPrincipal,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            // Botón de Cerrar Sesión
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
                        text = "Cerrar Sesión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

