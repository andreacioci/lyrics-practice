package com.andreacioci.weatherwidget.ui

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.andreacioci.weatherwidget.AppContainer
import com.andreacioci.weatherwidget.ui.detail.WeatherDetailScreen
import com.andreacioci.weatherwidget.ui.settings.SettingsScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var hasLocationPermission by remember {
                mutableStateOf(AppContainer.from(this).locationProvider.hasLocationPermission())
            }
            var showSettings by remember { mutableStateOf(false) }

            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions(),
            ) { grants ->
                hasLocationPermission = grants.values.any { it }
            }

            MaterialTheme {
                Surface {
                    if (showSettings) {
                        SettingsScreen(onBack = { showSettings = false })
                    } else {
                        WeatherDetailScreen(
                            hasLocationPermission = hasLocationPermission,
                            onRequestPermission = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                    ),
                                )
                            },
                            onOpenSettings = { showSettings = true },
                        )
                    }
                }
            }
        }
    }
}
