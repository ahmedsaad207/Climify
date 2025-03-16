package com.delighted2wins.climify

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign

@Composable
fun FavoriteUI() {
    Text(
        text = "Favorite",
        modifier = Modifier.fillMaxSize(),
        textAlign = TextAlign.Center
    )
}