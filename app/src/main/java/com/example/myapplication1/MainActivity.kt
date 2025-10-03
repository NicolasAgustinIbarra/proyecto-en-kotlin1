package com.example.myapplication1
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Color(0xFF00C853),
                    secondary = Color(0xFF03DAC6),
                    background = Color(0xFF121212),
                    surface = Color(0xFF1E1E1E)
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    var saldoActual by remember { mutableStateOf(10000.0) }

    NavHost(navController = navController, startDestination = "billetera") {
        composable("billetera") {
            BilleteraScreen(navController, saldoActual) { nuevoSaldo ->
                saldoActual = nuevoSaldo
            }
        }
        composable("comprobante/{monto}") { backStackEntry ->
            val monto = backStackEntry.arguments?.getString("monto") ?: "0"
            ComprobanteScreen(monto, saldoActual)
        }
    }
}

@Composable
fun BilleteraScreen(
    navController: NavHostController,
    saldoActual: Double,
    onSaldoActualizado: (Double) -> Unit
) {
    var montoRetiro by remember { mutableStateOf("") }
    var mensajeError by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1B5E20),
                        Color(0xFF2E7D32),
                        Color(0xFF388E3C)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Billetera",
                        tint = Color(0xFF00C853),
                        modifier = Modifier
                            .size(64.dp)
                            .padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Mi Billetera",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00C853),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Saldo disponible",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "$ ${String.format("%.2f", saldoActual)}",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    HorizontalDivider(
                        color = Color(0xFF00C853).copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = "Retirar dinero",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = montoRetiro,
                        onValueChange = {
                            montoRetiro = it
                            mensajeError = ""
                        },
                        label = { Text("Monto a retirar", color = Color.White.copy(alpha = 0.7f)) },
                        placeholder = { Text("0.00", color = Color.White.copy(alpha = 0.4f)) },
                        prefix = { Text("$ ", color = Color.White) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF00C853),
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = Color(0xFF00C853)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    if (mensajeError.isNotEmpty()) {
                        Text(
                            text = mensajeError,
                            color = Color(0xFFFF6B6B),
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val monto = montoRetiro.toDoubleOrNull()

                            when {
                                montoRetiro.isEmpty() -> {
                                    mensajeError = "Ingresa un monto"
                                }
                                monto == null || monto <= 0 -> {
                                    mensajeError = "Ingresa un monto válido"
                                }
                                monto > saldoActual -> {
                                    mensajeError = "Saldo insuficiente"
                                }
                                else -> {
                                    val nuevoSaldo = saldoActual - monto
                                    onSaldoActualizado(nuevoSaldo)
                                    navController.navigate("comprobante/$monto")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF00C853)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            "RETIRAR",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ComprobanteScreen(montoRetirado: String, saldoRestante: Double) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF388E3C),
                        Color(0xFF2E7D32),
                        Color(0xFF1B5E20)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1E1E1E).copy(alpha = 0.95f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = "Exitoso",
                        tint = Color(0xFF00C853),
                        modifier = Modifier
                            .size(80.dp)
                            .padding(bottom = 24.dp)
                    )

                    Text(
                        text = "¡Retiro Exitoso!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00C853),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        text = "Tu retiro se procesó correctamente",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    HorizontalDivider(
                        color = Color(0xFF00C853).copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = "Monto retirado",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "$ $montoRetirado",
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    HorizontalDivider(
                        color = Color(0xFF00C853).copy(alpha = 0.3f),
                        thickness = 1.dp,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    Text(
                        text = "Saldo restante",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "$ ${String.format("%.2f", saldoRestante)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00C853)
                    )
                }
            }
        }
    }
}