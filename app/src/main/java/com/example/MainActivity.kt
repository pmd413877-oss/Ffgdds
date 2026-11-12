package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.AppScreen
import com.example.ui.screens.AutoMachineScreen
import com.example.ui.screens.PrDashboardScreen
import com.example.ui.screens.ProofHistoryScreen
import com.example.ui.screens.ServerSettingsScreen
import com.example.ui.screens.TaskListScreen
import com.example.ui.theme.InstaPink
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrCardDark
import com.example.ui.theme.PrNavyDark
import com.example.ui.theme.PrPrimary
import com.example.ui.theme.PrSurfaceDark
import com.example.ui.theme.PrTextPrimary
import com.example.ui.theme.PrTextSecondary
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val submissions by viewModel.submissions.collectAsState()
    val serverConfig by viewModel.serverConfig.collectAsState()

    // Handle Back Press
    BackHandler(enabled = uiState.currentScreen != AppScreen.PR_DASHBOARD) {
        viewModel.setScreen(AppScreen.PR_DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = PrSurfaceDark,
                contentColor = PrTextPrimary,
                tonalElevation = 8.dp
            ) {
                // PR. Social Dashboard Browser Tab
                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.PR_DASHBOARD,
                    onClick = { viewModel.setScreen(AppScreen.PR_DASHBOARD) },
                    icon = {
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = "PR. Social ড্যাশবোর্ড"
                        )
                    },
                    label = { Text("ড্যাশবোর্ড") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = InstaPink,
                        indicatorColor = InstaPink,
                        unselectedIconColor = PrTextSecondary,
                        unselectedTextColor = PrTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_pr_dashboard")
                )

                // Auto Machine Tab
                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.AUTO_MACHINE,
                    onClick = { viewModel.setScreen(AppScreen.AUTO_MACHINE) },
                    icon = {
                        Icon(
                            Icons.Default.PlayCircle,
                            contentDescription = "অটো মেশিন"
                        )
                    },
                    label = { Text("অটো মেশিন") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = InstaPink,
                        indicatorColor = InstaPink,
                        unselectedIconColor = PrTextSecondary,
                        unselectedTextColor = PrTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_auto_machine")
                )

                // Tasks Tab
                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.TASK_LIST,
                    onClick = { viewModel.setScreen(AppScreen.TASK_LIST) },
                    icon = {
                        BadgedBox(
                            badge = {
                                val pendingCount = tasks.count { it.status == com.example.data.model.TaskStatus.PENDING }
                                if (pendingCount > 0) {
                                    Badge(containerColor = InstaPink) {
                                        Text("$pendingCount")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.List, contentDescription = "টাস্ক তালিকা")
                        }
                    },
                    label = { Text("টাস্ক") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = PrPrimary,
                        indicatorColor = PrPrimary,
                        unselectedIconColor = PrTextSecondary,
                        unselectedTextColor = PrTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_tasks")
                )

                // Proof Submissions History Tab
                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.HISTORY,
                    onClick = { viewModel.setScreen(AppScreen.HISTORY) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (submissions.isNotEmpty()) {
                                    Badge(containerColor = com.example.ui.theme.PrSuccess) {
                                        Text("${submissions.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = "প্রুফ লগ")
                        }
                    },
                    label = { Text("প্রুফ লগ") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = com.example.ui.theme.PrSuccess,
                        indicatorColor = com.example.ui.theme.PrSuccess,
                        unselectedIconColor = PrTextSecondary,
                        unselectedTextColor = PrTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_history")
                )

                // Server Settings Tab
                NavigationBarItem(
                    selected = uiState.currentScreen == AppScreen.SETTINGS,
                    onClick = { viewModel.setScreen(AppScreen.SETTINGS) },
                    icon = {
                        Icon(Icons.Default.Settings, contentDescription = "সার্ভার সেটিংস")
                    },
                    label = { Text("সার্ভার") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = InstaPink,
                        indicatorColor = InstaPink,
                        unselectedIconColor = PrTextSecondary,
                        unselectedTextColor = PrTextSecondary
                    ),
                    modifier = Modifier.testTag("nav_settings")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PrNavyDark)
        ) {
            when (uiState.currentScreen) {
                AppScreen.PR_DASHBOARD -> {
                    PrDashboardScreen(
                        onNavigateToAutoMachine = {
                            viewModel.setScreen(AppScreen.AUTO_MACHINE)
                        }
                    )
                }

                AppScreen.AUTO_MACHINE -> {
                    AutoMachineScreen(
                        uiState = uiState,
                        serverConfig = serverConfig,
                        onFollowDetected = { username, bitmapProvider ->
                            viewModel.onFollowActionDetected(username, bitmapProvider)
                        },
                        onManualCapture = { bitmapProvider ->
                            viewModel.triggerManualCapture(bitmapProvider)
                        },
                        onGalleryPicked = { uri ->
                            viewModel.handleGalleryScreenshotPicked(uri)
                        },
                        onNextTask = {
                            viewModel.advanceToNextTask()
                        },
                        onSubmitProof = {
                            viewModel.submitCurrentProof()
                        },
                        onOpenSettings = {
                            viewModel.setScreen(AppScreen.SETTINGS)
                        },
                        onDismissBanner = {
                            viewModel.clearBanner()
                        }
                    )
                }

                AppScreen.TASK_LIST -> {
                    TaskListScreen(
                        tasks = tasks,
                        activeTaskId = uiState.activeTask?.id,
                        onSelectTask = { task ->
                            viewModel.selectTask(task)
                        },
                        onAddTask = { username, points ->
                            viewModel.addNewTask(username, points)
                        }
                    )
                }

                AppScreen.HISTORY -> {
                    ProofHistoryScreen(
                        submissions = submissions,
                        onRetrySubmission = { submission ->
                            viewModel.submitCurrentProof()
                        }
                    )
                }

                AppScreen.SETTINGS -> {
                    ServerSettingsScreen(
                        currentConfig = serverConfig,
                        serverPingStatus = uiState.serverPingStatus,
                        onSaveConfig = { newConfig ->
                            viewModel.updateServerConfig(newConfig)
                        },
                        onTestConnection = {
                            viewModel.testServerConnection()
                        }
                    )
                }
            }
        }
    }
}
