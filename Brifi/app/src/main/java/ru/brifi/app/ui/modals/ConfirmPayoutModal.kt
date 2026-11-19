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
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

@Composable
fun ConfirmPayoutModal(
    onSelectMethod: (String) -> Unit,
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
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Подтвердить оплату",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.White
            )
            Text(
                text = "Выберите способ получения выплаты:",
                fontFamily = GoogleSansFamily,
                fontSize = 12.sp,
                color = BrifiColors.TextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(50))
                    .background(BrifiColors.ProfileAccent)
                    .clickable { onSelectMethod("Переводом на карту") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Переводом на карту",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF1C1C1C))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50))
                    .clickable { onSelectMethod("Выплачено из кассы") },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Выплачено из кассы",
                    fontFamily = GoogleSansFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = Color.White
                )
            }
        }
    }
}
