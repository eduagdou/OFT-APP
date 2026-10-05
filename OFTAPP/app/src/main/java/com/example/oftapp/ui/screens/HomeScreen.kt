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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun HomeScreen(
    viewModel: ExamenesViewModel,
    nombreUsuario: String,
    onVerExamenes: () -> Unit
) {
    val examenes by viewModel.examenes.collectAsState()
    val pendientes = examenes.count { it.estado == "Pendiente" }
    val realizados = examenes.size - pendientes
    val atencion = viewModel.proximaAtencion

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
            // Saludo Inicial
            Text(
                text = "Hola, $nombreUsuario 👋",
                style = MaterialTheme.typography.displayMedium,
                color = TextoPrincipal,
                fontSize = 24.sp
            )
            Text(
                text = "Resumen de tu seguimiento médico",
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundario
            )

            Spacer(Modifier.height(20.dp))

            // Tarjeta Hero: Próxima Atención
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeSuave),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    // Header de la Tarjeta con Azul Principal
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AzulPrincipal)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = "Próxima Atención Médica",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.weight(1f))
                            // Badge Confirmada en Verde Secundario
                            Surface(
                                color = VerdeSecundario,
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    text = "Confirmada",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    // Cuerpo de la tarjeta
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
                                text = atencion.motivo,
                                style = MaterialTheme.typography.titleMedium,
                                color = TextoPrincipal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Fecha programada: ${atencion.fecha}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = TextoSecundario
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tarjetas de Resumen
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Tarjeta Pendientes
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AzulContenedor),
                    border = BorderStroke(1.dp, AzulPrincipal.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(AzulPrincipal, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PendingActions,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "$pendientes",
                            style = MaterialTheme.typography.displayMedium,
                            color = AzulPrincipal,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Exámenes Pendientes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoPrincipal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Tarjeta Realizados
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = VerdeContenedor),
                    border = BorderStroke(1.dp, VerdeSecundario.copy(alpha = 0.2f))
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(VerdeSecundario, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "$realizados",
                            style = MaterialTheme.typography.displayMedium,
                            color = VerdeSecundario,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Exámenes Realizados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoPrincipal,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botón de Acción Principal
            Button(
                onClick = onVerExamenes,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AzulPrincipal,
                    contentColor = Color.White
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
                        imageVector = Icons.Default.Assignment,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Ver Mis Exámenes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

