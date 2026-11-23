package com.example.oftapp.ui.screens

import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Accessibility
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
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oftapp.data.local.AccessibilityPreferencesRepository
import com.example.oftapp.data.local.AccessibilitySettings
import com.example.oftapp.model.Paciente
import com.example.oftapp.model.Rol
import com.example.oftapp.model.Usuario
import com.example.oftapp.ui.theme.VerdeContenedor
import com.example.oftapp.ui.theme.VerdeSecundario
import com.example.oftapp.util.TextToSpeechHelper
import kotlinx.coroutines.launch

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

@OptIn(ExperimentalLayoutApi::class)
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

    val scope = rememberCoroutineScope()
    val accessibilityRepo = remember { AccessibilityPreferencesRepository(context) }
    val accessibilitySettings by accessibilityRepo.accessibilitySettingsFlow.collectAsState(initial = AccessibilitySettings())

    val ttsHelper = remember { TextToSpeechHelper(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

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

    val rutFormateado = pacienteRelacionado?.rutFicticio ?: "18.432.109-K"

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Tarjeta con Datos Principales, RUT y Foto de Perfil
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val txt = "Perfil de usuario. Nombre: ${usuario.nombre}. RUT: $rutFormateado. Correo: ${usuario.correo}. Sucursal asignada: ${usuario.sucursalDefault}. Rol activo: ${usuario.rol.titulo}."
                        ttsHelper.hablar(txt)
                    }
                    .semantics { contentDescription = "Ficha principal de datos del usuario ${usuario.nombre}, RUT $rutFormateado" },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                                contentDescription = "Foto de perfil de ${usuario.nombre}",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape),
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
                            color = MaterialTheme.colorScheme.primary,
                            border = BorderStroke(1.5.dp, Color.White),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { launcherFoto.launch("image/*") }
                                .semantics { contentDescription = "Botón para cambiar foto de perfil" }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = null,
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
                                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { if (it.isFocused) ttsHelper.hablar("Casilla seleccionada: Nombre de usuario") }
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
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.semantics { heading() }
                            )
                            IconButton(
                                onClick = { editandoNombre = true },
                                modifier = Modifier
                                    .size(32.dp)
                                    .semantics { contentDescription = "Editar nombre de usuario" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // RUT destacado al inicio
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = "RUT del usuario",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "RUT: $rutFormateado",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = usuario.correo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = usuario.sucursalDefault,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = "Rol Activo: ${usuario.rol.titulo}",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tarjeta de Accesibilidad Visual, Modo Oscuro, Botones Grandes y TextToSpeech
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val modo = when (accessibilitySettings.modoTema) { "CLARO" -> "Modo Claro"; "OSCURO" -> "Modo Oscuro"; else -> "Modo Sistema" }
                        val esc = when (accessibilitySettings.escalaBotones) { 1.15f -> "15 por ciento grande"; 1.25f -> "25 por ciento muy grande"; 1.35f -> "35 por ciento extra grande"; else -> "100 por ciento normal" }
                        val contra = if (accessibilitySettings.altoContraste) "activado" else "desactivado"
                        val voz = if (accessibilitySettings.lecturaVozAutomatica) "activada" else "desactivada"
                        val txt = "Ajustes de accesibilidad visual establecidos. Modo de tema: $modo. Modo alto contraste: $contra. Escala de tamaño de botones: $esc. Lectura por voz automática: $voz."
                        ttsHelper.hablar(txt)
                    }
                    .semantics { contentDescription = "Sección de configuración de accesibilidad visual, tema y sintetizador de voz" },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Accessibility, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Accesibilidad Visual (WCAG 2.2 AA)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.semantics { heading() }
                            )
                            Text(
                                text = "Configuración guardada en DataStore",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // 1. Selector de Modo de Tema (Claro / Oscuro / Sistema)
                    Text(
                        text = "Modo de Tema (Apariencia)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val opcionesTema = listOf(
                            "SISTEMA" to "Sistema ⚙️",
                            "CLARO" to "Claro ☀️",
                            "OSCURO" to "Oscuro 🌙"
                        )
                        opcionesTema.forEach { (codigo, etiqueta) ->
                            val seleccionado = accessibilitySettings.modoTema == codigo
                            FilterChip(
                                selected = seleccionado,
                                onClick = {
                                    scope.launch { accessibilityRepo.setModoTema(codigo) }
                                    ttsHelper.hablar("Casilla seleccionada: Modo de tema $etiqueta")
                                },
                                label = { Text(etiqueta, fontWeight = FontWeight.Bold, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // 2. Modo Alto Contraste
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Modo Alto Contraste (AAA)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Fondo negro absoluto con texto amarillo/blanco de alto contraste", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = accessibilitySettings.altoContraste,
                            onCheckedChange = { check ->
                                scope.launch {
                                    accessibilityRepo.setAltoContraste(check)
                                }
                                ttsHelper.hablar(if (check) "Casilla activada: Modo alto contraste" else "Casilla desactivada: Modo alto contraste")
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.semantics { contentDescription = "Interruptor para activar o desactivar el Modo Alto Contraste" }
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    // 3. Tamaño de Botones Grandes (15%, 25%, 35%)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Tamaño de Botones Táctiles (Escala)",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = "Amplía la superficie de toque de botones e íconos para baja visión",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val escalas = listOf(
                            1.0f to "100% Normal",
                            1.15f to "+15% Grande",
                            1.25f to "+25% Muy Grande",
                            1.35f to "+35% Extra Grande"
                        )
                        escalas.forEach { (esc, label) ->
                            val sel = accessibilitySettings.escalaBotones == esc
                            FilterChip(
                                selected = sel,
                                onClick = {
                                    scope.launch { accessibilityRepo.setEscalaBotones(esc) }
                                    ttsHelper.hablar("Casilla seleccionada: Escala de tamaño de botones $label")
                                },
                                label = { Text(label, fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // 4. Lectura por Voz Automática / TextToSpeech
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Lectura por Voz Automática (TTS)", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text("Sintetizador nativo para lecturas clínicas sin pantalla", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = accessibilitySettings.lecturaVozAutomatica,
                            onCheckedChange = { check ->
                                scope.launch {
                                    accessibilityRepo.setLecturaVozAutomatica(check)
                                }
                                ttsHelper.hablar(if (check) "Casilla activada: Lectura por voz automática" else "Casilla desactivada: Lectura por voz automática")
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.semantics { contentDescription = "Interruptor de lectura por voz automática en diagnósticos" }
                        )
                    }

                    Spacer(Modifier.height(10.dp))

                    // Botón de prueba de TextToSpeech
                    OutlinedButton(
                        onClick = {
                            val modo = when (accessibilitySettings.modoTema) { "CLARO" -> "Modo Claro"; "OSCURO" -> "Modo Oscuro"; else -> "Modo Sistema" }
                            val esc = when (accessibilitySettings.escalaBotones) { 1.15f -> "15 por ciento grande"; 1.25f -> "25 por ciento muy grande"; 1.35f -> "35 por ciento extra grande"; else -> "100 por ciento normal" }
                            val contra = if (accessibilitySettings.altoContraste) "activado" else "desactivado"
                            val mensajePrueba = "Probando sintetizador de voz nativo en OftApp para la cuenta de ${usuario.nombre}, RUT $rutFormateado. Permisos activos: ${usuario.rol.titulo}. Ajustes actuales: $modo, alto contraste $contra, botones al $esc."
                            ttsHelper.hablar(mensajePrueba)
                            Toast.makeText(context, "Reproduciendo voz nativa accesible...", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .semantics { contentDescription = "Botón para probar la reproducción de voz del sintetizador nativo TextToSpeech" }
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("🔊 Probar Sintetizador de Voz Nativo (TTS)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // 2. Tarjeta de Cambio de Contraseña
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        ttsHelper.hablar("Sección Seguridad y Contraseña. Opciones para cambiar la clave de acceso de esta cuenta.")
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { mostrarCambioClave = !mostrarCambioClave }
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Seguridad y Contraseña",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.semantics { heading() }
                            )
                            Text(
                                text = "Cambiar la clave de acceso de esta cuenta",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { mostrarCambioClave = !mostrarCambioClave }) {
                            Icon(
                                imageVector = if (mostrarCambioClave) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
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
                            leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { if (it.isFocused) ttsHelper.hablar("Casilla seleccionada: Contraseña actual") }
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
                            leadingIcon = { Icon(Icons.Default.LockReset, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { if (it.isFocused) ttsHelper.hablar("Casilla seleccionada: Nueva contraseña") }
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
                            leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
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
                            modifier = Modifier
                                .fillMaxWidth()
                                .onFocusChanged { if (it.isFocused) ttsHelper.hablar("Casilla seleccionada: Confirmar nueva contraseña") }
                        )

                        Spacer(Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (nuevaClave != confirmarClave) {
                                    mensajeErrorClave = "Las contraseñas ingresadas no coinciden."
                                    ttsHelper.hablar("Error: Las contraseñas ingresadas no coinciden")
                                } else {
                                    val resError = onCambiarClave(claveActual, nuevaClave)
                                    if (resError != null) {
                                        mensajeErrorClave = resError
                                        ttsHelper.hablar("Error: $resError")
                                    } else {
                                        mensajeExitoClave = "¡Contraseña actualizada exitosamente! Usa tu nueva contraseña en el próximo inicio de sesión."
                                        ttsHelper.hablar("Contraseña actualizada exitosamente")
                                        claveActual = ""
                                        nuevaClave = ""
                                        confirmarClave = ""
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val txt = when (usuario.rol) {
                            Rol.PACIENTE -> "Ficha médica del paciente ${pacienteRelacionado?.nombre}. RUT: $rutFormateado. Edad: ${pacienteRelacionado?.edad} años. Teléfono: ${pacienteRelacionado?.telefonoFicticio}. Antecedentes: ${pacienteRelacionado?.antecedentes}."
                            Rol.MEDICO_OFTALMOLOGO -> "Ficha del especialista médico ${usuario.nombre}. RUT: $rutFormateado. Especialidad: Oftalmología Clínica y Cirugía Refractiva. Registro Minsal N° 483.920. Cargo: Médico Oftalmólogo Senior en ${usuario.sucursalDefault}."
                            Rol.TECNOLOGO_MEDICO -> "Ficha de la tecnólogo médico ${usuario.nombre}. RUT: $rutFormateado. Especialidad: Tecnología Médica Oftalmológica. Registro Minsal N° 592.011. Área de diagnóstico: Tomografía, Campimetría y Tonometría."
                            Rol.PERSONAL_ADMINISTRATIVO -> "Ficha del funcionario administrativo ${usuario.nombre}. RUT: $rutFormateado. Cargo: Encargado de Admisión y Recepción de Pacientes en ${usuario.sucursalDefault}."
                            Rol.ADMINISTRADOR -> "Ficha del administrador general ${usuario.nombre}. RUT: $rutFormateado. Código funcionario: SYS-ADMIN-001. Cobertura: Todas las sucursales y catálogos."
                        }
                        ttsHelper.hablar(txt)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column {
                    val colorEncabezado = if (esPaciente) VerdeSecundario else MaterialTheme.colorScheme.primary

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
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.semantics { heading() }
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
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        text = "RUT Ficticio: ${p.rutFicticio} · Tel: ${p.telefonoFicticio}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Rol.MEDICO_OFTALMOLOGO -> {
                                DetailRow("RUT Profesional", rutFormateado)
                                DetailRow("Especialidad", "Oftalmología Clínica y Cirugía Refractiva")
                                DetailRow("Registro MINSAL", "N° 483.920")
                                DetailRow("Cargo Actual", "Médico Oftalmólogo Senior")
                                DetailRow("Sucursal Principal", usuario.sucursalDefault)
                                DetailRow("Experiencia Clinical", "14 años de ejercicio profesional")
                            }

                            Rol.TECNOLOGO_MEDICO -> {
                                DetailRow("RUT Profesional", rutFormateado)
                                DetailRow("Especialidad", "Tecnología Médica Mención Oftalmología")
                                DetailRow("Registro MINSAL", "N° 592.011")
                                DetailRow("Cargo Actual", "Coordinadora de Exámenes Diagnósticos")
                                DetailRow("Sucursal Principal", usuario.sucursalDefault)
                                DetailRow("Área de Diagnóstico", "Tomografía OCT, Campimetría y Tonometría")
                            }

                            Rol.PERSONAL_ADMINISTRATIVO -> {
                                DetailRow("RUT Funcionario", rutFormateado)
                                DetailRow("Cargo", "Encargado de Admisión y Recepción de Pacientes")
                                DetailRow("Código Funcionario", "ADM-2024-08")
                                DetailRow("Departamento", "Atención al Paciente y Entrega de Resultados")
                                DetailRow("Sucursal Asignada", usuario.sucursalDefault)
                                DetailRow("Nivel de Sistema", "Gestión de Entrega y Consultas")
                            }

                            Rol.ADMINISTRADOR -> {
                                DetailRow("RUT Administrador", rutFormateado)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val txt = "Permisos habilitados para el rol ${usuario.rol.titulo}: ${usuario.rol.descripcion}. Crear atención: ${if (usuario.rol.puedeCrearAtencion()) "sí" else "no"}. Crear examen: ${if (usuario.rol.puedeCrearExamen()) "sí" else "no"}. Editar observación médica: ${if (usuario.rol.puedeEditarObservacionMedica()) "sí" else "no"}."
                        ttsHelper.hablar(txt)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Permisos Habilitados para el Rol",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.semantics { heading() }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = usuario.rol.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "• Registrar atención médica: ${if (usuario.rol.puedeCrearAtencion()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeCrearAtencion()) VerdeSecundario else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Crear exámenes simulados: ${if (usuario.rol.puedeCrearExamen()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeCrearExamen()) VerdeSecundario else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Adjuntar evidencias PDF/Imagen: ${if (usuario.rol.puedeAdjuntarDocumentos()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeAdjuntarDocumentos()) VerdeSecundario else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Editar observación médica: ${if (usuario.rol.puedeEditarObservacionMedica()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeEditarObservacionMedica()) VerdeSecundario else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "• Actualizar estado de entrega: ${if (usuario.rol.puedeActualizarEstadoEntrega()) "Sí" else "No"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (usuario.rol.puedeActualizarEstadoEntrega()) VerdeSecundario else MaterialTheme.colorScheme.onSurfaceVariant,
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = valStr,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}
