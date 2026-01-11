package com.example.calorietrack

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calorietrack.room.CalorieTrackDatabase
import com.example.calorietrack.room.Meal
import com.example.calorietrack.ui.theme.CalorieTrackTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddMealActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalorieTrackTheme {
                AddMealScreen()
            }
        }
    }
}

@Composable
fun AddMealScreen() {

    val context = LocalContext.current
    val activity = context as? Activity

    val gradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF4CAF50),
            Color(0xFF81C784),
            Color(0xFFA5D6A7)
        )
    )

    var name by remember { mutableStateOf("") }
    var proteinText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val db = remember { CalorieTrackDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()

    // ✅ Use SimpleDateFormat for broader Android support
    val today = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // 🔝 Top bar: Back + Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { activity?.finish() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }

                Text(
                    text = "Add Meal",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    errorText = null
                },
                label = { Text("Meal name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = proteinText,
                onValueChange = {
                    // keep only digits (optional safety)
                    proteinText = it.filter { ch -> ch.isDigit() }
                    errorText = null
                },
                label = { Text("Protein (g)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (errorText != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorText!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val protein = proteinText.toIntOrNull()

                    when {
                        name.trim().isEmpty() -> {
                            errorText = "Please enter a meal name."
                        }
                        protein == null || protein <= 0 -> {
                            errorText = "Please enter a valid protein amount greater than 0."
                        }
                        else -> {
                            scope.launch {
                                db.mealDao().insertMeal(
                                    Meal(
                                        name = name.trim(),
                                        proteinGrams = protein,
                                        date = today
                                    )
                                )

                                Toast.makeText(context, "Meal saved ", Toast.LENGTH_SHORT).show()

                                //  IMPORTANT: tell Dashboard to refresh
                                activity?.setResult(Activity.RESULT_OK)
                                activity?.finish()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Save Meal")
            }
        }
    }
}
