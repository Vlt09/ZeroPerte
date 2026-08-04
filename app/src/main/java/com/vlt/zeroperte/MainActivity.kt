package com.vlt.zeroperte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vlt.zeroperte.ui.theme.ZeroPerteTheme
import com.vlt.zeroperte.ui.FoodCreateUpdateScreen
import com.vlt.zeroperte.ui.FoodDetailScreen
import com.vlt.zeroperte.ui.foodListScreen
import com.vlt.zeroperte.ui.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ZeroPerteTheme {

                Surface(modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background)
                {
                    NavHost(
                        navController = navController,
                        startDestination = Screen.FoodCreateUpdate.route
                    ){
                        composable(Screen.FoodList.route){
                            foodListScreen(modifier = Modifier.fillMaxSize())
                        }
                        composable(Screen.FoodDetail.route){backStackEntry ->
                            val foodId = backStackEntry.arguments?.getString("foodId")
                            //FoodDetailScreen()
                        }
                        composable(Screen.FoodCreateUpdate.route){backStackEntry ->
                            val foodId = backStackEntry.arguments?.getString("foodId")
                            FoodCreateUpdateScreen()
                        }
                        composable(Screen.Parameters.route){}

                    }

                }

            }

        }
    }
}