package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GradientBlue
import com.example.ui.theme.GradientGreen
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onTimeout: () -> Unit
) {
    // Navigate away after 2.5 seconds
    LaunchedEffect(Unit) {
        delay(2500)
        onTimeout()
    }

    // Logo Spin and Pulse Animations
    val transition = rememberInfiniteTransition(label = "SplashAnimation")
    
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LogoRotation"
    )

    val scale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LogoScale"
    )

    val waveOffset by transition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveAnimation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                )
            )
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Creative Hybrid Framework Canvas Art (RL & GA concept)
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .testTag("splash_logo"),
                contentAlignment = Alignment.Center
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                val secondaryColor = MaterialTheme.colorScheme.secondary
                val tertiaryColor = MaterialTheme.colorScheme.tertiary
                
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val baseRadius = 60.dp.toPx() * scale

                    // 1. Draw GA Double-Helix representation or chromosome loops
                    val gaPointsCount = 8
                    val helixSpacing = 30.dp.toPx()
                    for (i in 0 until gaPointsCount) {
                        val fraction = i.toFloat() / gaPointsCount
                        val angle = fraction * 2 * Math.PI.toFloat() + Math.toRadians(rotation.toDouble()).toFloat()
                        val offsetRadius = baseRadius + sin(angle * 2 + waveOffset) * 15.dp.toPx()
                        
                        val px1 = center.x + offsetRadius * cos(angle)
                        val py1 = center.y + offsetRadius * sin(angle)

                        val px2 = center.x + (offsetRadius - helixSpacing) * cos(angle)
                        val py2 = center.y + (offsetRadius - helixSpacing) * sin(angle)

                        // Connecting chromosome strands
                        drawLine(
                            color = secondaryColor.copy(alpha = 0.6f),
                            start = Offset(px1, py1),
                            end = Offset(px2, py2),
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )

                        // Outer nodes (genes)
                        drawCircle(
                            color = secondaryColor,
                            radius = 6.dp.toPx(),
                            center = Offset(px1, py1)
                        )

                        // Inner nodes
                        drawCircle(
                            color = tertiaryColor,
                            radius = 4.dp.toPx(),
                            center = Offset(px2, py2)
                        )
                    }

                    // 2. Draw RL Agent exploration / network nodes in center
                    drawCircle(
                        color = primaryColor.copy(alpha = 0.15f),
                        radius = baseRadius - 15.dp.toPx(),
                        center = center
                    )

                    drawCircle(
                        color = primaryColor,
                        radius = 12.dp.toPx(),
                        center = center
                    )

                    // Connecting lines from active central RL agent to genetic chromosome nodes
                    for (i in 0..3) {
                        val angle = (i * Math.PI / 2).toFloat() + Math.toRadians(rotation.toDouble() * 0.5).toFloat()
                        val targetX = center.x + baseRadius * cos(angle)
                        val targetY = center.y + baseRadius * sin(angle)

                        drawLine(
                            color = primaryColor.copy(alpha = 0.7f),
                            start = center,
                            end = Offset(targetX, targetY),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // App Name with high-polish typography
            Text(
                text = "HRGAF",
                fontSize = 42.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = "GUI TESTING FRAMEWORK",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.secondary,
                letterSpacing = 6.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Research Context Subtitle
            Text(
                text = "Hybrid Reinforcement Learning &\nGenetic Algorithm Framework",
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                lineHeight = 20.sp,
                fontFamily = FontFamily.SansSerif
            )
        }
    }
}
