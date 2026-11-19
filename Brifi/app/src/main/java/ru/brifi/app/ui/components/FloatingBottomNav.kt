package ru.brifi.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.brifi.app.model.BrifiTab
import ru.brifi.app.ui.theme.GoogleSansFamily

@Composable
fun FloatingBottomNav(
    selectedTab: BrifiTab,
    onSelectTab: (BrifiTab) -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val glassGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xD8242428),
            Color(0xE6141418)
        )
    )
    val borderBrush = Brush.verticalGradient(
        colors = listOf(
            Color.White.copy(alpha = 0.28f),
            Color.White.copy(alpha = 0.08f)
        )
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Translucent glass navigation capsule
        Row(
            modifier = Modifier
                .height(60.dp)
                .shadow(elevation = 18.dp, shape = RoundedCornerShape(50), ambientColor = Color.Black, spotColor = Color.Black)
                .clip(RoundedCornerShape(50))
                .background(glassGradient)
                .border(1.dp, borderBrush, RoundedCornerShape(50))
                .padding(horizontal = 6.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            NavTabItem(
                selected = selectedTab == BrifiTab.HOME,
                onClick = { onSelectTab(BrifiTab.HOME) }
            ) { tint, cutout ->
                NavHomeBrifiIcon(tint = tint, cutoutColor = cutout)
            }
            NavTabItem(
                selected = selectedTab == BrifiTab.STATS,
                onClick = { onSelectTab(BrifiTab.STATS) }
            ) { tint, _ ->
                NavStatsIcon(tint = tint)
            }
            NavTabItem(
                selected = selectedTab == BrifiTab.PROFILE,
                onClick = { onSelectTab(BrifiTab.PROFILE) }
            ) { tint, _ ->
                NavProfileIcon(tint = tint)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Separate circular + button
        Box(
            modifier = Modifier
                .size(60.dp)
                .shadow(elevation = 18.dp, shape = CircleShape, ambientColor = Color.Black, spotColor = Color.Black)
                .clip(CircleShape)
                .background(glassGradient)
                .border(1.dp, borderBrush, CircleShape)
                .clickable(onClick = onAddClick),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                fontFamily = GoogleSansFamily,
                fontWeight = FontWeight.Light,
                fontSize = 30.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun NavTabItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable (tint: Color, cutout: Color) -> Unit
) {
    val bg by animateColorAsState(
        targetValue = if (selected) Color.White.copy(alpha = 0.18f) else Color.Transparent,
        animationSpec = tween(220),
        label = "nav_pill_bg"
    )
    val tint by animateColorAsState(
        targetValue = if (selected) Color.White else Color.White.copy(alpha = 0.65f),
        animationSpec = tween(220),
        label = "nav_icon_tint"
    )
    val cutout = if (selected) Color(0xFF28282E) else Color(0xFF18181C)

    Box(
        modifier = Modifier
            .width(66.dp)
            .height(50.dp)
            .clip(RoundedCornerShape(50))
            .background(bg)
            .then(
                if (selected) Modifier.border(
                    width = 0.8.dp,
                    color = Color.White.copy(alpha = 0.22f),
                    shape = RoundedCornerShape(50)
                ) else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        icon(tint, cutout)
    }
}
