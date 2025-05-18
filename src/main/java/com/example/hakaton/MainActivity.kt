package com.example.hakaton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hakaton.ui.screens.HomeScreen
import com.example.hakaton.ui.theme.HakatonTheme
import com.example.hakaton.ui.theme.screens.CardsListScreen
import com.example.hakaton.ui.theme.screens.RegistrationScreen
import com.example.hakaton.ui.theme.screens.SplashScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HakatonTheme {
                val navController: NavHostController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "splash"
                ) {
                    composable("splash") {
                        SplashScreen(navController)
                    }
                    composable("register") {
                        RegistrationScreen(navController)
                    }
                    composable("home") {
                        HomeScreen(navController)
                    }
                    composable("cards/{folderId}") { backStack ->
                        val folderId = backStack.arguments
                            ?.getString("folderId") ?: ""
                        CardsListScreen(navController, folderId)
                    }
                }
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    HakatonTheme {
        Greeting("Android")
    }
}