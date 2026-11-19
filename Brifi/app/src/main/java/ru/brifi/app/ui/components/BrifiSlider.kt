package ru.brifi.app.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

private fun triggerHaptic(context: Context, effectId: Int) {
    runCatching {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator?.vibrate(VibrationEffect.createPredefined(effectId))
    }
}

@Composable
fun BrifiActionSlider(
    thumbLabel: String,
    rightActionLabel: String,
    onSlideComplete: () -> Unit,
    onRightActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    height: Dp = 38.dp,
    thumbWidth: Dp = 112.dp
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var trackWidthPx by remember { mutableFloatStateOf(1f) }
    val thumbWidthPx = with(density) { thumbWidth.toPx() }
    val maxOffsetPx = (trackWidthPx - thumbWidthPx - with(density) { 6.dp.toPx() }).coerceAtLeast(1f)

    val dragOffset = remember { Animatable(0f) }
    var hit35 by remember { mutableStateOf(false) }
    var hit70 by remember { mutableStateOf(false) }

    val progress = (dragOffset.value / maxOffsetPx).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(50))
            .background(BrifiColors.SliderTrack)
            .onSizeChanged { trackWidthPx = it.width.toFloat().coerceAtLeast(1f) }
            .padding(3.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        // Chevrons + right status/action label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = thumbWidth + 4.dp, end = 12.dp)
                .graphicsLayer { alpha = (1f - progress * 1.4f).coerceIn(0f, 1f) },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SliderChevronsIcon()
            Text(
                text = rightActionLabel,
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = BrifiColors.SliderTextDark,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = if (onRightActionClick != null) {
                    Modifier.padding(start = 6.dp)
                } else {
                    Modifier.padding(start = 6.dp)
                }
            )
        }

        // Draggable purple capsule thumb
        Box(
            modifier = Modifier
                .width(thumbWidth)
                .fillMaxHeight()
                .graphicsLayer {
                    translationX = dragOffset.value
                }
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(50))
                .clip(RoundedCornerShape(50))
                .background(BrifiColors.SliderThumb)
                .border(1.dp, BrifiColors.SliderThumbBorder, RoundedCornerShape(50))
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        val next = (dragOffset.value + delta).coerceIn(0f, maxOffsetPx)
                        val p = next / maxOffsetPx
                        if (!hit35 && p >= 0.35f) {
                            hit35 = true
                            triggerHaptic(context, VibrationEffect.EFFECT_TICK)
                        }
                        if (!hit70 && p >= 0.70f) {
                            hit70 = true
                            triggerHaptic(context, VibrationEffect.EFFECT_TICK)
                        }
                        scope.launch { dragOffset.snapTo(next) }
                    },
                    onDragStarted = {
                        hit35 = false
                        hit70 = false
                        triggerHaptic(context, VibrationEffect.EFFECT_TICK)
                    },
                    onDragStopped = {
                        val currentProgress = dragOffset.value / maxOffsetPx
                        if (currentProgress >= 0.70f) {
                            triggerHaptic(context, VibrationEffect.EFFECT_DOUBLE_CLICK)
                            scope.launch {
                                dragOffset.animateTo(
                                    maxOffsetPx,
                                    spring(stiffness = Spring.StiffnessMediumLow)
                                )
                                onSlideComplete()
                                dragOffset.snapTo(0f)
                            }
                        } else {
                            scope.launch {
                                dragOffset.animateTo(
                                    0f,
                                    spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessMedium
                                    )
                                )
                            }
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = thumbLabel,
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = Color.White,
                maxLines = 1
            )
        }
    }
}
