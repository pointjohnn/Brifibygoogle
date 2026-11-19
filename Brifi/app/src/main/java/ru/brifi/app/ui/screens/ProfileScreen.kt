package ru.brifi.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.brifi.app.R
import ru.brifi.app.data.SheetsParser
import ru.brifi.app.ui.components.BreathingAura
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily
import ru.brifi.app.viewmodel.BrifiUiState

@Composable
fun ProfileScreen(
    state: BrifiUiState,
    onConfirmPayoutClick: () -> Unit,
    onSaveWebhookUrl: (String) -> Unit,
    onRefresh: () -> Unit
) {
    val scrollState = rememberScrollState()
    var webhookInput by remember(state.webhookUrl) { mutableStateOf(state.webhookUrl) }
    val finance = state.finance

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

            val sheetShape = RoundedCornerShape(36.dp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(sheetShape)
                    .background(BrifiColors.SheetSurface)
                    .border(1.dp, BrifiColors.SheetBorder, sheetShape)
                    .padding(horizontal = 18.dp, vertical = 22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Привет,",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BrifiColors.TextPrimary
                )
                Text(
                    text = "Поинт Джон",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BrifiColors.ActiveCountAccent
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "За неделю от ${finance.latestWeek.startDateLabel}",
                    fontFamily = GoogleSansFamily,
                    fontSize = 11.sp,
                    color = BrifiColors.TextMuted
                )
                Text(
                    text = "Ты нахастлил",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = BrifiColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Dashed black earnings card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(106.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Black)
                        .drawBehind {
                            val stroke = Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f), 0f)
                            )
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.25f),
                                topLeft = Offset(3f, 3f),
                                size = Size(size.width - 6f, size.height - 6f),
                                cornerRadius = CornerRadius(24.dp.toPx(), 24.dp.toPx()),
                                style = stroke
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = SheetsParser.formatMoney(finance.latestWeek.amount),
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 44.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Payout status + Confirm payment button (#6157E5)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(BrifiColors.TileDark),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = finance.latestWeek.payoutStatus,
                            fontFamily = GoogleSansFamily,
                            fontSize = 11.sp,
                            color = BrifiColors.TextSecondary,
                            maxLines = 1
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(RoundedCornerShape(50))
                            .background(BrifiColors.ProfileAccent)
                            .clickable(onClick = onConfirmPayoutClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Подтвердить оплату",
                            fontFamily = GoogleSansFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Row 1 of metrics: All time & This month
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ProfileMetricTile(
                        value = SheetsParser.formatMoney(finance.allTimeTotalIncome),
                        caption = "Заработал за все время",
                        modifier = Modifier.weight(1f)
                    )
                    ProfileMetricTile(
                        value = SheetsParser.formatMoney(finance.currentMonthTotalIncome),
                        caption = "Заработал за этот месяц",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Row 2: 2 stacked metrics on left + Pie chart on right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ProfileMetricTile(
                            value = SheetsParser.formatMoney(finance.avgMonthlyCommission50),
                            caption = "Средний процент в месяц"
                        )
                        ProfileMetricTile(
                            value = SheetsParser.formatMoney(finance.avgMonthlyTotalIncome),
                            caption = "Средний доход в месяц"
                        )
                    }

                    // Paid / Free Pie Chart Tile
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(170.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(BrifiColors.TileDark)
                            .padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        val paid = finance.paidOrdersCount.coerceAtLeast(0)
                        val free = finance.freeOrdersCount.coerceAtLeast(0)
                        val total = (paid + free).coerceAtLeast(1)
                        val paidSweep = 360f * (paid.toFloat() / total.toFloat())

                        Canvas(modifier = Modifier.size(96.dp)) {
                            drawArc(
                                color = BrifiColors.ProfilePieSecondary,
                                startAngle = -90f + paidSweep,
                                sweepAngle = 360f - paidSweep,
                                useCenter = true
                            )
                            drawArc(
                                color = BrifiColors.ProfileAccent,
                                startAngle = -90f,
                                sweepAngle = paidSweep,
                                useCenter = true
                            )
                        }

                        Text(
                            text = "Платные/бесплатные
$paid / $free",
                            fontFamily = GoogleSansFamily,
                            fontSize = 10.sp,
                            lineHeight = 13.sp,
                            color = BrifiColors.TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Sync settings: Apps Script Webhook URL + Refresh button
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(BrifiColors.TileDark)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Синхронизация (Apps Script Webhook)",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = BrifiColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        if (webhookInput.isEmpty()) {
                            Text(
                                text = "https://script.google.com/macros/s/.../exec",
                                fontFamily = GoogleSansFamily,
                                fontSize = 11.sp,
                                color = BrifiColors.TextMuted
                            )
                        }
                        BasicTextField(
                            value = webhookInput,
                            onValueChange = { webhookInput = it },
                            textStyle = TextStyle(
                                fontFamily = GoogleSansFamily,
                                fontSize = 12.sp,
                                color = Color.White
                            ),
                            singleLine = true,
                            cursorBrush = SolidColor(BrifiColors.ActiveCountAccent),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(50))
                                .background(BrifiColors.ProfileAccent)
                                .clickable { onSaveWebhookUrl(webhookInput) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Сохранить URL",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .clip(RoundedCornerShape(50))
                                .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(50))
                                .clickable(onClick = onRefresh),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (state.isRefreshing) "Обновление..." else "Обновить данные",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMetricTile(
    value: String,
    caption: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(BrifiColors.TileDark)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 19.sp,
            color = Color.White,
            maxLines = 1
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = caption,
            fontFamily = GoogleSansFamily,
            fontSize = 10.sp,
            color = BrifiColors.TextMuted,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
