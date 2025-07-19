package com.example.autenticationapp.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.autenticationapp.data.AuthRepository
import com.example.autenticationapp.viewmodel.AuthViewModel
import com.example.autenticationapp.viewmodel.AuthViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    )

    LaunchedEffect(key1 = true) {
        val destination = if (viewModel.isUserLoggedIn()) Screen.Home.route else Screen.Login.route

        navController.navigate(destination) {
            popUpTo(Screen.Splash.route) {
                inclusive = true
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
    }
}