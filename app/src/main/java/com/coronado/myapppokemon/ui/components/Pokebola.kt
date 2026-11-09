package com.coronado.myapppokemon.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Pokebola(tamano: Dp = 96.dp, modifier: Modifier = Modifier) {
    Canvas(modifier.size(tamano)) {
        val r = size.minDimension / 2f
        val c = center
        val negro = Color(0xFF222222)
        drawArc(Color(0xFFEE1515), startAngle = 180f, sweepAngle = 180f, useCenter = true)
        drawArc(Color.White, startAngle = 0f, sweepAngle = 180f, useCenter = true)
        drawRect(negro, topLeft = Offset(0f, c.y - r * 0.08f), size = Size(size.width, r * 0.16f))
        drawCircle(negro, radius = r * 0.30f, center = c)
        drawCircle(Color.White, radius = r * 0.17f, center = c)
        drawCircle(negro, radius = r * 0.96f, center = c, style = Stroke(width = r * 0.08f))
    }
}
