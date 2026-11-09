package com.upchiapas.kt_template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.upchiapas.kt_template.auth.presentation.login.LoginScreen
import com.upchiapas.kt_template.auth.presentation.register.RegisterScreen
import com.upchiapas.kt_template.core.navigation.Routes
import com.upchiapas.kt_template.gym.presentation.detail.RutinaDetailScreen
import com.upchiapas.kt_template.gym.presentation.form.RutinaFormScreen
import com.upchiapas.kt_template.gym.presentation.home.HomeScreen
import com.upchiapas.kt_template.ui.theme.Kt_templateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Se calcula UNA vez, antes de setContent, para que el grafo no se reconstruya al iniciar sesión
        val container = (application as KtApplication).container
        val startDestination = if (container.authPreferences.hasToken()) Routes.HOME else Routes.LOGIN

        setContent {
            Kt_templateTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = startDestination) {

                    composable(Routes.LOGIN) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            },
                            onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                        )
                    }

                    composable(Routes.REGISTER) {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }

                    composable(Routes.HOME) {
                        HomeScreen(
                            onRutinaClick = { id -> navController.navigate(Routes.detail(id)) },
                            onAddClick = { navController.navigate(Routes.form()) },
                            onEditClick = { id -> navController.navigate(Routes.form(id)) },
                            onLoggedOut = {
                                navController.navigate(Routes.LOGIN) { popUpTo(0) }
                            }
                        )
                    }

                    composable(
                        route = Routes.DETAIL,
                        arguments = listOf(
                            navArgument(Routes.RUTINA_ID) { type = NavType.IntType }
                        )
                    ) {
                        RutinaDetailScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onEditClick = { id -> navController.navigate(Routes.form(id)) }
                        )
                    }

                    composable(
                        route = Routes.FORM,
                        arguments = listOf(
                            navArgument(Routes.RUTINA_ID) {
                                type = NavType.IntType
                                defaultValue = Routes.NO_ID
                            }
                        )
                    ) {
                        RutinaFormScreen(
                            onSaved = { navController.popBackStack() },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}