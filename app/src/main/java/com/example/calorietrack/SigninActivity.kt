package com.example.calorietrack

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietrack.ui.theme.CalorieTrackTheme
import com.google.firebase.auth.FirebaseAuth

class SigninActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CalorieTrackTheme {
                SignInScreen()
            }
        }
    }
}

@Composable
fun SignInScreen() {

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF81C784),
            Color(0xFFA5D6A7)
        )
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }

    fun validateInputs(): Boolean {
        val e = email.trim()
        if (e.isEmpty()) {
            errorText = "Please enter your email."
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(e).matches()) {
            errorText = "Please enter a valid email address."
            return false
        }
        if (password.isBlank()) {
            errorText = "Please enter your password."
            return false
        }
        if (password.length < 6) {
            errorText = "Password must be at least 6 characters."
            return false
        }
        errorText = null
        return true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .statusBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Calorie Track",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Sign in to continue tracking your nutrition",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    errorText = null
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    errorText = null
                },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )

            if (errorText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorText!!,
                    color = Color(0xFFFFEBEE), // light error tint on green background
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (!validateInputs()) return@Button

                    isLoading = true
                    val e = email.trim()

                    auth.signInWithEmailAndPassword(e, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Signed in ", Toast.LENGTH_SHORT).show()

                                val intent = Intent(context, DashboardActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                context.startActivity(intent)
                            } else {
                                errorText = task.exception?.localizedMessage
                                    ?: "Sign in failed. Please try again."
                            }
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Signing in...")
                } else {
                    Text("Sign In")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = {
                    val intent = Intent(context, SignUpActivity::class.java)
                    context.startActivity(intent)
                },
                enabled = !isLoading
            ) {
                Text(
                    text = "Don't have an account? Sign Up",
                    color = Color.White
                )
            }
        }
    }
}
