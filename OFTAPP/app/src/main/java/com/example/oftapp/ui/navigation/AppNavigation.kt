package com.example.oftapp.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.oftapp.data.local.AccessibilityPreferencesRepository
import com.example.oftapp.data.local.AccessibilitySettings
import com.example.oftapp.model.Rol
import com.example.oftapp.model.Usuario
import com.example.oftapp.ui.screens.AtencionesScreen
import com.example.oftapp.ui.screens.DetalleExamenScreen
import com.example.oftapp.ui.screens.ExamenesScreen
import com.example.oftapp.ui.screens.HistorialPacienteScreen
import com.example.oftapp.ui.screens.HomeScreen
import com.example.oftapp.ui.screens.LoginScreen
import com.example.oftapp.ui.screens.PerfilScreen
import com.example.oftapp.ui.theme.LocalHighContrast
import com.example.oftapp.util.TextToSpeechHelper
import com.example.oftapp.viewmodel.AtencionesViewModel
import com.example.oftapp.viewmodel.ExamenesViewModel
import com.example.oftapp.viewmodel.LoginViewModel
import kotlinx.coroutines.launch

private data class ItemMenu(val ruta: String, val titulo: String, val icono: ImageVector)

private val itemsMenu = listOf(
    ItemMenu(Routes.INICIO, "Inicio", Icons.Default.Home),
    ItemMenu(Routes.ATENCIONES, "Atenciones", Icons.Default.CalendarMonth),
    ItemMenu(Routes.EXAMENES, "Exámenes", Icons.Default.Assignment),
    ItemMenu(Routes.HISTORIAL, "Historial", Icons.Default.History),
    ItemMenu(Routes.PERFIL, "Perfil", Icons.Default.Person)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OftAppNavigation() {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    val loginVm: LoginViewModel = viewModel()
    val examenesVm: ExamenesViewModel = viewModel()
    val atencionesVm: AtencionesViewModel = viewModel()

    val usuarioActual by loginVm.usuarioLogueado.collectAsState()

    val todosExamenes by examenesVm.examenes.collectAsState()
    val todasAtenciones by atencionesVm.atenciones.collectAsState()

    val accessibilityRepo = remember { AccessibilityPreferencesRepository(context) }
    val accessibilitySettings by accessibilityRepo.accessibilitySettingsFlow.collectAsState(initial = AccessibilitySettings())

    val ttsHelper = remember { TextToSpeechHelper(context) }
    LaunchedEffect(accessibilitySettings.lecturaVozAutomatica) {
        ttsHelper.ttsHabilitado = accessibilitySettings.lecturaVozAutomatica
    }
    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route
    val mostrarBarras = rutaActual != null && rutaActual != Routes.LOGIN

    // Estado del Pager horizontal para deslizar entre secciones
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 5 })

    // Datos calculados para lectura de voz completa
    val esPaciente = usuarioActual.rol == Rol.PACIENTE
    val nombrePacienteSesion = usuarioActual.nombre.lowercase()
    val examenesVisibles = if (esPaciente) todosExamenes.filter { it.pacienteId == 4 } else todosExamenes
    val atencionesVisibles = if (esPaciente) todasAtenciones.filter { it.pacienteId == 4 } else todasAtenciones
    val pendientes = examenesVisibles.count { it.estado == "Pendiente" }
    val realizados = examenesVisibles.count { it.estado == "Realizado" }
    val atencionProx = atencionesVisibles.firstOrNull { it.estado == "Programada" } ?: atencionesVisibles.firstOrNull()

    val titulo = when {
        rutaActual == Routes.LOGIN -> "OftApp - Inicio de Sesión"
        rutaActual?.startsWith("detalle") == true -> "Detalle del Examen"
        else -> when (pagerState.currentPage) {
            0 -> "OftApp - Inicio"
            1 -> "Atenciones Médicas"
            2 -> "Exámenes Clínicos"
            3 -> "Historial del Paciente"
            4 -> "Perfil y Permisos"
            else -> "OftApp"
        }
    }

    // Dictado por voz dinámico TextToSpeech detallado con datos en tiempo real al deslizar o cambiar de sección
    LaunchedEffect(pagerState.currentPage, rutaActual, accessibilitySettings) {
        if (accessibilitySettings.lecturaVozAutomatica) {
            val mensajeDictado = if (rutaActual?.startsWith("detalle") == true) {
                "Sección Detalle del Examen Clínico Seleccionado."
            } else if (rutaActual == Routes.LOGIN) {
                "Sección Inicio de Sesión de OftApp. Ingrese sus credenciales o seleccione una cuenta ficticia de prueba por rol."
            } else {
                when (pagerState.currentPage) {
                    0 -> {
                        val proxInfo = if (atencionProx != null) "Próxima atención agendada: ${atencionProx.motivo}, fecha ${atencionProx.fecha}, especialista ${atencionProx.profesional}, sucursal ${atencionProx.sucursal}." else "Sin atenciones médicas programadas."
                        "Sección Inicio de OftApp. Hola, ${usuarioActual.nombre}. Rol activo: ${usuarioActual.rol.titulo}. Resumen clínico: $pendientes exámenes pendientes por realizar y $realizados exámenes completados. $proxInfo"
                    }
                    1 -> {
                        val listaAtencionesDetalle = atencionesVisibles.take(3).joinToString("; ") { "${it.motivo} con paciente ${it.pacienteNombre} el ${it.fecha} en ${it.sucursal}" }
                        "Sección Atenciones Médicas. Hay un total de ${atencionesVisibles.size} atenciones registradas. Atenciones destacadas: $listaAtencionesDetalle."
                    }
                    2 -> {
                        val listaExamenesDetalle = examenesVisibles.take(3).joinToString("; ") { "${it.tipoExamen} de ${it.pacienteNombre}, estado ${it.estado}" }
                        "Sección Exámenes Clínicos. Listado con ${examenesVisibles.size} exámenes registrados. $pendientes pendientes de firma y $realizados realizados. Exámenes principales: $listaExamenesDetalle."
                    }
                    3 -> "Sección Historial Clínico del Paciente. Ficha médica de ${usuarioActual.nombre} con registro histórico de diagnósticos."
                    4 -> {
                        val modoTemaNombre = when (accessibilitySettings.modoTema) { "CLARO" -> "Modo Claro"; "OSCURO" -> "Modo Oscuro"; else -> "Modo Sistema" }
                        val porcentajeBotones = when (accessibilitySettings.escalaBotones) { 1.15f -> "15 por ciento grande"; 1.25f -> "25 por ciento muy grande"; 1.35f -> "35 por ciento extra grande"; else -> "100 por ciento normal" }
                        val altoContrasteTexto = if (accessibilitySettings.altoContraste) "Alto contraste activado" else "Alto contraste desactivado"
                        val ttsTexto = if (accessibilitySettings.lecturaVozAutomatica) "Lectura de voz automática activada" else "Lectura de voz automática desactivada"
                        "Sección Perfil de Usuario. Usuario: ${usuarioActual.nombre}, Correo: ${usuarioActual.correo}, Rol: ${usuarioActual.rol.titulo}, Sucursal: ${usuarioActual.sucursalDefault}. Configuraciones de accesibilidad actuales: $modoTemaNombre, $altoContrasteTexto, Tamaño de botones al $porcentajeBotones, y $ttsTexto."
                    }
                    else -> "Sección $titulo."
                }
            }
            ttsHelper.hablar(mensajeDictado)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (mostrarBarras) {
                TopAppBar(
                    title = {
                        Text(
                            text = titulo,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        if (rutaActual?.startsWith("detalle") == true) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.semantics { contentDescription = "Botón para volver a la pantalla anterior" }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (LocalHighContrast.current) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            if (mostrarBarras && rutaActual?.startsWith("detalle") != true) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    itemsMenu.forEachIndexed { index, item ->
                        val seleccionado = pagerState.currentPage == index
                        NavigationBarItem(
                            selected = seleccionado,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            icon = { Icon(item.icono, contentDescription = "Menú ${item.titulo}") },
                            label = {
                                Text(
                                    item.titulo,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = loginVm,
                    onLoginExitoso = {
                        navController.navigate(Routes.INICIO) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.INICIO) {
                // Pager horizontal con soporte de deslizamiento lateral hacia la derecha / izquierda
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> HomeScreen(
                            usuarioActual = usuarioActual,
                            examenesViewModel = examenesVm,
                            atencionesViewModel = atencionesVm,
                            onVerExamenes = { scope.launch { pagerState.animateScrollToPage(2) } },
                            onVerAtenciones = { scope.launch { pagerState.animateScrollToPage(1) } },
                            onVerHistorial = { scope.launch { pagerState.animateScrollToPage(3) } }
                        )
                        1 -> AtencionesScreen(
                            viewModel = atencionesVm,
                            usuarioActual = usuarioActual
                        )
                        2 -> ExamenesScreen(
                            viewModel = examenesVm,
                            usuarioActual = usuarioActual,
                            onExamenClick = { id -> navController.navigate(Routes.detalle(id)) }
                        )
                        3 -> HistorialPacienteScreen(
                            viewModel = examenesVm,
                            usuarioActual = usuarioActual,
                            onExamenClick = { id -> navController.navigate(Routes.detalle(id)) }
                        )
                        4 -> PerfilScreen(
                            usuario = usuarioActual,
                            listaPacientes = examenesVm.listaPacientes,
                            onActualizarNombre = { nuevoNombre ->
                                loginVm.actualizarNombre(nuevoNombre)
                            },
                            onActualizarFoto = { nuevaFotoUri ->
                                loginVm.actualizarFotoPerfil(nuevaFotoUri)
                            },
                            onCambiarClave = { claveActual, nuevaClave ->
                                loginVm.cambiarClave(claveActual, nuevaClave)
                            },
                            onCerrarSesion = {
                                loginVm.cerrarSesion()
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.INICIO) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
            composable(
                route = Routes.DETALLE,
                arguments = listOf(navArgument("examenId") { type = NavType.IntType })
            ) { entry ->
                val id = entry.arguments?.getInt("examenId") ?: 0
                DetalleExamenScreen(
                    examen = examenesVm.obtenerExamen(id),
                    usuarioActual = usuarioActual,
                    viewModel = examenesVm,
                    onVolver = { navController.popBackStack() },
                    onIrPerfil = {
                        navController.popBackStack()
                        scope.launch { pagerState.animateScrollToPage(4) }
                    }
                )
            }
        }
    }
}
