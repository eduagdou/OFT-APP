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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.oftapp.model.Examen
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
    onVolver: () -> Unit,
    onIrPerfil: () -> Unit
) {
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
            } else {
                val esRealizado = examen.estado == "Realizado"
                val colorEstado = if (esRealizado) VerdeSecundario else AzulPrincipal
                val fondoEstado = if (esRealizado) VerdeContenedor else AzulContenedor

                // Tarjeta Principal de Información del Examen
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
                                text = examen.nombre,
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

                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Fecha de realización: ${examen.fecha}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoSecundario
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.Top) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = AzulPrincipal,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = examen.descripcion,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextoPrincipal
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Tarjeta de Resultado Clínico
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, BordeSuave),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Header de la Tarjeta de Resultado
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
                                    text = "Resultado Clínico Simulados",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Column(Modifier.padding(20.dp)) {
                            if (examen.resultado != null) {
                                // Resumen destacado
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
                                    text = "Observación Médica:",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextoPrincipal,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = examen.resultado.observacion,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextoSecundario
                                )
                            } else {
                                Surface(
                                    color = AzulContenedor,
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = AzulPrincipal,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            text = "Aún no hay un resultado disponible para este examen. Se publicará una vez procesado por el especialista.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AzulPrincipal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Botones de Acción
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
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Ir a Mi Perfil",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
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
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Volver al Listado",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

