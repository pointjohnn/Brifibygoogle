package ru.brifi.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.brifi.app.model.OrderTask
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily
import kotlin.math.abs

/**
 * 3-слойная стопка карточек «Другие задачи» на одном Animatable в graphicsLayer (120fps).
 * Не использует VerticalPager — ноль конфликтов с внешним verticalScroll.
 * Перелистывание: свайп влево/вправо, тап по выглядывающей верхней/нижней карточке,
 * либо боковая полоса ▲ / ▼ и точки-индикаторы справа. Автопереключение каждые 10 сек.
 */
@Composable
fun OtherTasksDeck(
    tasks: List<OrderTask>,
    onTakeInWork: (OrderTask) -> Unit,
    onDetailsClick: (OrderTask) -> Unit,
    onStatusClick: (OrderTask) -> Unit,
    modifier: Modifier = Modifier
) {
    if (tasks.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(BrifiColors.InProgressCardBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Все активные задачи уже в работе 🎉",
                fontFamily = GoogleSansFamily,
                fontSize = 14.sp,
                color = BrifiColors.TextSecondary
            )
        }
        return
    }

    var currentIndex by remember(tasks.size) { mutableIntStateOf(0) }
    if (currentIndex >= tasks.size) currentIndex = 0

    // Single Animatable driving the entire 3-layer stack:
    // -1f -> moving to previous, 0f -> resting, +1f -> moving to next
    val stackProgress = remember { Animatable(0f) }
    var isUserInteracting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun stepBy(delta: Int) {
        if (tasks.size <= 1) return
        scope.launch {
            val target = if (delta > 0) 1f else -1f
            stackProgress.animateTo(
                targetValue = target,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
            currentIndex = (currentIndex + delta + tasks.size) % tasks.size
            stackProgress.snapTo(0f)
        }
    }

    // Auto-advance every 10 seconds when not interacting
    LaunchedEffect(tasks.size, currentIndex, isUserInteracting) {
        if (tasks.size <= 1 || isUserInteracting) return@LaunchedEffect
        delay(10_000L)
        stepBy(1)
    }

    val prevIndex = (currentIndex - 1 + tasks.size) % tasks.size
    val nextIndex = (currentIndex + 1) % tasks.size
    val p = stackProgress.value // -1f .. +1f

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 3-layer card stack
        Box(
            modifier = Modifier
                .weight(1f)
                .height(308.dp)
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { dragDelta ->
                        if (tasks.size <= 1) return@rememberDraggableState
                        val nextVal = (stackProgress.value - dragDelta / 260f).coerceIn(-1f, 1f)
                        scope.launch { stackProgress.snapTo(nextVal) }
                    },
                    onDragStarted = { isUserInteracting = true },
                    onDragStopped = {
                        isUserInteracting = false
                        val cur = stackProgress.value
                        when {
                            cur > 0.25f -> stepBy(1)
                            cur < -0.25f -> stepBy(-1)
                            else -> scope.launch {
                                stackProgress.animateTo(
                                    0f,
                                    spring(stiffness = Spring.StiffnessMedium)
                                )
                            }
                        }
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (tasks.size > 1) {
                // Previous card peeking from top
                val prevT = (-p).coerceIn(0f, 1f) // 0 at rest, 1 when becoming center
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (p < -0.5f) 3f else 1f)
                        .graphicsLayer {
                            val scale = 0.895f + 0.105f * prevT
                            scaleX = scale
                            scaleY = scale
                            alpha = 0.48f + 0.52f * prevT
                            translationY = -112f * (1f - prevT)
                        }
                        .clickable { stepBy(-1) }
                ) {
                    OtherTaskCard(
                        task = tasks[prevIndex],
                        onTakeInWorkSlide = {},
                        onDetailsClick = { stepBy(-1) },
                        onStatusClick = { stepBy(-1) }
                    )
                }

                // Next card peeking from bottom
                val nextT = p.coerceIn(0f, 1f) // 0 at rest, 1 when becoming center
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(if (p > 0.5f) 3f else 1f)
                        .graphicsLayer {
                            val scale = 0.895f + 0.105f * nextT
                            scaleX = scale
                            scaleY = scale
                            alpha = 0.48f + 0.52f * nextT
                            translationY = 112f * (1f - nextT)
                        }
                        .clickable { stepBy(1) }
                ) {
                    OtherTaskCard(
                        task = tasks[nextIndex],
                        onTakeInWorkSlide = {},
                        onDetailsClick = { stepBy(1) },
                        onStatusClick = { stepBy(1) }
                    )
                }
            }

            // Active center card
            val absP = abs(p).coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(if (absP > 0.5f) 2f else 4f)
                    .graphicsLayer {
                        val scale = 1f - 0.105f * absP
                        scaleX = scale
                        scaleY = scale
                        alpha = 1f - 0.52f * absP
                        translationY = -112f * p
                    }
            ) {
                val activeTask = tasks[currentIndex]
                OtherTaskCard(
                    task = activeTask,
                    onTakeInWorkSlide = { onTakeInWork(activeTask) },
                    onDetailsClick = { onDetailsClick(activeTask) },
                    onStatusClick = { onStatusClick(activeTask) }
                )
            }
        }

        // Functional side rail: ▲ / clickable dots / ▼
        if (tasks.size > 1) {
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier.width(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "▲",
                    fontSize = 11.sp,
                    color = BrifiColors.TextSecondary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { stepBy(-1) }
                        .padding(4.dp)
                )

                val visibleDots = tasks.size.coerceAtMost(6)
                val activeDot = currentIndex % visibleDots
                for (i in 0 until visibleDots) {
                    val selected = i == activeDot
                    Box(
                        modifier = Modifier
                            .size(width = 6.dp, height = if (selected) 18.dp else 6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (selected) BrifiColors.SliderThumb else Color.White.copy(alpha = 0.28f)
                            )
                            .clickable {
                                currentIndex = i
                            }
                    )
                }

                Text(
                    text = "▼",
                    fontSize = 11.sp,
                    color = BrifiColors.TextSecondary,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { stepBy(1) }
                        .padding(4.dp)
                )
            }
        }
    }
}
