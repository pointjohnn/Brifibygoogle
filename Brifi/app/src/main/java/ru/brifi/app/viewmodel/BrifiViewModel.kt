package ru.brifi.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.brifi.app.data.BrifiRepository
import ru.brifi.app.data.SheetsParser
import ru.brifi.app.model.BrifiStory
import ru.brifi.app.model.BrifiTab
import ru.brifi.app.model.FinanceSummary
import ru.brifi.app.model.OrderTask
import ru.brifi.app.model.TaskStatuses
import ru.brifi.app.model.WeeklyPayout
import java.time.LocalDate

data class BrifiUiState(
    val showSplash: Boolean = true,
    val selectedTab: BrifiTab = BrifiTab.HOME,
    val isRefreshing: Boolean = false,
    val lastUpdatedText: String = "Ещё не обновлялось",
    val tasks: List<OrderTask> = emptyList(),
    val finance: FinanceSummary = FinanceSummary(
        latestWeek = WeeklyPayout("", "текущей недели", "Ожидает оплаты", 0L),
        allTimeTotalIncome = 0L,
        allTimeCommission50 = 0L,
        allTimeSalary = 0L,
        currentMonthLabel = "Этот месяц",
        currentMonthTotalIncome = 0L,
        currentMonthCommission50 = 0L,
        currentMonthSalary = 0L
    ),
    val webhookUrl: String = "",
    val checklistChecked: Set<Int> = emptySet(),
    val activeStory: BrifiStory? = null,
    val showCreateTaskModal: Boolean = false,
    val showSaveLinkModal: Boolean = false,
    val selectedTaskForDetails: OrderTask? = null,
    val selectedTaskForStatus: OrderTask? = null,
    val showConfirmPayoutModal: Boolean = false,
    val statsWeeksAgo: Int = 0,
    val toastMessage: String? = null
) {
    val activeTasks: List<OrderTask>
        get() = tasks.filter { it.isActive }

    val inProgressTasks: List<OrderTask>
        get() = SheetsParser.selectInProgressTasks(tasks)

    val otherActiveTasks: List<OrderTask>
        get() = SheetsParser.selectOtherActiveTasks(tasks, inProgressTasks)

    val deadlineTodayCount: Int
        get() {
            val today = LocalDate.now()
            return activeTasks.count { it.deadline == today }
        }

    val doneTodayCount: Int
        get() {
            val today = LocalDate.now()
            return tasks.count { it.isCompleted && (it.deadline == today || it.createdDate == today) }
        }

    val deadlineTomorrowCount: Int
        get() {
            val tomorrow = LocalDate.now().plusDays(1)
            return activeTasks.count { it.deadline == tomorrow }
        }
}

class BrifiViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = BrifiRepository(application.applicationContext)

    private val _uiState = MutableStateFlow(
        BrifiUiState(
            webhookUrl = repo.getWebhookUrl(),
            lastUpdatedText = repo.getLastSyncText(),
            checklistChecked = repo.getChecklistChecked()
        )
    )
    val uiState: StateFlow<BrifiUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repo.loadCached()?.let { cached ->
                _uiState.update {
                    it.copy(
                        tasks = cached.tasks,
                        finance = cached.finance,
                        lastUpdatedText = cached.lastUpdatedText
                    )
                }
            }
            refreshData(silent = true)
        }

        // Splash screen minimum smooth display
        viewModelScope.launch {
            delay(1500)
            _uiState.update { it.copy(showSplash = false) }
        }

        // Auto-refresh every 5 minutes
        viewModelScope.launch {
            while (isActive) {
                delay(5 * 60 * 1000L)
                refreshData(silent = true)
            }
        }
    }

    fun refreshData(silent: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val result = repo.refreshFromGoogleSheets()
            _uiState.update {
                it.copy(
                    isRefreshing = false,
                    tasks = result.tasks,
                    finance = result.finance,
                    lastUpdatedText = result.lastUpdatedText,
                    toastMessage = when {
                        result.errorMessage != null && !silent -> result.errorMessage
                        !silent -> "Данные обновлены ✓"
                        else -> it.toastMessage
                    }
                )
            }
        }
    }

    fun selectTab(tab: BrifiTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun openStory(story: BrifiStory) {
        _uiState.update { it.copy(activeStory = story) }
    }

    fun closeStory() {
        _uiState.update { it.copy(activeStory = null) }
    }

    fun toggleChecklistItem(index: Int) {
        val current = _uiState.value.checklistChecked.toMutableSet()
        if (!current.add(index)) current.remove(index)
        repo.saveChecklistChecked(current)
        _uiState.update { it.copy(checklistChecked = current) }
    }

    fun openCreateTaskModal() = _uiState.update { it.copy(showCreateTaskModal = true) }
    fun closeCreateTaskModal() = _uiState.update { it.copy(showCreateTaskModal = false) }

    fun openSaveLinkModal() = _uiState.update { it.copy(showSaveLinkModal = true) }
    fun closeSaveLinkModal() = _uiState.update { it.copy(showSaveLinkModal = false) }

    fun openTaskDetails(task: OrderTask) = _uiState.update { it.copy(selectedTaskForDetails = task) }
    fun closeTaskDetails() = _uiState.update { it.copy(selectedTaskForDetails = null) }

    fun openStatusPicker(task: OrderTask) = _uiState.update { it.copy(selectedTaskForStatus = task) }
    fun closeStatusPicker() = _uiState.update { it.copy(selectedTaskForStatus = null) }

    fun openConfirmPayout() = _uiState.update { it.copy(showConfirmPayoutModal = true) }
    fun closeConfirmPayout() = _uiState.update { it.copy(showConfirmPayoutModal = false) }

    fun shiftStatsWeek(delta: Int) {
        _uiState.update { state ->
            val next = (state.statsWeeksAgo + delta).coerceAtLeast(0)
            state.copy(statsWeeksAgo = next)
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun saveWebhookUrl(url: String) {
        repo.saveWebhookUrl(url)
        _uiState.update {
            it.copy(
                webhookUrl = url.trim(),
                toastMessage = "Ссылка скрипта сохранена ✓"
            )
        }
    }

    fun takeTaskInProgress(task: OrderTask) {
        updateTaskStatus(task, TaskStatuses.IN_PROGRESS, "Взято в работу: ${task.title}")
    }

    fun markTaskDone(task: OrderTask) {
        updateTaskStatus(task, TaskStatuses.READY, "Готово: ${task.title}")
    }

    fun updateTaskStatus(task: OrderTask, newStatus: String, customToast: String? = null) {
        // Optimistic UI update
        _uiState.update { state ->
            val updated = state.tasks.map { if (it.id == task.id) it.copy(status = newStatus) else it }
            val updatedDetails = state.selectedTaskForDetails?.let {
                if (it.id == task.id) it.copy(status = newStatus) else it
            }
            state.copy(
                tasks = updated,
                selectedTaskForDetails = updatedDetails,
                selectedTaskForStatus = null,
                toastMessage = customToast ?: "Статус: $newStatus"
            )
        }
        viewModelScope.launch {
            val syncMsg = repo.updateTaskStatus(task, newStatus)
            if (!syncMsg.isNullOrBlank()) {
                _uiState.update { it.copy(toastMessage = syncMsg) }
            }
        }
    }

    fun updateTaskNote(task: OrderTask, newNote: String) {
        val clean = newNote.trim()
        _uiState.update { state ->
            val updated = state.tasks.map { if (it.id == task.id) it.copy(note = clean) else it }
            val updatedDetails = state.selectedTaskForDetails?.let {
                if (it.id == task.id) it.copy(note = clean) else it
            }
            state.copy(
                tasks = updated,
                selectedTaskForDetails = updatedDetails,
                toastMessage = "Заметка сохранена"
            )
        }
        viewModelScope.launch {
            val syncMsg = repo.updateTaskNote(task, clean)
            if (!syncMsg.isNullOrBlank()) {
                _uiState.update { it.copy(toastMessage = syncMsg) }
            }
        }
    }

    fun saveOrderLink(task: OrderTask, url: String) {
        val clean = url.trim()
        _uiState.update { state ->
            val updated = state.tasks.map { if (it.id == task.id) it.copy(link = clean) else it }
            state.copy(
                tasks = updated,
                showSaveLinkModal = false,
                toastMessage = "Ссылка сохранена"
            )
        }
        viewModelScope.launch {
            val syncMsg = repo.saveOrderLink(task, clean)
            if (!syncMsg.isNullOrBlank()) {
                _uiState.update { it.copy(toastMessage = syncMsg) }
            }
        }
    }

    fun createTask(title: String, orderNumber: String, note: String, deadline: LocalDate) {
        viewModelScope.launch {
            val (newTask, syncMsg) = repo.createTask(title, orderNumber, note, deadline)
            _uiState.update { state ->
                state.copy(
                    tasks = listOf(newTask) + state.tasks,
                    showCreateTaskModal = false,
                    toastMessage = syncMsg ?: "Задача создана ✓"
                )
            }
        }
    }

    fun deleteTask(task: OrderTask) {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.filterNot { it.id == task.id },
                selectedTaskForDetails = null,
                toastMessage = "Задача удалена"
            )
        }
        viewModelScope.launch {
            val syncMsg = repo.deleteTask(task)
            if (!syncMsg.isNullOrBlank()) {
                _uiState.update { it.copy(toastMessage = syncMsg) }
            }
        }
    }

    fun confirmWeeklyPayout(methodText: String) {
        _uiState.update { state ->
            state.copy(
                finance = state.finance.copy(
                    latestWeek = state.finance.latestWeek.copy(payoutStatus = methodText)
                ),
                showConfirmPayoutModal = false,
                toastMessage = "Оплата подтверждена: $methodText"
            )
        }
        viewModelScope.launch {
            val syncMsg = repo.confirmWeeklyPayout(methodText)
            if (!syncMsg.isNullOrBlank()) {
                _uiState.update { it.copy(toastMessage = syncMsg) }
            }
        }
    }
}
