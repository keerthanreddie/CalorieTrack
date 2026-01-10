package com.example.calorietrack

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietrack.ui.theme.CalorieTrackTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences("gdpr_prefs", MODE_PRIVATE)

        setContent {
            CalorieTrackTheme {

                val context = LocalContext.current
                var showSplash by remember { mutableStateOf(true) }

                // ✅ This is the ONLY place GDPR is checked
                var gdprAccepted by remember {
                    mutableStateOf(prefs.getBoolean("gdpr_accepted", false))
                }

                // ✅ Show splash first
                LaunchedEffect(Unit) {
                    delay(2500)
                    showSplash = false
                }

                when {
                    showSplash -> {
                        SplashScreen()
                    }

                    !gdprAccepted -> {
                        GdprConsentScreen(
                            onAccept = {
                                prefs.edit().putBoolean("gdpr_accepted", true).apply()
                                gdprAccepted = true

                                // Go to Signin after GDPR
                                val intent = Intent(context, SigninActivity::class.java)
                                context.startActivity(intent)
                                (context as? Activity)?.finish()
                            }
                        )
                    }

                    else -> {
                        // GDPR already accepted -> go directly to Signin
                        LaunchedEffect(Unit) {
                            val intent = Intent(context, SigninActivity::class.java)
                            context.startActivity(intent)
                            (context as? Activity)?.finish()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen() {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF81C784),
            Color(0xFFA5D6A7)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Calorie Track",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Track your nutrition effortlessly",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun GdprConsentScreen(onAccept: () -> Unit) {
    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF81C784),
            Color(0xFFA5D6A7)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .statusBarsPadding()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "GDPR Consent",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text =
                    "We value your privacy.\n\n" +
                            "CalorieTrack stores your account details and meal data securely on your device " +
                            "and may use authentication services to manage your account.\n\n" +
                            "Your data is used only for providing core app functionality and is never shared " +
                            "with third parties without consent.\n\n" +
                            "By continuing, you agree to the collection and processing of your data in " +
                            "accordance with GDPR regulations.",
                fontSize = 15.sp,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onAccept,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("I Agree & Continue")
            }
        }
    }
}
