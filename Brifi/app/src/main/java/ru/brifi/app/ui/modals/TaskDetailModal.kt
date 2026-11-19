package ru.brifi.app.ui.modals

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.brifi.app.model.OrderTask
import ru.brifi.app.ui.components.UrgencyDeadlinePill
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

@Composable
fun TaskDetailModal(
    task: OrderTask,
    onDismiss: () -> Unit,
    onSaveNote: (String) -> Unit,
    onChangeStatusClick: () -> Unit,
    onDeleteTask: () -> Unit
) {
    val context = LocalContext.current
    var noteInput by remember(task.id, task.note) { mutableStateOf(task.note) }
    var confirmDelete by remember { mutableStateOf(false) }

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
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = task.title,
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${task.orderNumber}  •  ${task.status}",
                        fontFamily = GoogleSansFamily,
                        fontSize = 12.sp,
                        color = BrifiColors.TextSecondary
                    )
                }
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UrgencyDeadlinePill(task = task)
                if (task.link.isNotBlank()) {
                    Text(
                        text = "Открыть макет ↗",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        color = BrifiColors.DeadlineTodayAccent,
                        modifier = Modifier.clickable {
                            runCatching {
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(task.link)))
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            ModalInputField(
                label = "Заметка к заказу",
                value = noteInput,
                onValueChange = { noteInput = it },
                placeholder = "Добавьте заметку...",
                singleLine = false,
                minHeight = 96.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(50))
                        .clickable(onClick = onChangeStatusClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Статус",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1.4f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(50))
                        .background(BrifiColors.ModalButtonPrimary)
                        .clickable { onSaveNote(noteInput) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "СОХРАНИТЬ",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Удалить задачу",
                fontFamily = GoogleSansFamily,
                fontSize = 12.sp,
                color = BrifiColors.UrgencyHighBg,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clickable { confirmDelete = true }
                    .padding(6.dp)
            )
        }
    }

    if (confirmDelete) {
        ConfirmDiscardModal(
            onDiscard = {
                confirmDelete = false
                onDeleteTask()
            },
            onSave = { confirmDelete = false },
            onDismiss = { confirmDelete = false }
        )
    }
}
