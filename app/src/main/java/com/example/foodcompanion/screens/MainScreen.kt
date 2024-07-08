package com.example.foodcompanion.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun MainPage(
    onNextButtonClicked: () -> Unit = {},
) {
    Text(
        text = "This is the main page"
    )
}

@Preview
@Composable
fun Preview(){
    MainPage()
}