package ru.brifi.app.ui.modals

import android.app.DatePickerDialog
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.brifi.app.data.SheetsParser
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily
import java.time.LocalDate

@Composable
fun CreateTaskModal(
    onDismiss: () -> Unit,
    onSave: (title: String, orderNumber: String, note: String, deadline: LocalDate) -> Unit
) {
    val context = LocalContext.current
    val today = remember { LocalDate.now() }
    val tomorrow = remember { today.plusDays(1) }

    var title by remember { mutableStateOf("") }
    var orderNumber by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var selectedDeadline by remember { mutableStateOf(today) }
    var showDiscardConfirm by remember { mutableStateOf(false) }

    val hasUnsavedChanges = title.isNotBlank() || orderNumber.isNotBlank() || note.isNotBlank()

    fun attemptClose() {
        if (hasUnsavedChanges) {
            showDiscardConfirm = true
        } else {
            onDismiss()
        }
    }

    fun submit() {
        if (title.isBlank()) return
        onSave(title, orderNumber, note, selectedDeadline)
    }

    Dialog(
        onDismissRequest = { attemptClose() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val modalShape = RoundedCornerShape(38.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .clip(modalShape)
                .background(BrifiColors.ModalBg)
                .border(1.dp, Color.White.copy(alpha = 0.14f), modalShape)
                .padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Новая задача",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f))
                        .clickable { attemptClose() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✕", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ModalInputField(
                label = "Название задачи",
                value = title,
                onValueChange = { title = it },
                placeholder = "Например: Визитки 4+4, 1000шт, Дизайн"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModalInputField(
                label = "Номер заказа",
                value = orderNumber,
                onValueChange = { orderNumber = it },
                placeholder = "Например: 26-001548"
            )

            Spacer(modifier = Modifier.height(12.dp))

            ModalInputField(
                label = "Детали и заметки",
                value = note,
                onValueChange = { note = it },
                placeholder = "Размеры, контакты, и другая информация",
                singleLine = false,
                minHeight = 92.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Дедлайн",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = BrifiColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isToday = selectedDeadline == today
                val isTomorrow = selectedDeadline == tomorrow
                val isCustom = !isToday && !isTomorrow

                DeadlineChip(
                    label = "Сегодня",
                    selected = isToday,
                    onClick = { selectedDeadline = today },
                    modifier = Modifier.weight(1f)
                )
                DeadlineChip(
                    label = "Завтра",
                    selected = isTomorrow,
                    onClick = { selectedDeadline = tomorrow },
                    modifier = Modifier.weight(1f)
                )
                DeadlineChip(
                    label = if (isCustom) SheetsParser.formatDate(selectedDeadline) else "Выбрать дату",
                    selected = isCustom,
                    onClick = {
                        DatePickerDialog(
                            context,
                            { _, y, m, d -> selectedDeadline = LocalDate.of(y, m + 1, d) },
                            selectedDeadline.year,
                            selectedDeadline.monthValue - 1,
                            selectedDeadline.dayOfMonth
                        ).show()
                    },
                    modifier = Modifier.weight(1.2f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (title.isNotBlank()) BrifiColors.ModalButtonPrimary
                        else BrifiColors.ModalButtonPrimary.copy(alpha = 0.45f)
                    )
                    .clickable(enabled = title.isNotBlank()) { submit() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "СОХРАНИТЬ",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    letterSpacing = 0.6.sp,
                    color = Color.White
                )
            }
        }
    }

    if (showDiscardConfirm) {
        ConfirmDiscardModal(
            onDiscard = {
                showDiscardConfirm = false
                onDismiss()
            },
            onSave = {
                showDiscardConfirm = false
                if (title.isNotBlank()) submit()
            },
            onDismiss = { showDiscardConfirm = false }
        )
    }
}

@Composable
fun ConfirmDiscardModal(
    onDiscard: () -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val shape = RoundedCornerShape(32.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(BrifiColors.ModalBg)
                .border(1.dp, Color.White.copy(alpha = 0.16f), shape)
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Точно удалить?",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(50))
                        .clickable(onClick = onDiscard),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Не сохранять",
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .clip(RoundedCornerShape(50))
                        .background(BrifiColors.ModalButtonPrimary)
                        .clickable(onClick = onSave),
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
        }
    }
}

@Composable
fun ModalInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    minHeight: Dp = 44.dp
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    fontFamily = GoogleSansFamily,
                    fontSize = 12.sp,
                    color = BrifiColors.TextMuted
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                textStyle = TextStyle(
                    fontFamily = GoogleSansFamily,
                    fontSize = 13.sp,
                    color = Color.White
                ),
                cursorBrush = SolidColor(BrifiColors.ModalButtonPrimary),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun DeadlineChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(50))
            .background(if (selected) BrifiColors.ModalButtonPrimary else Color(0xFF1C1C1C))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontFamily = GoogleSansFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            color = Color.White,
            maxLines = 1
        )
    }
}
