package com.moneymatters.core.designsystem

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * Lightweight 3D Canvas Primitives rendered in native Jetpack Compose Canvas (60-120fps performance).
 */

@Composable
fun Canvas3DCoin(
    modifier: Modifier = Modifier.size(70.dp),
    coinColor: Color = Color(0xFFFFD700),
    secondaryColor: Color = Color(0xFFFFA500)
) {
    val infiniteTransition = rememberInfiniteTransition("coin_rot")
    val rotY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(2500, easing = LinearEasing)),
        label = "y_rot"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = min(size.width, size.height) * 0.4f
        val scaleX = cos(Math.toRadians(rotY.toDouble())).toFloat()

        // 3D Rim / Edge Thickness
        val rimOffset = 4.dp.toPx()
        val isFront = scaleX >= 0

        // Draw 3D Coin Edge
        drawOval(
            brush = Brush.verticalGradient(listOf(secondaryColor, coinColor.copy(alpha = 0.5f))),
            topLeft = Offset(cx - radius * abs(scaleX) - rimOffset, cy - radius),
            size = Size(radius * 2f * abs(scaleX) + rimOffset * 2f, radius * 2f)
        )

        // Draw Front/Back Face
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(coinColor, secondaryColor),
                center = Offset(cx - radius * scaleX * 0.3f, cy - radius * 0.3f),
                radius = radius * 1.2f
            ),
            topLeft = Offset(cx - radius * abs(scaleX), cy - radius),
            size = Size(radius * 2f * abs(scaleX), radius * 2f)
        )

        // Inner Engraving Circle
        drawOval(
            color = Color.White.copy(alpha = 0.5f),
            topLeft = Offset(cx - radius * 0.7f * abs(scaleX), cy - radius * 0.7f),
            size = Size(radius * 1.4f * abs(scaleX), radius * 1.4f),
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun Canvas3DBalanceScale(
    modifier: Modifier = Modifier.fillMaxWidth().height(100.dp),
    tiltAngle: Float = 0f, // -15f to +15f
    accentColor: Color = Color(0xFF3B82F6)
) {
    val animTilt by animateFloatAsState(
        targetValue = tiltAngle,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "tilt"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height * 0.7f
        val poleHeight = size.height * 0.6f

        // Base Stand
        drawRoundRect(
            color = Color(0xFF1E293B),
            topLeft = Offset(cx - 30.dp.toPx(), cy),
            size = Size(60.dp.toPx(), 8.dp.toPx()),
            cornerRadius = CornerRadius(4.dp.toPx())
        )

        // Vertical Pillar
        drawLine(
            color = Color(0xFF475569),
            start = Offset(cx, cy),
            end = Offset(cx, cy - poleHeight),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Rotating Fulcrum Beam
        val beamLength = size.width * 0.6f
        val rad = Math.toRadians(animTilt.toDouble())
        val dx = (beamLength / 2f) * cos(rad).toFloat()
        val dy = (beamLength / 2f) * sin(rad).toFloat()

        val leftEnd = Offset(cx - dx, cy - poleHeight - dy)
        val rightEnd = Offset(cx + dx, cy - poleHeight + dy)

        drawLine(
            brush = Brush.horizontalGradient(listOf(accentColor, Color.White, accentColor)),
            start = leftEnd,
            end = rightEnd,
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Left Pan Strings & Metallic Bowl
        drawLine(Color.Gray, leftEnd, Offset(leftEnd.x, leftEnd.y + 25.dp.toPx()), strokeWidth = 1.5.dp.toPx())
        drawArc(
            color = accentColor.copy(alpha = 0.8f),
            startAngle = 0f, sweepAngle = 180f, useCenter = true,
            topLeft = Offset(leftEnd.x - 20.dp.toPx(), leftEnd.y + 15.dp.toPx()),
            size = Size(40.dp.toPx(), 20.dp.toPx())
        )

        // Right Pan Strings & Metallic Bowl
        drawLine(Color.Gray, rightEnd, Offset(rightEnd.x, rightEnd.y + 25.dp.toPx()), strokeWidth = 1.5.dp.toPx())
        drawArc(
            color = accentColor.copy(alpha = 0.8f),
            startAngle = 0f, sweepAngle = 180f, useCenter = true,
            topLeft = Offset(rightEnd.x - 20.dp.toPx(), rightEnd.y + 15.dp.toPx()),
            size = Size(40.dp.toPx(), 20.dp.toPx())
        )
    }
}

@Composable
fun Canvas3DDonutChart(
    modifier: Modifier = Modifier.size(140.dp),
    needsPercent: Float = 0.5f,
    wantsPercent: Float = 0.3f,
    savingsPercent: Float = 0.2f
) {
    val infiniteTransition = rememberInfiniteTransition("donut_rot")
    val rotAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(20000, easing = LinearEasing)),
        label = "rot"
    )

    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val outerRadius = min(size.width, size.height) * 0.45f
        val strokeW = 24.dp.toPx()

        rotate(rotAngle, pivot = Offset(cx, cy)) {
            // Needs Slice (Blue/Emerald)
            drawArc(
                color = Color(0xFF3B82F6),
                startAngle = 0f, sweepAngle = needsPercent * 360f,
                useCenter = false,
                topLeft = Offset(cx - outerRadius, cy - outerRadius),
                size = Size(outerRadius * 2f, outerRadius * 2f),
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // Wants Slice (Red/Pink)
            drawArc(
                color = Color(0xFFEF4444),
                startAngle = needsPercent * 360f, sweepAngle = wantsPercent * 360f,
                useCenter = false,
                topLeft = Offset(cx - outerRadius, cy - outerRadius),
                size = Size(outerRadius * 2f, outerRadius * 2f),
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )

            // Savings Slice (Purple/Gold)
            drawArc(
                color = Color(0xFF10B981),
                startAngle = (needsPercent + wantsPercent) * 360f, sweepAngle = savingsPercent * 360f,
                useCenter = false,
                topLeft = Offset(cx - outerRadius, cy - outerRadius),
                size = Size(outerRadius * 2f, outerRadius * 2f),
                style = Stroke(width = strokeW, cap = StrokeCap.Round)
            )
        }
    }
}
