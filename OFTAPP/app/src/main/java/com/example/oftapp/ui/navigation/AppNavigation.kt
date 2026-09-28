package com.example.oftapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.oftapp.ui.screens.DetalleExamenScreen
import com.example.oftapp.ui.screens.ExamenesScreen
import com.example.oftapp.ui.screens.HomeScreen
import com.example.oftapp.ui.screens.LoginScreen
import com.example.oftapp.ui.screens.PerfilScreen
import com.example.oftapp.viewmodel.ExamenesViewModel
import com.example.oftapp.viewmodel.LoginViewModel

private data class ItemMenu(val ruta: String, val titulo: String, val icono: ImageVector)

private val itemsMenu = listOf(
    ItemMenu(Routes.INICIO, "Inicio", Icons.Default.Home),
    ItemMenu(Routes.EXAMENES, "Exámenes", Icons.Default.List),
    ItemMenu(Routes.PERFIL, "Perfil", Icons.Default.Person)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OftAppNavigation() {
    val navController = rememberNavController()
    val loginVm: LoginViewModel = viewModel()
    val examenesVm: ExamenesViewModel = viewModel()

    val backStack by navController.currentBackStackEntryAsState()
    val rutaActual = backStack?.destination?.route
    val mostrarBarras = rutaActual != null && rutaActual != Routes.LOGIN

    val titulo = when (rutaActual) {
        Routes.INICIO -> "OftApp"
        Routes.EXAMENES -> "Mis exámenes"
        Routes.DETALLE -> "Detalle del examen"
        Routes.PERFIL -> "Mi perfil"
        else -> "OftApp"
    }

    Scaffold(
        topBar = {
            if (mostrarBarras) {
                TopAppBar(
                    title = { Text(titulo) },
                    navigationIcon = {
                        if (rutaActual != Routes.INICIO) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                            }
                        }
                    }
                )
            }
        },
        bottomBar = {
            if (mostrarBarras) {
                NavigationBar {
                    itemsMenu.forEach { item ->
                        val seleccionado = rutaActual == item.ruta ||
                            (item.ruta == Routes.EXAMENES && rutaActual == Routes.DETALLE)
                        NavigationBarItem(
                            selected = seleccionado,
                            onClick = {
                                navController.navigate(item.ruta) {
                                    popUpTo(Routes.INICIO)
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(item.icono, contentDescription = item.titulo) },
                            label = { Text(item.titulo) }
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
                    viewModel = examenesVm,
                    nombreUsuario = loginVm.usuario.nombre,
                    onVerExamenes = { navController.navigate(Routes.EXAMENES) }
                )
            }
            composable(Routes.EXAMENES) {
                ExamenesScreen(
                    viewModel = examenesVm,
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
                    onVolver = { navController.popBackStack() },
                    onIrPerfil = { navController.navigate(Routes.PERFIL) }
                )
            }
            composable(Routes.PERFIL) {
                PerfilScreen(
                    usuario = loginVm.usuario,
                    paciente = examenesVm.paciente,
                    onCerrarSesion = {
                        loginVm.limpiar()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.INICIO) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
