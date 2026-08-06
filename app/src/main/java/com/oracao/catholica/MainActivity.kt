package com.oracao.catholica

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.oracao.catholica.ui.NativePrayerAppScreen
import com.oracao.catholica.ui.theme.MyApplicationTheme
import com.oracao.catholica.viewmodel.PrayerViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PrayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep the screen awake while praying
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        val headerColor = android.graphics.Color.parseColor("#400000")
        val parchmentColor = android.graphics.Color.parseColor("#FAF4E8")

        enableEdgeToEdge(
            statusBarStyle = androidx.activity.SystemBarStyle.dark(headerColor),
            navigationBarStyle = androidx.activity.SystemBarStyle.light(parchmentColor, parchmentColor)
        )
        
        val decorView = window.decorView
        androidx.core.view.WindowCompat.getInsetsController(window, decorView)?.let { controller ->
            controller.show(
                androidx.core.view.WindowInsetsCompat.Type.statusBars() or
                        androidx.core.view.WindowInsetsCompat.Type.navigationBars()
            )
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = true
        }
        
        setContent {
            MyApplicationTheme {
                NativePrayerAppScreen(viewModel)
            }
        }
    }
}
