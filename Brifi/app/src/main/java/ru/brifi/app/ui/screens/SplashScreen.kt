package ru.brifi.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.brifi.app.R
import ru.brifi.app.ui.components.BreathingAura
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.GoogleSansFamily

@Composable
fun SplashScreen(lastUpdatedText: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrifiColors.Background)
    ) {
        // Breathing aura top & bottom
        BreathingAura(
            modifier = Modifier.align(Alignment.TopCenter),
            height = 280.dp,
            inverted = false
        )
        BreathingAura(
            modifier = Modifier.align(Alignment.BottomCenter),
            height = 240.dp,
            inverted = true
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Разработано",
                    fontFamily = GoogleSansFamily,
                    fontSize = 11.sp,
                    color = BrifiColors.TextPrimary.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = painterResource(id = R.drawable.point_logo),
                    contentDescription = "ПОИНТ.",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.height(22.dp)
                )
            }

            Image(
                painter = painterResource(id = R.drawable.brifi_logo_v2),
                contentDescription = "БРИФИ",
                contentScale = ContentScale.Fit,
                modifier = Modifier.width(210.dp)
            )

            Text(
                text = "Дата обновления данных: $lastUpdatedText",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 11.sp,
                color = BrifiColors.TextPrimary.copy(alpha = 0.72f)
            )
        }
    }
}
