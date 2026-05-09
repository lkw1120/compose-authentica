package app.kwlee.authentica.presentation.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import app.kwlee.authentica.R
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun SwipeToRevealItem(
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val actionWidth = 96.dp
    val density = LocalDensity.current
    val actionWidthPx = with(density) { actionWidth.toPx() }

    val offsetX = remember { Animatable(0f) }
    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    val contentHeight = with(density) { contentSize.height.toDp() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clipToBounds()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(actionWidth)
                .height(contentHeight)
                .background(MaterialTheme.colorScheme.secondaryContainer)
                .clickable {
                    onEdit()
                    scope.launch { offsetX.animateTo(0f, spring()) }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.swipe_action_edit))
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(actionWidth)
                .height(contentHeight)
                .background(MaterialTheme.colorScheme.errorContainer)
                .clickable {
                    onDelete()
                    scope.launch { offsetX.animateTo(0f, spring()) }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.swipe_action_delete))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .background(MaterialTheme.colorScheme.surface)
                .onSizeChanged { contentSize = it }
                .pointerInput(actionWidthPx) {
                    detectHorizontalDragGestures(
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            val next = (offsetX.value + dragAmount).coerceIn(-actionWidthPx, actionWidthPx)
                            scope.launch { offsetX.snapTo(next) }
                        },
                        onDragEnd = {
                            val target = when {
                                abs(offsetX.value) < actionWidthPx * 0.5f -> 0f
                                offsetX.value > 0f -> actionWidthPx
                                else -> -actionWidthPx
                            }
                            scope.launch { offsetX.animateTo(target, spring()) }
                        }
                    )
                }
        ) {
            content()
        }
    }
}
