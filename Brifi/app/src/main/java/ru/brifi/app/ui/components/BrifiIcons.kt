package ru.brifi.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

/**
 * Кнопка «Создать задачу»: толстый белый скруглённый прямоугольник + фиолетовый круг #6C61FF с белым + в правом нижнем углу.
 */
@Composable
fun CreateTaskIcon(modifier: Modifier = Modifier.size(34.dp)) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Thick white rounded rectangle
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(w * 0.08f, h * 0.14f),
            size = Size(w * 0.72f, h * 0.58f),
            cornerRadius = CornerRadius(w * 0.16f, w * 0.16f),
            style = Stroke(width = w * 0.11f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )

        // Purple circle badge at bottom right
        val circleRadius = w * 0.27f
        val circleCenter = Offset(w * 0.72f, h * 0.72f)
        drawCircle(
            color = BrifiColors.TileDark,
            radius = circleRadius + w * 0.04f,
            center = circleCenter
        )
        drawCircle(
            color = BrifiColors.SliderThumb,
            radius = circleRadius,
            center = circleCenter
        )

        // White plus inside purple circle
        val plusHalf = circleRadius * 0.52f
        val strokeW = w * 0.08f
        drawLine(
            color = Color.White,
            start = Offset(circleCenter.x - plusHalf, circleCenter.y),
            end = Offset(circleCenter.x + plusHalf, circleCenter.y),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
        drawLine(
            color = Color.White,
            start = Offset(circleCenter.x, circleCenter.y - plusHalf),
            end = Offset(circleCenter.x, circleCenter.y + plusHalf),
            strokeWidth = strokeW,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Кнопка «Сохранить макет»: жирные белые буквы URL + фиолетовая стрелка-галочка ^ (#6C61FF) в правом нижнем углу.
 */
@Composable
fun SaveUrlIcon(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(width = 56.dp, height = 34.dp)) {
        Text(
            text = "URL",
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 20.sp,
            letterSpacing = (-0.5).sp,
            color = Color.White,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Canvas(
            modifier = Modifier
                .size(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            val w = size.width
            val h = size.height
            val path = Path().apply {
                moveTo(w * 0.12f, h * 0.72f)
                lineTo(w * 0.50f, h * 0.24f)
                lineTo(w * 0.88f, h * 0.72f)
            }
            drawPath(
                path = path,
                color = BrifiColors.SliderThumb,
                style = Stroke(width = w * 0.22f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

/**
 * Иконка «Домой»: скруглённый домик с вырезом в форме буквы «Б».
 */
@Composable
fun NavHomeBrifiIcon(
    tint: Color,
    cutoutColor: Color,
    modifier: Modifier = Modifier.size(24.dp)
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val w = size.width
            val h = size.height
            val house = Path().apply {
                moveTo(w * 0.50f, h * 0.08f)
                lineTo(w * 0.90f, h * 0.42f)
                quadraticTo(w * 0.94f, h * 0.46f, w * 0.92f, h * 0.52f)
                lineTo(w * 0.92f, h * 0.82f)
                quadraticTo(w * 0.92f, h * 0.92f, w * 0.80f, h * 0.92f)
                lineTo(w * 0.20f, h * 0.92f)
                quadraticTo(w * 0.08f, h * 0.92f, w * 0.08f, h * 0.82f)
                lineTo(w * 0.08f, h * 0.52f)
                quadraticTo(w * 0.06f, h * 0.46f, w * 0.10f, h * 0.42f)
                close()
            }
            drawPath(path = house, color = tint)
        }
        Text(
            text = "Б",
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.sp,
            color = cutoutColor,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

/**
 * Иконка «Статистика»: 3 вертикальных скруглённых столбца с пропорциями высот 53 / 80 / 40.
 */
@Composable
fun NavStatsIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(24.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val barWidth = w * 0.22f
        val gap = w * 0.10f
        val totalW = barWidth * 3 + gap * 2
        val startX = (w - totalW) / 2f
        val bottomY = h * 0.90f
        val maxBarH = h * 0.80f

        val ratios = listOf(53f / 80f, 1f, 40f / 80f)
        ratios.forEachIndexed { idx, ratio ->
            val bh = maxBarH * ratio
            val x = startX + idx * (barWidth + gap)
            drawRoundRect(
                color = tint,
                topLeft = Offset(x, bottomY - bh),
                size = Size(barWidth, bh),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }
    }
}

/**
 * Иконка «Профиль»: силуэт пользователя.
 */
@Composable
fun NavProfileIcon(
    tint: Color,
    modifier: Modifier = Modifier.size(24.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Head
        drawCircle(
            color = tint,
            radius = w * 0.21f,
            center = Offset(w * 0.50f, h * 0.30f)
        )
        // Shoulders
        val body = Path().apply {
            moveTo(w * 0.16f, h * 0.88f)
            quadraticTo(w * 0.16f, h * 0.58f, w * 0.50f, h * 0.58f)
            quadraticTo(w * 0.84f, h * 0.58f, w * 0.84f, h * 0.88f)
            close()
        }
        drawPath(path = body, color = tint)
    }
}

/**
 * 3 векторных шеврона >>> для слайдера.
 */
@Composable
fun SliderChevronsIcon(
    tint: Color = BrifiColors.SliderThumb,
    modifier: Modifier = Modifier.size(width = 30.dp, height = 14.dp)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val step = w / 3.4f
        val strokeW = 2.dp.toPx()
        for (i in 0..2) {
            val ox = i * step + 2f
            val alpha = 0.45f + i * 0.27f
            val p = Path().apply {
                moveTo(ox, h * 0.18f)
                lineTo(ox + step * 0.55f, h * 0.50f)
                lineTo(ox, h * 0.82f)
            }
            drawPath(
                path = p,
                color = tint.copy(alpha = alpha.coerceAtMost(1f)),
                style = Stroke(width = strokeW, cap = StrokeCap.Round, join = StrokeJoin.Round)
            )
        }
    }
}

/**
 * Маленькая круглая иконка + на карточках задач.
 */
@Composable
fun CardPlusIcon(
    circleColor: Color,
    plusColor: Color,
    size: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.size(size)) {
        val r = this.size.minDimension / 2f
        val c = Offset(r, r)
        drawCircle(color = circleColor, radius = r, center = c)
        val half = r * 0.46f
        val sw = this.size.minDimension * 0.10f
        drawLine(plusColor, Offset(c.x - half, c.y), Offset(c.x + half, c.y), sw, StrokeCap.Round)
        drawLine(plusColor, Offset(c.x, c.y - half), Offset(c.x, c.y + half), sw, StrokeCap.Round)
    }
}
