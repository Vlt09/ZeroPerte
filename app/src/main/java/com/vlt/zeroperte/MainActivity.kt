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
import androidx.navigation.toRoute
import com.vlt.zeroperte.ui.FoodCreateUpdate
import com.vlt.zeroperte.ui.theme.ZeroPerteTheme
import com.vlt.zeroperte.ui.Composable.FoodCreateUpdateScreen
import com.vlt.zeroperte.ui.FoodDetail
import com.vlt.zeroperte.ui.Composable.FoodDetailScreen
import com.vlt.zeroperte.ui.FoodList
import com.vlt.zeroperte.ui.Composable.FoodListScreen
import com.vlt.zeroperte.ui.Home
import com.vlt.zeroperte.ui.Composable.HomeScreen
import com.vlt.zeroperte.ui.Parameters
import com.vlt.zeroperte.ui.Composable.ParametersScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            ZeroPerteTheme {

                val activity = this
                Surface(modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background)
                {
                    NavHost(
                        navController = navController,
                        startDestination = Home
                    ){
                        composable<Home> {
                            HomeScreen(navController = navController)
                        }

                        composable<FoodList>{
                            FoodListScreen(modifier = Modifier.fillMaxSize(), navController = navController)
                        }
                        composable<FoodDetail>{ backStackEntry ->
                            val args = backStackEntry.toRoute<FoodDetail>()
                            FoodDetailScreen(foodId = args.foodId, navController = navController)
                        }

                        composable<FoodCreateUpdate>{ backStackEntry ->
                            val args = backStackEntry.toRoute<FoodCreateUpdate>()

                            FoodCreateUpdateScreen(foodId = args.foodId, navController = navController,
                            activity = activity)
                        }
                        composable<Parameters>{backStackEntry ->
                            ParametersScreen(navController = navController)
                        }

                    }

                }

            }

        }
    }
}