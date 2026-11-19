package ru.brifi.app.ui.modals

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import ru.brifi.app.model.OrderTask
import ru.brifi.app.model.TaskStatuses
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

@Composable
fun ChangeStatusModal(
    task: OrderTask,
    onSelectStatus: (String) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        val shape = RoundedCornerShape(32.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(BrifiColors.ModalBg)
                .border(1.dp, Color.White.copy(alpha = 0.14f), shape)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Изменить статус",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.White
            )
            Text(
                text = task.title,
                fontFamily = GoogleSansFamily,
                fontSize = 12.sp,
                color = BrifiColors.TextSecondary,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))

            TaskStatuses.all.forEach { status ->
                val isCurrent = status.equals(task.status, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (isCurrent) BrifiColors.ModalButtonPrimary else Color(0xFF1A1A1A))
                        .clickable { onSelectStatus(status) }
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = status,
                        fontFamily = GoogleSansFamily,
                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
