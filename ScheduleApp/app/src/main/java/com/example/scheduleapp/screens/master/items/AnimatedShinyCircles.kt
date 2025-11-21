package com.example.scheduleapp.screens.master.items

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.*
import kotlin.random.Random
import kotlinx.coroutines.delay

@Composable
fun AnimatedShinyTop(
    shiny: Color,
    initialX: Float,
    initialY: Float
) {
    var targetX by remember { mutableStateOf(initialX) }
    var targetY by remember { mutableStateOf(initialY) }

    val animatedX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = tween(durationMillis = 3000, easing = androidx.compose.animation.core.LinearEasing),
        label = "shiny_top_x_animation"
    )
    val animatedY by animateFloatAsState(
        targetValue = targetY,
        animationSpec = tween(durationMillis = 3000, easing = androidx.compose.animation.core.LinearEasing),
        label = "shiny_top_y_animation"
    )

    // Функция для генерации следующей позиции для верхнего круга
    fun getNextTopPosition(currentX: Float, currentY: Float, minDistance: Int = 50, maxDistance: Int = 150): Pair<Float, Float> {
        val distance = Random.nextInt(minDistance, maxDistance + 1).toFloat()
        val angle = Random.nextDouble(0.0, 2 * PI).toFloat()

        val newX = currentX + cos(angle) * distance
        val newY = currentY + sin(angle) * distance

        // X: по всей ширине экрана (-400 до 400), Y: только верхняя половина (-400 до 0)
        return Pair(
            newX.coerceIn(-200f, 500f),   // Вся ширина экрана
            newY.coerceIn(-400f, 0f)      // Только верхняя половина (отрицательные значения)
        )
    }

    LaunchedEffect(Unit) {
        delay(1000)

        while (true) {
            val (newX, newY) = getNextTopPosition(targetX, targetY)
            targetX = newX
            targetY = newY
            delay(2800)
        }
    }

    ShinyCircle(shiny = shiny, x = animatedX, y = animatedY)
}

@Composable
fun AnimatedShinyBottom(
    shiny: Color,
    initialX: Float,
    initialY: Float
) {
    var targetX by remember { mutableStateOf(initialX) }
    var targetY by remember { mutableStateOf(initialY) }

    val animatedX by animateFloatAsState(
        targetValue = targetX,
        animationSpec = tween(durationMillis = 3500, easing = androidx.compose.animation.core.LinearEasing),
        label = "shiny_bottom_x_animation"
    )
    val animatedY by animateFloatAsState(
        targetValue = targetY,
        animationSpec = tween(durationMillis = 3500, easing = androidx.compose.animation.core.LinearEasing),
        label = "shiny_bottom_y_animation"
    )

    // Функция для генерации следующей позиции для нижнего круга
    fun getNextBottomPosition(currentX: Float, currentY: Float, minDistance: Int = 50, maxDistance: Int = 150): Pair<Float, Float> {
        val distance = Random.nextInt(minDistance, maxDistance + 1).toFloat()
        val angle = Random.nextDouble(0.0, 2 * PI).toFloat()

        val newX = currentX + cos(angle) * distance
        val newY = currentY + sin(angle) * distance

        // X: по всей ширине экрана (-400 до 400), Y: только нижняя половина (0 до 600)
        return Pair(
            newX.coerceIn(-300f, 200f),   // Вся ширина экрана
            newY.coerceIn(0f, 400f)       // Только нижняя половина (положительные значения)
        )
    }

    LaunchedEffect(Unit) {
        delay(1500)

        while (true) {
            val (newX, newY) = getNextBottomPosition(targetX, targetY)
            targetX = newX
            targetY = newY
            delay(3300)
        }
    }

    ShinyCircle(shiny = shiny, x = animatedX, y = animatedY)
}

@Composable
fun ShinyCircle(
    shiny: Color,
    x: Float,
    y: Float,
    size: Float = 320f
) {
    Canvas(
        modifier = Modifier
            .size(size.dp)
            .offset(x = x.dp, y = y.dp)
    ) {
        val radius = size.dp.toPx()
        val colorList = createColorGradient(shiny)
        val brush = Brush.radialGradient(
            colors = colorList,
            center = center,
            radius = radius,
        )
        drawCircle(brush = brush, radius = radius)
    }
}

private fun createColorGradient(shiny: Color): List<Color> {
    return listOf(
        shiny,
        shiny.copy(alpha = 0.9f),
        shiny.copy(alpha = 0.8f),
        shiny.copy(alpha = 0.6f),
        shiny.copy(alpha = 0.4f),
        shiny.copy(alpha = 0.3f),
        shiny.copy(alpha = 0.15f),
        shiny.copy(alpha = 0.05f),
        Color.Transparent
    )
}