package ru.brifi.app.ui.modals

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.brifi.app.model.OrderTask
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

@Composable
fun SaveLayoutLinkModal(
    activeTasks: List<OrderTask>,
    onDismiss: () -> Unit,
    onSaveLink: (OrderTask, String) -> Unit
) {
    var selectedTask by remember(activeTasks) { mutableStateOf(activeTasks.firstOrNull()) }
    var urlInput by remember(selectedTask) { mutableStateOf(selectedTask?.link.orEmpty()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val shape = RoundedCornerShape(38.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(shape)
                .background(BrifiColors.ModalBg)
                .border(1.dp, Color.White.copy(alpha = 0.14f), shape)
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Сохранить макет",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f))
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✕", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Выберите активный заказ:",
                fontFamily = GoogleSansFamily,
                fontSize = 12.sp,
                color = BrifiColors.TextSecondary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 170.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                activeTasks.forEach { task ->
                    val isSelected = selectedTask?.id == task.id
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) BrifiColors.ModalButtonPrimary.copy(alpha = 0.28f) else Color(0xFF171717))
                            .border(
                                1.dp,
                                if (isSelected) BrifiColors.ModalButtonPrimary else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                selectedTask = task
                                urlInput = task.link
                            }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = "${task.orderNumber} — ${task.title}",
                            fontFamily = GoogleSansFamily,
                            fontSize = 12.sp,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ModalInputField(
                label = "Ссылка на макет (Яндекс.Диск и др.)",
                value = urlInput,
                onValueChange = { urlInput = it },
                placeholder = "https://disk.yandex.ru/d/..."
            )

            Spacer(modifier = Modifier.height(18.dp))

            val canSave = selectedTask != null && urlInput.isNotBlank()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (canSave) BrifiColors.ModalButtonPrimary else BrifiColors.ModalButtonPrimary.copy(alpha = 0.45f))
                    .clickable(enabled = canSave) {
                        selectedTask?.let { onSaveLink(it, urlInput) }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "СОХРАНИТЬ ССЫЛКУ",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}
