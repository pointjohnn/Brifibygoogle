package ru.brifi.app

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.brifi.app.model.BrifiTab
import ru.brifi.app.ui.components.FloatingBottomNav
import ru.brifi.app.ui.modals.ChangeStatusModal
import ru.brifi.app.ui.modals.ConfirmPayoutModal
import ru.brifi.app.ui.modals.CreateTaskModal
import ru.brifi.app.ui.modals.SaveLayoutLinkModal
import ru.brifi.app.ui.modals.TaskDetailModal
import ru.brifi.app.ui.screens.HomeScreen
import ru.brifi.app.ui.screens.ProfileScreen
import ru.brifi.app.ui.screens.SplashScreen
import ru.brifi.app.ui.screens.StatsScreen
import ru.brifi.app.ui.screens.StoryOverlay
import ru.brifi.app.ui.theme.BrifiColors
import ru.brifi.app.ui.theme.BrifiTheme
import ru.brifi.app.ui.theme.GoogleSansFamily
import ru.brifi.app.viewmodel.BrifiViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: BrifiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT)
        )
        setContent {
            BrifiTheme {
                BrifiAppRoot(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData(silent = true)
    }
}

@Composable
private fun BrifiAppRoot(viewModel: BrifiViewModel) {
    val state by viewModel.uiState.collectAsState()

    // Auto-clear toast after 2.6 seconds
    LaunchedEffect(state.toastMessage) {
        if (state.toastMessage != null) {
            delay(2600L)
            viewModel.clearToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BrifiColors.Background)
    ) {
        Crossfade(
            targetState = state.selectedTab,
            animationSpec = tween(220),
            label = "brifi_tab_crossfade"
        ) { tab ->
            when (tab) {
                BrifiTab.HOME -> HomeScreen(
                    state = state,
                    onOpenStory = viewModel::openStory,
                    onOpenCreateTask = viewModel::openCreateTaskModal,
                    onOpenSaveLink = viewModel::openSaveLinkModal,
                    onMarkDone = viewModel::markTaskDone,
                    onTakeInWork = viewModel::takeTaskInProgress,
                    onOpenDetails = viewModel::openTaskDetails,
                    onOpenStatusPicker = viewModel::openStatusPicker,
                    onRefresh = { viewModel.refreshData(silent = false) }
                )
                BrifiTab.STATS -> StatsScreen(
                    state = state,
                    onShiftWeek = viewModel::shiftStatsWeek,
                    onOpenDetails = viewModel::openTaskDetails,
                    onOpenStatusPicker = viewModel::openStatusPicker
                )
                BrifiTab.PROFILE -> ProfileScreen(
                    state = state,
                    onConfirmPayoutClick = viewModel::openConfirmPayout,
                    onSaveWebhookUrl = viewModel::saveWebhookUrl,
                    onRefresh = { viewModel.refreshData(silent = false) }
                )
            }
        }

        // Floating glass bottom navigation
        FloatingBottomNav(
            selectedTab = state.selectedTab,
            onSelectTab = viewModel::selectTab,
            onAddClick = viewModel::openCreateTaskModal,
            modifier = Modifier.align(Alignment.BottomCenter)
        )

        // Subtle toast banner above bottom nav
        AnimatedVisibility(
            visible = state.toastMessage != null,
            enter = fadeIn(tween(180)),
            exit = fadeOut(tween(180)),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 92.dp)
        ) {
            state.toastMessage?.let { msg ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xE61F1F24))
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = msg,
                        fontFamily = GoogleSansFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
        }

        // Fullscreen Story Overlay (Production / Checklist)
        AnimatedVisibility(
            visible = state.activeStory != null,
            enter = fadeIn(tween(220)),
            exit = fadeOut(tween(200))
        ) {
            state.activeStory?.let { story ->
                StoryOverlay(
                    story = story,
                    checkedIndices = state.checklistChecked,
                    onToggleChecklist = viewModel::toggleChecklistItem,
                    onClose = viewModel::closeStory
                )
            }
        }

        // Splash Screen
        AnimatedVisibility(
            visible = state.showSplash,
            exit = fadeOut(tween(380))
        ) {
            SplashScreen(lastUpdatedText = state.lastUpdatedText)
        }
    }

    // Modals
    if (state.showCreateTaskModal) {
        CreateTaskModal(
            onDismiss = viewModel::closeCreateTaskModal,
            onSave = viewModel::createTask
        )
    }

    if (state.showSaveLinkModal) {
        SaveLayoutLinkModal(
            activeTasks = state.activeTasks,
            onDismiss = viewModel::closeSaveLinkModal,
            onSaveLink = viewModel::saveOrderLink
        )
    }

    state.selectedTaskForDetails?.let { task ->
        TaskDetailModal(
            task = task,
            onDismiss = viewModel::closeTaskDetails,
            onSaveNote = { viewModel.updateTaskNote(task, it) },
            onChangeStatusClick = { viewModel.openStatusPicker(task) },
            onDeleteTask = { viewModel.deleteTask(task) }
        )
    }

    state.selectedTaskForStatus?.let { task ->
        ChangeStatusModal(
            task = task,
            onSelectStatus = { viewModel.updateTaskStatus(task, it) },
            onDismiss = viewModel::closeStatusPicker
        )
    }

    if (state.showConfirmPayoutModal) {
        ConfirmPayoutModal(
            onSelectMethod = viewModel::confirmWeeklyPayout,
            onDismiss = viewModel::closeConfirmPayout
        )
    }
}
