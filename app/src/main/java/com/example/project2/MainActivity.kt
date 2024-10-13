package com.example.project2

import kotlin.math.pow
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.project2.ui.theme.Project2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Project2Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    EmissionCalculatorUI(
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun EmissionCalculatorUI(modifier: Modifier = Modifier) {
    var selectedFuel by remember { mutableStateOf("Оберіть тип палива") }
    var fuelAmount by remember { mutableStateOf(TextFieldValue("")) }
    var calculationResult by remember { mutableStateOf("") }

    // Options for the fuel types
    val fuelTypes = listOf(
        "Донецьке газове вугілля GR",
        "Високосірковий мазут 40",
        "Природній газ (Уренгой-Ужгород)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Input for fuel mass
        OutlinedTextField(
            value = fuelAmount,
            onValueChange = { fuelAmount = it },
            label = { Text("Введіть значення палива") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Dropdown for fuel selection
        var isDropdownOpen by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { isDropdownOpen = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(selectedFuel)
            }
            DropdownMenu(
                expanded = isDropdownOpen,
                onDismissRequest = { isDropdownOpen = false }
            ) {
                fuelTypes.forEach { fuel ->
                    DropdownMenuItem(
                        text = { Text(fuel) },
                        onClick = {
                            selectedFuel = fuel
                            isDropdownOpen = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Calculate button
        Button(
            onClick = {
                calculationResult = computeEmissions(selectedFuel, fuelAmount.text)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Порахувати")
        }

        // Show the result
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = calculationResult)
    }
}

// Function to compute emissions
fun computeEmissions(fuel: String, amountText: String): String {
    val amount = amountText.toDoubleOrNull() ?: return "Invalid fuel mass"

    val calorificValue: Double
    val ashContent: Double
    val unburnedFraction: Double
    val ashRatio: Double
    val filterEfficiency: Double

    // Select parameters based on fuel type
    when (fuel) {
        "Донецьке газове вугілля GR" -> {
            calorificValue = 20.47
            ashContent = 25.20
            unburnedFraction = 1.5
            ashRatio = 0.8
            filterEfficiency = 0.985
        }

        "Високосірковий мазут 40" -> {
            calorificValue = 40.40
            ashContent = 0.15
            unburnedFraction = 0.0
            ashRatio = 1.0
            filterEfficiency = 0.985
        }

        "Природній газ (Уренгой-Ужгород)" -> {
            return """
                Particulate emission factor: 0 g/GJ
                Total emission: 0 tons
            """.trimIndent()
        }

        else -> return "Please select a valid fuel type"
    }

    // Compute emission factor
    val emissionFactor = (10.0.pow(6) / calorificValue) * ashRatio * (ashContent / (100 - unburnedFraction)) * (1 - filterEfficiency)

    // Compute total emission
    val totalEmission = 10.0.pow(-6) * emissionFactor * amount * calorificValue

    // Return formatted result
    return """
        Показник емісії=: ${"%.2f".format(emissionFactor)} g/GJ
        Валовий викид: ${"%.2f".format(totalEmission)} tons
    """.trimIndent()
}


@Composable
fun PreviewEmissionCalculatorUI() = Project2Theme {
    EmissionCalculatorUI()
}
