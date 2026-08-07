package com.vlt.zeroperte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.vlt.zeroperte.ui.theme.ZeroPerteTheme
import com.vlt.zeroperte.ui.FoodCreateUpdateScreen
import com.vlt.zeroperte.ui.FoodDetailScreen
import com.vlt.zeroperte.ui.FoodListScreen
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
                        startDestination = Screen.FoodDetail.route
                    ){
                        composable(Screen.FoodList.route){
                            FoodListScreen(modifier = Modifier.fillMaxSize(), navController = navController)
                        }
                        composable(Screen.FoodDetail.route,
                            arguments = listOf(navArgument("foodId")
                            { type = NavType.LongType })){ backStackEntry ->
                            val foodId = backStackEntry.arguments?.getLong("foodId") ?: return@composable
                            FoodDetailScreen()
                        }
                        composable(Screen.FoodCreateUpdate.route){backStackEntry ->
                            val foodId = backStackEntry.arguments?.getString("foodId")
                            FoodCreateUpdateScreen(navController = navController)
                        }
                        composable(Screen.Parameters.route){}

                    }

                }

            }

        }
    }
}