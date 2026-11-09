package com.upchiapas.kt_template

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.upchiapas.kt_template.auth.presentation.login.LoginScreen
import com.upchiapas.kt_template.auth.presentation.register.RegisterScreen
import com.upchiapas.kt_template.ui.theme.Kt_templateTheme
import com.upchiapas.kt_template.gym.presentation.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as KtApplication).container

        // con esto se define la pantalla principal dependiendo si ees que hay una secion activa pa
        val startDestination = if (container.authPreferences.hasToken()) "home" else "login"

        setContent {
            Kt_templateTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateToRegister = {
                                navController.navigate("register")
                            }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            },
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(
                            onRutinaClick = { /* Paso 10: detalle */ },
                            onAddClick = { /* Paso 11: formulario */ },
                            onEditClick = { /* Paso 11: formulario */ },
                            onLoggedOut = {
                                navController.navigate("login") { popUpTo(0) }
                            }
                        )
                    }
                }
            }
        }
    }
}