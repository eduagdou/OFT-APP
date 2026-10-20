package com.example.oftapp.ui.screens

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.MedicalInformation
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
fun rememberBitmapFromUri(uriString: String?): ImageBitmap? {
    val context = LocalContext.current
    return remember(uriString) {
        if (uriString.isNullOrBlank()) null
        else {
            try {
                val uri = Uri.parse(uriString)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source).asImageBitmap()
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri).asImageBitmap()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}

@Composable
fun PerfilScreen(
    usuario: Usuario,
    listaPacientes: List<Paciente>,
    onActualizarNombre: (String) -> Unit = {},
    onActualizarFoto: (String?) -> Unit = {},
    onCambiarClave: (claveActual: String, nuevaClave: String) -> String? = { _, _ -> null },
    onCerrarSesion: () -> Unit
) {
    val context = LocalContext.current
    val esPaciente = usuario.rol == Rol.PACIENTE

    // Estado para edición de nombre
    var editandoNombre by remember(usuario.nombre) { mutableStateOf(false) }
    var nombreInput by remember(usuario.nombre) { mutableStateOf(usuario.nombre) }

    // Estado para cambio de contraseña
    var mostrarCambioClave by remember { mutableStateOf(false) }
    var claveActual by remember { mutableStateOf("") }
    var nuevaClave by remember { mutableStateOf("") }
    var confirmarClave by remember { mutableStateOf("") }

    var claveActualVisible by remember { mutableStateOf(false) }
    var nuevaClaveVisible by remember { mutableStateOf(false) }
    var confirmarClaveVisible by remember { mutableStateOf(false) }

    var mensajeErrorClave by remember { mutableStateOf<String?>(null) }
    var mensajeExitoClave by remember { mutableStateOf<String?>(null) }

    // Launcher para selección de foto de perfil
    val bitmapFoto = rememberBitmapFromUri(usuario.fotoPerfilUri)
    val launcherFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) {}
            onActualizarFoto(it.toString())
        }
    }

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
            // 1. Tarjeta con Datos Principales y Foto de Perfil
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
                    // Contenedor del Avatar / Foto
                    Box(contentAlignment = Alignment.BottomEnd) {
                        if (bitmapFoto != null) {
                            Image(
                                bitmap = bitmapFoto,
                                contentDescription = "Foto de perfil",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(2.5.dp, AzulPrincipal, CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
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
                        }

                        Surface(
                            shape = CircleShape,
                            color = AzulPrincipal,
                            border = BorderStroke(1.5.dp, Color.White),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { launcherFoto.launch("image/*") }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Cambiar Foto",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }

                    // Botones de gestión de foto
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        TextButton(onClick = { launcherFoto.launch("image/*") }) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Cambiar foto", style = MaterialTheme.typography.labelMedium)
                        }
                        if (usuario.fotoPerfilUri != null) {
                            Spacer(Modifier.width(4.dp))
                            TextButton(
                                onClick = { onActualizarFoto(null) },
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Quitar foto", style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Edición de Nombre
                    if (editandoNombre) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = nombreInput,
                                onValueChange = { nombreInput = it },
                                label = { Text("Nombre de usuario") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = AzulPrincipal) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = {
                                        nombreInput = usuario.nombre
                                        editandoNombre = false
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        if (nombreInput.isNotBlank()) {
                                            onActualizarNombre(nombreInput)
                                            editandoNombre = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Guardar")
                                }
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = usuario.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextoPrincipal,
                                fontWeight = FontWeight.Bold
                            )
                            IconButton(
                                onClick = { editandoNombre = true },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar nombre",
                                    tint = AzulPrincipal,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

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

            // 2. Tarjeta de Cambio de Contraseña
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BordeSuave)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mostrarCambioClave = !mostrarCambioClave }
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = AzulPrincipal)
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Seguridad y Contraseña",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                            Text(
                                text = "Cambiar la clave de acceso de esta cuenta",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }
                        IconButton(onClick = { mostrarCambioClave = !mostrarCambioClave }) {
                            Icon(
                                imageVector = if (mostrarCambioClave) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = TextoSecundario
                            )
                        }
                    }

                    if (mostrarCambioClave) {
                        Spacer(Modifier.height(12.dp))

                        if (mensajeErrorClave != null) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Text(
                                    text = mensajeErrorClave!!,
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        if (mensajeExitoClave != null) {
                            Surface(
                                color = VerdeContenedor,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Text(
                                    text = mensajeExitoClave!!,
                                    color = VerdeSecundario,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                            value = claveActual,
                            onValueChange = {
                                claveActual = it
                                mensajeErrorClave = null
                                mensajeExitoClave = null
                            },
                            label = { Text("Contraseña actual") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = AzulPrincipal) },
                            trailingIcon = {
                                IconButton(onClick = { claveActualVisible = !claveActualVisible }) {
                                    Icon(
                                        imageVector = if (claveActualVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (claveActualVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = nuevaClave,
                            onValueChange = {
                                nuevaClave = it
                                mensajeErrorClave = null
                                mensajeExitoClave = null
                            },
                            label = { Text("Nueva contraseña") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = AzulPrincipal) },
                            trailingIcon = {
                                IconButton(onClick = { nuevaClaveVisible = !nuevaClaveVisible }) {
                                    Icon(
                                        imageVector = if (nuevaClaveVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (nuevaClaveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmarClave,
                            onValueChange = {
                                confirmarClave = it
                                mensajeErrorClave = null
                                mensajeExitoClave = null
                            },
                            label = { Text("Confirmar nueva contraseña") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = AzulPrincipal) },
                            trailingIcon = {
                                IconButton(onClick = { confirmarClaveVisible = !confirmarClaveVisible }) {
                                    Icon(
                                        imageVector = if (confirmarClaveVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (confirmarClaveVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (nuevaClave != confirmarClave) {
                                    mensajeErrorClave = "Las contraseñas ingresadas no coinciden."
                                } else {
                                    val resError = onCambiarClave(claveActual, nuevaClave)
                                    if (resError != null) {
                                        mensajeErrorClave = resError
                                    } else {
                                        mensajeExitoClave = "¡Contraseña actualizada exitosamente! Usa tu nueva contraseña en el próximo inicio de sesión."
                                        claveActual = ""
                                        nuevaClave = ""
                                        confirmarClave = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AzulPrincipal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("Guardar Nueva Contraseña", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 3. Tarjeta con Información Específica del Perfil Seleccionado
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

            // 4. Tarjeta de Atribuciones del Rol Activo
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

            // 5. Cerrar Sesión
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
