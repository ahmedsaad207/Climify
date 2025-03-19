package com.delighted2wins.climify.weatherdetails

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun DetailsUI() {
    Text(
        text = "Details",
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center
    )
}