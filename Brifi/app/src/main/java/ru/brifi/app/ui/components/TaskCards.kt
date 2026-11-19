package ru.brifi.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.brifi.app.data.SheetsParser
import ru.brifi.app.model.OrderTask
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

@Composable
fun UrgencyDeadlinePill(
    task: OrderTask,
    modifier: Modifier = Modifier
) {
    val (bg, fg) = when (task.urgency.trim()) {
        "!!!" -> BrifiColors.UrgencyHighBg to Color.White
        "!!" -> BrifiColors.UrgencyMediumBg to Color.Black
        else -> BrifiColors.UrgencyNormalBg to Color.White
    }
    val label = task.deadline?.let { SheetsParser.formatDate(it) }
        ?: task.deadlineRaw.ifBlank { "Без даты" }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            color = fg,
            maxLines = 1
        )
    }
}

/**
 * Карточка «В работе»: #2C2C2C, обводка 1dp #606060, скругление 24dp, объёмная тень.
 */
@Composable
fun InProgressTaskCard(
    task: OrderTask,
    onDoneSlide: () -> Unit,
    onStatusClick: () -> Unit,
    onDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 12.dp, shape = shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .background(BrifiColors.InProgressCardBg)
            .border(1.dp, BrifiColors.InProgressCardBorder, shape)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = task.title,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BrifiColors.TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${task.orderNumber}${if (task.urgency.isNotBlank()) "  •  ${task.urgency}" else ""}",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = BrifiColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            CardPlusIcon(
                circleColor = Color.White.copy(alpha = 0.14f),
                plusColor = Color.White,
                size = 26.dp,
                modifier = Modifier.clickable(onClick = onDetailsClick)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UrgencyDeadlinePill(task = task)
            Text(
                text = task.source,
                fontFamily = GoogleSansFamily,
                fontSize = 11.sp,
                color = BrifiColors.TextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Изменить статус",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = BrifiColors.DeadlineTodayAccent,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onStatusClick)
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        BrifiActionSlider(
            thumbLabel = "Готово",
            rightActionLabel = task.status,
            onSlideComplete = onDoneSlide,
            onRightActionClick = onStatusClick,
            thumbWidth = 96.dp,
            height = 36.dp
        )
    }
}

/**
 * Светлая карточка «Другие задачи»: #E7E7E7, скругление 32dp, поле заметок #FFFFFF (rx = 10dp).
 */
@Composable
fun OtherTaskCard(
    task: OrderTask,
    onTakeInWorkSlide: () -> Unit,
    onDetailsClick: () -> Unit,
    onStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(32.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 16.dp, shape = shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .background(BrifiColors.LightCardBg)
            .padding(horizontal = 18.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = task.title,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = BrifiColors.TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${task.orderNumber}${if (task.urgency.isNotBlank()) "   ${task.urgency}" else ""}",
                    fontFamily = GoogleSansFamily,
                    fontSize = 11.sp,
                    color = BrifiColors.TextDarkSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            CardPlusIcon(
                circleColor = Color(0xFF1F1F1F),
                plusColor = Color.White,
                size = 26.dp,
                modifier = Modifier.clickable(onClick = onDetailsClick)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // White note field inside light card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(66.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BrifiColors.NoteFieldBg)
                .clickable(onClick = onDetailsClick)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = task.note.ifBlank { "Поле для заметок" },
                fontFamily = GoogleSansFamily,
                fontSize = 12.sp,
                color = if (task.note.isBlank()) Color(0xFF9E9E9E) else BrifiColors.TextDark,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UrgencyDeadlinePill(task = task)
            Text(
                text = task.source,
                fontFamily = GoogleSansFamily,
                fontSize = 11.sp,
                color = BrifiColors.TextDarkSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        BrifiActionSlider(
            thumbLabel = "Взять в работу",
            rightActionLabel = task.status,
            onSlideComplete = onTakeInWorkSlide,
            onRightActionClick = onStatusClick,
            thumbWidth = 118.dp,
            height = 38.dp
        )
    }
}

/**
 * Светлая карточка заказа на экране «Статистика» («Заказы за неделю»).
 */
@Composable
fun WeeklyOrderCard(
    task: OrderTask,
    onDetailsClick: () -> Unit,
    onChangeStatusClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(elevation = 8.dp, shape = shape)
            .clip(shape)
            .background(BrifiColors.LightCardBg)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text(
                    text = task.title,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = BrifiColors.TextDark,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${task.orderNumber}${if (task.urgency.isNotBlank()) "  •  ${task.urgency}" else ""}",
                    fontFamily = GoogleSansFamily,
                    fontSize = 11.sp,
                    color = BrifiColors.TextDarkSecondary,
                    maxLines = 1
                )
            }
            CardPlusIcon(
                circleColor = Color(0xFF1F1F1F),
                plusColor = Color.White,
                size = 24.dp,
                modifier = Modifier.clickable(onClick = onDetailsClick)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UrgencyDeadlinePill(task = task)
            Text(
                text = task.source,
                fontFamily = GoogleSansFamily,
                fontSize = 11.sp,
                color = BrifiColors.TextDarkSecondary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Capsule status bar matching Figma: white status pill + black "Изменить статус заказа"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .clip(RoundedCornerShape(50))
                .background(Color(0xFF121212))
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(0.46f)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = task.status,
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = BrifiColors.TextDark,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Box(
                modifier = Modifier
                    .weight(0.54f)
                    .clip(RoundedCornerShape(50))
                    .clickable(onClick = onChangeStatusClick)
                    .padding(horizontal = 8.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Изменить статус заказа",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
