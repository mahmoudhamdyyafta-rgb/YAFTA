package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.HelpGuideScreen
import com.example.ui.screens.SavedProjectsScreen
import com.example.ui.screens.StudioScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DesignViewModel

const val ROUTE_STUDIO = "studio"
const val ROUTE_PROJECTS = "projects"
const val ROUTE_HELP = "help"

class MainActivity : ComponentActivity() {

    private val designViewModel: DesignViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase cloud integration components
        com.example.data.api.FirebaseManager.initialize(applicationContext)

        // Support full screen bleed-through on contemporary Android OS versions
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = ROUTE_STUDIO
                    ) {
                        composable(ROUTE_STUDIO) {
                            StudioScreen(
                                viewModel = designViewModel,
                                onNavigateToProjects = { navController.navigate(ROUTE_PROJECTS) },
                                onNavigateToHelp = { navController.navigate(ROUTE_HELP) }
                            )
                        }

                        composable(ROUTE_PROJECTS) {
                            SavedProjectsScreen(
                                viewModel = designViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(ROUTE_HELP) {
                            HelpGuideScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
