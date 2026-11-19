package ru.brifi.app.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ru.brifi.app.ui.theme.BrifiColors

@Composable
fun BreathingAura(
    modifier: Modifier = Modifier,
    height: Dp = 260.dp,
    inverted: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "brifi_aura")

    val leftPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_left"
    )
    val centerPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_center"
    )
    val rightPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 11800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_right"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .graphicsLayer {
                if (inverted) scaleY = -1f
            }
    ) {
        val w = size.width
        val h = size.height

        // Left sphere (#4638FF)
        val leftCenter = Offset(
            x = w * (0.14f + 0.06f * leftPhase),
            y = h * (0.12f + 0.08f * (1f - leftPhase))
        )
        val leftRadius = w * (0.62f + 0.08f * leftPhase)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrifiColors.AuraLeft.copy(alpha = 0.68f + 0.12f * leftPhase),
                    BrifiColors.AuraLeft.copy(alpha = 0.25f),
                    Color.Transparent
                ),
                center = leftCenter,
                radius = leftRadius
            ),
            radius = leftRadius,
            center = leftCenter
        )

        // Center sphere (#6C61FF)
        val midCenter = Offset(
            x = w * (0.50f + 0.04f * (centerPhase - 0.5f)),
            y = h * (0.04f + 0.10f * centerPhase)
        )
        val midRadius = w * (0.66f + 0.09f * (1f - centerPhase))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrifiColors.AuraCenter.copy(alpha = 0.62f + 0.14f * centerPhase),
                    BrifiColors.AuraCenter.copy(alpha = 0.22f),
                    Color.Transparent
                ),
                center = midCenter,
                radius = midRadius
            ),
            radius = midRadius,
            center = midCenter
        )

        // Right sphere (#61CDFF)
        val rightCenter = Offset(
            x = w * (0.86f - 0.06f * rightPhase),
            y = h * (0.14f + 0.07f * rightPhase)
        )
        val rightRadius = w * (0.58f + 0.08f * (1f - rightPhase))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    BrifiColors.AuraRight.copy(alpha = 0.58f + 0.14f * rightPhase),
                    BrifiColors.AuraRight.copy(alpha = 0.20f),
                    Color.Transparent
                ),
                center = rightCenter,
                radius = rightRadius
            ),
            radius = rightRadius,
            center = rightCenter
        )
    }
}
