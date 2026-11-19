package ru.brifi.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.brifi.app.R
import ru.brifi.app.data.SheetsParser
import ru.brifi.app.model.OrderTask
import ru.brifi.app.ui.components.BreathingAura
import ru.brifi.app.ui.components.WeeklyOrderCard
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily
import ru.brifi.app.viewmodel.BrifiUiState
import java.time.LocalDate

@Composable
fun StatsScreen(
    state: BrifiUiState,
    onShiftWeek: (Int) -> Unit,
    onOpenDetails: (OrderTask) -> Unit,
    onOpenStatusPicker: (OrderTask) -> Unit
) {
    val scrollState = rememberScrollState()
    val today = LocalDate.now()
    val (weekStart, weekEnd) = SheetsParser.weekBounds(today, state.statsWeeksAgo)
    val weekTasks = SheetsParser.filterTasksForWeek(state.tasks, weekStart, weekEnd)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrifiColors.Background)
    ) {
        BreathingAura(
            modifier = Modifier.align(Alignment.TopCenter),
            height = 230.dp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(top = 12.dp, bottom = 112.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.brifi_logo_v2),
                contentDescription = "БРИФИ",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(22.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Main dark sheet surface (#2C2C2C, 1dp #3B3B3B, rx 36dp)
            val sheetShape = RoundedCornerShape(36.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(sheetShape)
                    .background(BrifiColors.SheetSurface)
                    .border(1.dp, BrifiColors.SheetBorder, sheetShape)
                    .padding(horizontal = 18.dp, vertical = 20.dp)
            ) {
                Text(
                    text = "Статистика",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BrifiColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 2x2 KPI grid (#0E04AC, #6C61FF, #50A5D1, #E7E7E7)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiTile(
                        value = state.activeTasks.size.toString(),
                        label = "Задач всего",
                        bgColor = BrifiColors.KpiTotal,
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    KpiTile(
                        value = state.deadlineTodayCount.toString(),
                        label = "Задачи на сегодня",
                        bgColor = BrifiColors.KpiToday,
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    KpiTile(
                        value = state.doneTodayCount.toString(),
                        label = "Задача выполнена сегодня",
                        bgColor = BrifiColors.KpiDoneToday,
                        contentColor = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    KpiTile(
                        value = state.deadlineTomorrowCount.toString(),
                        label = "Задач на завтра",
                        bgColor = BrifiColors.KpiTomorrow,
                        contentColor = Color(0xFF3E34C8),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "Заказы за неделю",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = BrifiColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (weekTasks.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "На этой неделе заказов нет",
                            fontFamily = GoogleSansFamily,
                            fontSize = 13.sp,
                            color = BrifiColors.TextSecondary
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        weekTasks.forEach { task ->
                            WeeklyOrderCard(
                                task = task,
                                onDetailsClick = { onOpenDetails(task) },
                                onChangeStatusClick = { onOpenStatusPicker(task) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Week switcher: < Неделя от ДД.ММ.ГГГГ >
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "‹",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = BrifiColors.SliderThumb,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onShiftWeek(1) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                    Text(
                        text = SheetsParser.formatWeekStart(weekStart),
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = BrifiColors.TextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "›",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = if (state.statsWeeksAgo > 0) BrifiColors.SliderThumb else BrifiColors.TextMuted.copy(alpha = 0.4f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(enabled = state.statsWeeksAgo > 0) { onShiftWeek(-1) }
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiTile(
    value: String,
    label: String,
    bgColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .height(98.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = value,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 34.sp,
            color = contentColor
        )
        Text(
            text = label,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 13.sp,
            color = contentColor.copy(alpha = 0.9f),
            maxLines = 2
        )
    }
}
