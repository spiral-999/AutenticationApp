package com.example.autenticationapp.view

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts 
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.autenticationapp.data.AuthRepository
import com.example.autenticationapp.viewmodel.AuthViewModel
import com.example.autenticationapp.viewmodel.AuthViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AuthRepository())
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coroutineScope.launch {
                val signInCredential = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val googleIdToken = signInCredential.result.idToken
                    if (googleIdToken != null) {
                        isLoading = true
                        viewModel.loginWithGoogle(googleIdToken) { success ->
                            if (success) {
                                navController.navigate("home_screen") {
                                    popUpTo("login_screen") { inclusive = true }
                                }
                            } else {
                                errorMessage = "Erro no login com Google."
                            }
                            isLoading = false
                        }
                    }
                } catch (e: ApiException) {
                    errorMessage = "Erro ao autenticar com Google: ${e.message}"
                    Log.e("LoginScreen", "Google sign-in failed", e)
                }
            }
        } else {
            errorMessage = "Login com Google cancelado ou falhou."
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
            Text("Bem-vindo!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Senha") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    viewModel.login(email, password) { success ->
                        if (success) {
                            navController.navigate("home_screen") {
                                popUpTo("login_screen") { inclusive = true }
                            }
                        } else {
                            errorMessage = "Email ou senha inválidos."
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
                    Text("Entrar")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    isLoading = true
                    errorMessage = null
                    val signInClient = viewModel.getGoogleSignInClient(context)
                    coroutineScope.launch {
                        val signInIntent = signInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar com Google")
            }

            Spacer(modifier = Modifier.height(16.dp))

            ClickableText(
                text = AnnotatedString("Criar Conta"),
                onClick = { navController.navigate("register_screen") },
                style = TextStyle(color = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.height(8.dp))
            ClickableText(
                text = AnnotatedString("Esqueci minha senha"),
                onClick = { navController.navigate("forgot_password_screen") },
                style = TextStyle(color = MaterialTheme.colorScheme.secondary)
            )
        }
    }
}