package com.example.oftapp.ui.navigation

import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.oftapp.ui.screens.AtencionesScreen
import com.example.oftapp.ui.screens.DetalleExamenScreen
import com.example.oftapp.ui.screens.ExamenesScreen
import com.example.oftapp.ui.screens.HistorialPacienteScreen
import com.example.oftapp.ui.screens.HomeScreen
import com.example.oftapp.ui.screens.LoginScreen
import com.example.oftapp.ui.screens.PerfilScreen
import com.example.oftapp.ui.theme.AzulContenedor
import com.example.oftapp.ui.theme.AzulPrincipal
import com.example.oftapp.ui.theme.TextoSecundario
import com.example.oftapp.viewmodel.AtencionesViewModel
import com.example.oftapp.viewmodel.ExamenesViewModel
import com.example.oftapp.viewmodel.LoginViewModel

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
    val navController = rememberNavController()
    val loginVm: LoginViewModel = viewModel()
    val examenesVm: ExamenesViewModel = viewModel()
    val atencionesVm: AtencionesViewModel = viewModel()

    val usuarioActual by loginVm.usuarioLogueado.collectAsState()

    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route
    val mostrarBarras = rutaActual != null && rutaActual != Routes.LOGIN

    val titulo = when {
        rutaActual == Routes.INICIO -> "OftApp - Inicio"
        rutaActual == Routes.ATENCIONES -> "Atenciones Médicas"
        rutaActual == Routes.EXAMENES -> "Exámenes Clínicos"
        rutaActual == Routes.HISTORIAL -> "Historial del Paciente"
        rutaActual?.startsWith("detalle") == true -> "Detalle del Examen"
        rutaActual == Routes.PERFIL -> "Perfil y Permisos"
        else -> "OftApp"
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
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        if (rutaActual != Routes.INICIO) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Volver",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AzulPrincipal
                    )
                )
            }
        },
        bottomBar = {
            if (mostrarBarras) {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    itemsMenu.forEach { item ->
                        val seleccionado = rutaActual == item.ruta ||
                                (item.ruta == Routes.EXAMENES && rutaActual?.startsWith("detalle") == true)
                        NavigationBarItem(
                            selected = seleccionado,
                            onClick = {
                                navController.navigate(item.ruta) {
                                    popUpTo(Routes.INICIO)
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(item.icono, contentDescription = item.titulo) },
                            label = {
                                Text(
                                    item.titulo,
                                    fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AzulPrincipal,
                                selectedTextColor = AzulPrincipal,
                                indicatorColor = AzulContenedor,
                                unselectedIconColor = TextoSecundario,
                                unselectedTextColor = TextoSecundario
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
                HomeScreen(
                    usuarioActual = usuarioActual,
                    examenesViewModel = examenesVm,
                    atencionesViewModel = atencionesVm,
                    onVerExamenes = { navController.navigate(Routes.EXAMENES) },
                    onVerAtenciones = { navController.navigate(Routes.ATENCIONES) },
                    onVerHistorial = { navController.navigate(Routes.HISTORIAL) }
                )
            }
            composable(Routes.ATENCIONES) {
                AtencionesScreen(
                    viewModel = atencionesVm,
                    usuarioActual = usuarioActual
                )
            }
            composable(Routes.EXAMENES) {
                ExamenesScreen(
                    viewModel = examenesVm,
                    usuarioActual = usuarioActual,
                    onExamenClick = { id -> navController.navigate(Routes.detalle(id)) }
                )
            }
            composable(Routes.HISTORIAL) {
                HistorialPacienteScreen(
                    viewModel = examenesVm,
                    usuarioActual = usuarioActual,
                    onExamenClick = { id -> navController.navigate(Routes.detalle(id)) }
                )
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
                    onIrPerfil = { navController.navigate(Routes.PERFIL) }
                )
            }
            composable(Routes.PERFIL) {
                PerfilScreen(
                    usuario = usuarioActual,
                    listaPacientes = examenesVm.listaPacientes,
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
}
