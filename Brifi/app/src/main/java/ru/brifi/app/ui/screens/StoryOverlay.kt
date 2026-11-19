package ru.brifi.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.brifi.app.R
import ru.brifi.app.model.BrifiStory
import ru.brifi.app.ui.components.BreathingAura
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

private val PRODUCTION_ITEMS = listOf(
    "Идальго: Визитки, бирки, листовки",
    "АДС: Таблички ПВХ, баннеры, самоклейки",
    "РомаПринто: ДТФ, фотографии, индивидуальные позиции, холст на подрамнике",
    "БигНет(УФ 360): УФ Печать, ПВХ(УФ), Самоклейки(УФ)",
    "Фотопринт: Фотографии"
)

private val CHECKLIST_ITEMS = listOf(
    "Размер макета дважды сверен с заказом.",
    "Текст, телефоны, адреса и имена проверены.",
    "Изображения качественные, QR-код работает.",
    "Есть вылеты, контур реза и кривые — если требуются.",
    "Итоговый файл открыт и проверен после экспорта.",
    "В названии есть номер заказа, изделие и размер.",
    "В папке нет старых версий, дублей и лишних файлов.",
    "Загружены все нужные файлы.",
    "Файл открывается с диска, ссылка правильная."
)

@Composable
fun StoryOverlay(
    story: BrifiStory,
    checkedIndices: Set<Int>,
    onToggleChecklist: (Int) -> Unit,
    onClose: () -> Unit
) {
    var dragOffsetY by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationY = dragOffsetY.coerceAtLeast(0f)
                alpha = (1f - (dragOffsetY / 700f)).coerceIn(0.4f, 1f)
            }
            .background(BrifiColors.Background)
            .pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount ->
                        if (dragOffsetY + dragAmount >= 0f) {
                            dragOffsetY += dragAmount
                        }
                    },
                    onDragEnd = {
                        if (dragOffsetY > 140f) {
                            onClose()
                        } else {
                            dragOffsetY = 0f
                        }
                    },
                    onDragCancel = { dragOffsetY = 0f }
                )
            }
    ) {
        BreathingAura(
            modifier = Modifier.align(Alignment.TopCenter),
            height = 260.dp
        )

        // Bottom illustration matching Figma
        Image(
            painter = painterResource(
                id = if (story == BrifiStory.PRODUCTION) R.drawable.ic_story_printer else R.drawable.ic_story_checklist
            ),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .size(if (story == BrifiStory.PRODUCTION) 190.dp else 160.dp)
                .padding(end = 8.dp, bottom = 8.dp)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            // Top bar with Close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (story == BrifiStory.PRODUCTION) "Производство" else "Чек лист",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f))
                        .clickable(onClick = onClose),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✕",
                        fontSize = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                if (story == BrifiStory.PRODUCTION) {
                    PRODUCTION_ITEMS.forEach { line ->
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "•",
                                fontFamily = GoogleSansFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = BrifiColors.ActiveCountAccent
                            )
                            Text(
                                text = line,
                                fontFamily = GoogleSansFamily,
                                fontSize = 15.sp,
                                lineHeight = 21.sp,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    CHECKLIST_ITEMS.forEachIndexed { idx, line ->
                        val checked = idx in checkedIndices
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onToggleChecklist(idx) }
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 2.dp)
                                    .size(20.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (checked) BrifiColors.ActiveCountAccent else Color.Transparent)
                                    .border(
                                        width = 1.5.dp,
                                        color = if (checked) BrifiColors.ActiveCountAccent else Color.White.copy(alpha = 0.6f),
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (checked) {
                                    Text(
                                        text = "✓",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                            Text(
                                text = line,
                                fontFamily = GoogleSansFamily,
                                fontSize = 14.sp,
                                lineHeight = 20.sp,
                                color = if (checked) Color.White.copy(alpha = 0.55f) else Color.White,
                                textDecoration = if (checked) TextDecoration.LineThrough else TextDecoration.None
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }

            Image(
                painter = painterResource(id = R.drawable.brifi_logo_v2),
                contentDescription = "БРИФИ",
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(20.dp)
            )
        }
    }
}
