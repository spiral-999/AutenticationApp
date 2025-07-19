package com.example.autenticationapp.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.autenticationapp.data.AuthRepository
import com.example.autenticationapp.viewmodel.AuthViewModel
import com.example.autenticationapp.viewmodel.AuthViewModelFactory

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    )

    var email by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Recuperar Senha", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Digite seu email para receber o link de redefinição.", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            message?.let {
                val color = if (isError) MaterialTheme.colorScheme.error else Color.Green
                Text(it, color = color)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    isLoading = true
                    message = null
                    viewModel.resetPassword(email) { success ->
                        if (success) {
                            message = "Email de recuperação enviado com sucesso!"
                            isError = false
                        } else {
                            message = "Erro ao enviar email. Verifique o endereço digitado."
                            isError = true
                        }
                        isLoading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Enviar Email de Recuperação")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            ClickableText(
                text = AnnotatedString("Voltar ao Login"),
                onClick = { navController.popBackStack() },
                style = TextStyle(color = MaterialTheme.colorScheme.primary)
            )
        }
    }
}