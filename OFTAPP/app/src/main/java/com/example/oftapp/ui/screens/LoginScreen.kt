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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.ui.theme.AzulContenedor
import com.example.oftapp.ui.theme.AzulPrincipal
import com.example.oftapp.ui.theme.BordeSuave
import com.example.oftapp.ui.theme.FondoApp
import com.example.oftapp.ui.theme.TextoPrincipal
import com.example.oftapp.ui.theme.TextoSecundario
import com.example.oftapp.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onLoginExitoso: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = FondoApp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con Logo / Ícono Médico
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(AzulContenedor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "OftApp Logo",
                    tint = AzulPrincipal,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "OftApp",
                style = MaterialTheme.typography.displayMedium,
                color = AzulPrincipal,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Plataforma de Seguimiento Oftalmológico",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(32.dp))

            // Tarjeta con el Formulario de Ingreso
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeSuave),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextoPrincipal
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.correo,
                        onValueChange = viewModel::onCorreoChange,
                        label = { Text("Correo Institucional") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = AzulPrincipal)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulPrincipal,
                            unfocusedBorderColor = BordeSuave,
                            focusedLabelColor = AzulPrincipal
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = state.clave,
                        onValueChange = viewModel::onClaveChange,
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = AzulPrincipal)
                        },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AzulPrincipal,
                            unfocusedBorderColor = BordeSuave,
                            focusedLabelColor = AzulPrincipal
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    state.error?.let {
                        Spacer(Modifier.height(12.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.errorContainer,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Button(
                        onClick = { viewModel.iniciarSesion(onLoginExitoso) },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AzulPrincipal,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Text(
                            text = "Ingresar a la Plataforma",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Banner con datos de prueba
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = AzulContenedor,
                border = BorderStroke(1.dp, AzulPrincipal.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = AzulPrincipal,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Credenciales demo: paciente@oftapp.cl / 1234",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AzulPrincipal,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

