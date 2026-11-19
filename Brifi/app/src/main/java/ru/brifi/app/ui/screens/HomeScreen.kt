package ru.brifi.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.brifi.app.R
import ru.brifi.app.data.BrifiRepository
import ru.brifi.app.model.BrifiStory
import ru.brifi.app.model.OrderTask
import ru.brifi.app.ui.components.BreathingAura
import ru.brifi.app.ui.components.CreateTaskIcon
import ru.brifi.app.ui.components.InProgressTaskCard
import ru.brifi.app.ui.components.OtherTasksDeck
import ru.brifi.app.ui.components.SaveUrlIcon
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily
import ru.brifi.app.viewmodel.BrifiUiState
import kotlin.math.absoluteValue

private data class QuickLinkItem(
    val label: String,
    val iconRes: Int,
    val url: String
)

@Composable
fun HomeScreen(
    state: BrifiUiState,
    onOpenStory: (BrifiStory) -> Unit,
    onOpenCreateTask: () -> Unit,
    onOpenSaveLink: () -> Unit,
    onMarkDone: (OrderTask) -> Unit,
    onTakeInWork: (OrderTask) -> Unit,
    onOpenDetails: (OrderTask) -> Unit,
    onOpenStatusPicker: (OrderTask) -> Unit,
    onRefresh: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val quickLinks = listOf(
        QuickLinkItem("Чат тг", R.drawable.ic_quick_tg, BrifiRepository.URL_TG_CHAT),
        QuickLinkItem("Таблица", R.drawable.ic_quick_sheets, BrifiRepository.SHEET_URL),
        QuickLinkItem("Принт", R.drawable.ic_quick_print, BrifiRepository.URL_PRINTOFFICE),
        QuickLinkItem("Mail", R.drawable.ic_quick_mail, BrifiRepository.URL_MAIL_RU),
        QuickLinkItem("Диск", R.drawable.ic_quick_disk, BrifiRepository.URL_YANDEX_DISK),
        QuickLinkItem("Макс", R.drawable.ic_quick_max, BrifiRepository.URL_MAX)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrifiColors.Background)
    ) {
        BreathingAura(
            modifier = Modifier.align(Alignment.TopCenter),
            height = 250.dp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .padding(top = 12.dp, bottom = 112.dp)
        ) {
            // Top Logo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Image(
                    painter = painterResource(id = R.drawable.brifi_logo_v2),
                    contentDescription = "БРИФИ",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .height(22.dp)
                        .clickable(onClick = onRefresh)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 4 Top Square Tiles
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TopStatTile(
                    value = state.activeTasks.size.toString(),
                    valueColor = BrifiColors.ActiveCountAccent,
                    caption = "Активных
задач",
                    modifier = Modifier.weight(1f)
                )
                TopStatTile(
                    value = state.deadlineTodayCount.toString(),
                    valueColor = BrifiColors.DeadlineTodayAccent,
                    caption = "Дедлайн
сегодня!",
                    modifier = Modifier.weight(1f)
                )
                TopStoryTile(
                    iconRes = R.drawable.ic_story_printer,
                    caption = "Справка по
производству",
                    onClick = { onOpenStory(BrifiStory.PRODUCTION) },
                    modifier = Modifier.weight(1f)
                )
                TopStoryTile(
                    iconRes = R.drawable.ic_story_checklist,
                    caption = "Чек-лист
перед загрузкой",
                    onClick = { onOpenStory(BrifiStory.CHECKLIST) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2 Big Capsule Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionCapsuleButton(
                    label = "Создать задачу",
                    onClick = onOpenCreateTask,
                    modifier = Modifier.weight(1f)
                ) {
                    CreateTaskIcon()
                }
                ActionCapsuleButton(
                    label = "Сохранить макет",
                    onClick = onOpenSaveLink,
                    modifier = Modifier.weight(1f)
                ) {
                    SaveUrlIcon()
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section: «В работе:»
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(36.dp))
                    .background(BrifiColors.SectionDark)
                    .padding(top = 18.dp, bottom = 14.dp)
            ) {
                Text(
                    text = "В работе:",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BrifiColors.TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                val inProgress = state.inProgressTasks
                if (inProgress.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp)
                            .height(120.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(BrifiColors.InProgressCardBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Нет задач в работе",
                            fontFamily = GoogleSansFamily,
                            fontSize = 14.sp,
                            color = BrifiColors.TextSecondary
                        )
                    }
                } else {
                    val pagerState = rememberPagerState(pageCount = { inProgress.size })
                    HorizontalPager(
                        state = pagerState,
                        contentPadding = PaddingValues(start = 18.dp, end = if (inProgress.size > 1) 46.dp else 18.dp),
                        pageSpacing = 12.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        val task = inProgress[page]
                        val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                        val scale = 1f - (0.04f * pageOffset.coerceIn(0f, 1f))
                        val alpha = 1f - (0.28f * pageOffset.coerceIn(0f, 1f))

                        InProgressTaskCard(
                            task = task,
                            onDoneSlide = { onMarkDone(task) },
                            onStatusClick = { onOpenStatusPicker(task) },
                            onDetailsClick = { onOpenDetails(task) },
                            modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (state.isRefreshing) "Обновляем данные..." else "Дата обновления: ${state.lastUpdatedText}",
                    fontFamily = GoogleSansFamily,
                    fontSize = 10.sp,
                    color = BrifiColors.TextMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onRefresh)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section: «Другие задачи:»
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(36.dp))
                    .background(BrifiColors.SectionDark)
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                Text(
                    text = "Другие задачи:",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = BrifiColors.TextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                OtherTasksDeck(
                    tasks = state.otherActiveTasks,
                    onTakeInWork = onTakeInWork,
                    onDetailsClick = onOpenDetails,
                    onStatusClick = onOpenStatusPicker
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section: «Last but not least»
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Last but not least",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = BrifiColors.TextSecondary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickLinks.forEach { item ->
                        QuickLinkTile(
                            item = item,
                            onClick = {
                                runCatching {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.url)))
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TopStatTile(
    value: String,
    valueColor: Color,
    caption: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(BrifiColors.TileDark),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = valueColor
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = caption,
            fontFamily = GoogleSansFamily,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = BrifiColors.TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun TopStoryTile(
    iconRes: Int,
    caption: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(BrifiColors.TileDark)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = caption,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = caption,
            fontFamily = GoogleSansFamily,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            color = BrifiColors.TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun ActionCapsuleButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .height(82.dp)
            .shadow(elevation = 10.dp, shape = shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .background(BrifiColors.TileDark)
            .border(1.dp, BrifiColors.CapsuleBorder, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        icon()
        Text(
            text = label,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = BrifiColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun QuickLinkTile(
    item: QuickLinkItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .height(58.dp)
            .clip(shape)
            .background(BrifiColors.QuickLinkTile)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = item.label,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            color = BrifiColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 6.dp, top = 5.dp, end = 4.dp)
        )
        Image(
            painter = painterResource(id = item.iconRes),
            contentDescription = item.label,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(34.dp)
                .align(Alignment.BottomEnd)
        )
    }
}
