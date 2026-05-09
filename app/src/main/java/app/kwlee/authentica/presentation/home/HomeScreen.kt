package app.kwlee.authentica.presentation.home

import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.kwlee.authentica.R
import app.kwlee.authentica.presentation.component.DeleteConfirmBottomSheet
import app.kwlee.authentica.presentation.component.EditAccountBottomSheet
import app.kwlee.authentica.presentation.component.ExpandableFab
import app.kwlee.authentica.presentation.component.ExpandableFabAction
import app.kwlee.authentica.presentation.component.OtpAccountCard
import app.kwlee.authentica.presentation.component.SwipeToRevealItem
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeUiState,
    snackbarHostState: SnackbarHostState,
    onOpenSettings: () -> Unit,
    onScanQrCode: () -> Unit,
    onOpenManualInput: () -> Unit,
    onDeleteAccount: (String) -> Unit,
    onReorderAccounts: (List<String>) -> Unit,
    onStartEditing: (OtpAccountUiModel) -> Unit,
    onCancelEditing: () -> Unit,
    onSaveEdit: (issuer: String, accountName: String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val view = LocalView.current
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var isSearchMode by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val searchFocusRequester = remember { FocusRequester() }
    val trimmedQuery = searchQuery.trim()
    val filteredItems = remember(state.items, trimmedQuery) {
        state.items.filter { item ->
            trimmedQuery.isBlank() ||
                item.issuer.contains(trimmedQuery, ignoreCase = true) ||
                item.accountName.contains(trimmedQuery, ignoreCase = true)
        }
    }
    val localItems = remember { mutableStateListOf<OtpAccountUiModel>() }
    var draggingIndex by remember { mutableIntStateOf(-1) }
    var draggingItemId by remember { mutableStateOf<String?>(null) }
    var dragStartOrder by remember { mutableStateOf<List<String>>(emptyList()) }
    var dragOffsetY by remember { mutableFloatStateOf(0f) }
    var itemHeightPx by remember { mutableFloatStateOf(1f) }

    val codeCopiedMessage = stringResource(R.string.home_code_copied)
    var pendingDeleteId by remember { mutableStateOf<String?>(null) }

    pendingDeleteId?.let { id ->
        DeleteConfirmBottomSheet(
            onDismiss = { pendingDeleteId = null },
            onConfirm = {
                onDeleteAccount(id)
                pendingDeleteId = null
            }
        )
    }

    LaunchedEffect(filteredItems, trimmedQuery) {
        if (draggingIndex == -1) {
            localItems.clear()
            localItems.addAll(filteredItems)
        }
    }
    LaunchedEffect(isSearchMode) {
        if (isSearchMode) {
            searchFocusRequester.requestFocus()
        }
    }

    if (state.editingItem != null) {
        EditAccountBottomSheet(
            item = state.editingItem,
            onDismiss = onCancelEditing,
            onConfirm = onSaveEdit
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchMode) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(searchFocusRequester),
                            singleLine = true,
                            placeholder = { Text(stringResource(R.string.home_search_placeholder)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )
                    } else {
                        Text(stringResource(R.string.home_title))
                    }
                },
                navigationIcon = {
                    if (isSearchMode) {
                        IconButton(
                            onClick = {
                                isSearchMode = false
                                searchQuery = ""
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.home_search_close_desc))
                        }
                    } else {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Security, contentDescription = null)
                        }
                    }
                },
                actions = {
                    if (!isSearchMode) {
                        IconButton(onClick = { isSearchMode = true }) {
                            Icon(Icons.Default.Search, contentDescription = stringResource(R.string.home_search_desc))
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.home_settings_desc))
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            ExpandableFab(
                actions = listOf(
                    ExpandableFabAction(
                        label = stringResource(R.string.home_fab_scan),
                        icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = stringResource(R.string.home_fab_scan)) },
                        onClick = onScanQrCode
                    ),
                    ExpandableFabAction(
                        label = stringResource(R.string.home_fab_manual),
                        icon = { Icon(Icons.Default.Keyboard, contentDescription = stringResource(R.string.home_fab_manual)) },
                        onClick = onOpenManualInput
                    )
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (state.items.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_empty_title),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Text(
                        text = stringResource(R.string.home_empty_subtitle),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            } else if (filteredItems.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.home_search_empty_title),
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Text(
                        text = stringResource(R.string.home_search_empty_subtitle),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                    )
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    itemsIndexed(localItems, key = { _, item -> item.id }) { index, item ->
                        val progress by animateFloatAsState(
                            targetValue = item.progressFraction,
                            animationSpec = tween(durationMillis = 120, easing = LinearEasing),
                            label = "otpProgress"
                        )

                        Box(
                            modifier = Modifier
                                .zIndex(if (draggingIndex == index) 1f else 0f)
                                .offset {
                                    IntOffset(
                                        x = 0,
                                        y = if (draggingIndex == index) dragOffsetY.roundToInt() else 0
                                    )
                                }
                                .pointerInput(item.id, localItems.size, trimmedQuery) {
                                    if (trimmedQuery.isNotBlank()) return@pointerInput
                                    detectDragGesturesAfterLongPress(
                                        onDragStart = {
                                            draggingItemId = item.id
                                            draggingIndex = localItems.indexOfFirst { it.id == item.id }
                                            dragStartOrder = localItems.map { it.id }
                                            dragOffsetY = 0f
                                            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        },
                                        onDrag = { change, dragAmount ->
                                            val currentIndex = localItems.indexOfFirst { it.id == draggingItemId }
                                            if (currentIndex == -1) return@detectDragGesturesAfterLongPress
                                            draggingIndex = currentIndex
                                            change.consume()
                                            dragOffsetY += dragAmount.y
                                            val threshold = itemHeightPx * 0.5f
                                            if (dragOffsetY > threshold && draggingIndex < localItems.lastIndex) {
                                                val current = draggingIndex
                                                val target = current + 1
                                                val temp = localItems[current]
                                                    localItems[current] = localItems[target]
                                                    localItems[target] = temp
                                                    draggingIndex = target
                                                    if (current == listState.firstVisibleItemIndex || target == listState.firstVisibleItemIndex) {
                                                        listState.requestScrollToItem(
                                                            index = listState.firstVisibleItemIndex,
                                                            scrollOffset = listState.firstVisibleItemScrollOffset
                                                        )
                                                    }
                                                    dragOffsetY -= itemHeightPx
                                                } else if (dragOffsetY < -threshold && draggingIndex > 0) {
                                                    val current = draggingIndex
                                                    val target = current - 1
                                                    val temp = localItems[current]
                                                    localItems[current] = localItems[target]
                                                    localItems[target] = temp
                                                    draggingIndex = target
                                                    if (current == listState.firstVisibleItemIndex || target == listState.firstVisibleItemIndex) {
                                                        listState.requestScrollToItem(
                                                            index = listState.firstVisibleItemIndex,
                                                            scrollOffset = listState.firstVisibleItemScrollOffset
                                                        )
                                                    }
                                                    dragOffsetY += itemHeightPx
                                                }
                                            },
                                            onDragEnd = {
                                                val moved = draggingIndex != -1 &&
                                                    localItems.map { it.id } != dragStartOrder &&
                                                    trimmedQuery.isBlank()
                                                draggingIndex = -1
                                                draggingItemId = null
                                                dragOffsetY = 0f
                                                if (moved) {
                                                    onReorderAccounts(localItems.map { it.id })
                                                }
                                            },
                                            onDragCancel = {
                                                draggingIndex = -1
                                                draggingItemId = null
                                                dragOffsetY = 0f
                                                localItems.clear()
                                                localItems.addAll(filteredItems)
                                            }
                                        )
                                    }
                            ) {
                                SwipeToRevealItem(
                                    onDelete = { pendingDeleteId = item.id },
                                    onEdit = { onStartEditing(item) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .onSizeChanged {
                                            if (it.height > 0) {
                                                itemHeightPx = it.height.toFloat()
                                            }
                                        }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    ) {
                                        OtpAccountCard(
                                            item = item,
                                            progress = progress,
                                            onCopy = {
                                                clipboardManager.setText(AnnotatedString(item.code))
                                                Toast.makeText(context, codeCopiedMessage, Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    }
                                }
                            }
                        if (index < localItems.lastIndex) {
                            HorizontalDivider()
                        }
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = innerPadding.calculateBottomPadding() + 8.dp)
            )
        }
    }
}
