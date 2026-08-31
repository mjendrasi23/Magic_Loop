package com.example.magicloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.magicloop.navigation.MagicLoopNavHost
import com.example.magicloop.ui.theme.MagicLoopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = (application as MagicLoopApplication).repository

        setContent {
            MagicLoopTheme {
                MagicLoopNavHost(
                    repository = repository,
                    streakRepository = (application as MagicLoopApplication).streakRepository,
                    badgeRepository = (application as MagicLoopApplication).badgeRepository,
                    badgeChecker = (application as MagicLoopApplication).badgeChecker

                    )
            }
        }
    }
}