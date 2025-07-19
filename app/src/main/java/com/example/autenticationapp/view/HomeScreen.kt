package com.example.autenticationapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.autenticationapp.data.AuthRepository
import com.example.autenticationapp.viewmodel.AuthViewModel
import com.example.autenticationapp.viewmodel.AuthViewModelFactory

@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    )

    var userName by remember { mutableStateOf("Carregando...") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.getUserName { name ->
            userName = name ?: "Usuário"
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bem-vindo, $userName!",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.logout(context)
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sair")
            }
        }
    }
}