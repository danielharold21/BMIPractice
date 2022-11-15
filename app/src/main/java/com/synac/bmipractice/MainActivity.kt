package com.synac.bmipractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.synac.bmipractice.ui.theme.BMIPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BMIPracticeTheme {
                val viewModel = viewModel<BMIViewModel>()
                BMIScreen(viewModel = viewModel)
            }
        }
    }
}
