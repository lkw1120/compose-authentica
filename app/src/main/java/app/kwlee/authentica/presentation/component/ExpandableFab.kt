package app.kwlee.authentica.presentation.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.zIndex
import androidx.compose.ui.unit.dp
import app.kwlee.authentica.R
import app.kwlee.authentica.presentation.theme.MotionTokens

data class ExpandableFabAction(
    val label: String,
    val icon: @Composable () -> Unit,
    val onClick: () -> Unit
)

@Composable
fun ExpandableFab(
    actions: List<ExpandableFabAction>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .graphicsLayer { clip = false }
            .zIndex(10f)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 72.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            actions.forEachIndexed { index, action ->
                ActionChip(
                    expanded = expanded,
                    label = action.label,
                    icon = action.icon,
                    index = index,
                    totalCount = actions.size,
                    onClick = {
                        expanded = false
                        action.onClick()
                    }
                )
            }
        }

        val fabRotation by animateFloatAsState(
            targetValue = if (expanded) 45f else 0f,
            animationSpec = tween(
                durationMillis = MotionTokens.fabRotateDurationMs,
                easing = MotionTokens.standardEasing
            ),
            label = "fabRotation"
        )

        FloatingActionButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            onClick = { expanded = !expanded },
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(R.string.home_fab_add_desc),
                modifier = Modifier.rotate(fabRotation)
            )
        }
    }
}

@Composable
private fun ActionChip(
    expanded: Boolean,
    label: String,
    icon: @Composable () -> Unit,
    index: Int,
    totalCount: Int,
    onClick: () -> Unit
) {
    val enterDelayMillis = index * MotionTokens.submenuStaggerDelayMs
    val exitDelayMillis = (totalCount - 1 - index) * MotionTokens.submenuStaggerDelayMs

    val alpha by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = tween(
            durationMillis = if (expanded) MotionTokens.submenuEnterDurationMs else MotionTokens.submenuExitDurationMs,
            delayMillis = if (expanded) enterDelayMillis else exitDelayMillis,
            easing = if (expanded) MotionTokens.enterEasing else MotionTokens.exitEasing
        ),
        label = "chipAlpha-$index"
    )
    val offsetY by animateFloatAsState(
        targetValue = if (expanded) 0f else 12f,
        animationSpec = tween(
            durationMillis = if (expanded) MotionTokens.submenuEnterDurationMs else MotionTokens.submenuExitDurationMs,
            delayMillis = if (expanded) enterDelayMillis else exitDelayMillis,
            easing = MotionTokens.standardEasing
        ),
        label = "chipOffsetY-$index"
    )
    val scale by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.97f,
        animationSpec = tween(
            durationMillis = if (expanded) MotionTokens.submenuEnterDurationMs else MotionTokens.submenuExitDurationMs,
            delayMillis = if (expanded) enterDelayMillis else exitDelayMillis,
            easing = MotionTokens.standardEasing
        ),
        label = "chipScale-$index"
    )

    Surface(
        modifier = Modifier
            .graphicsLayer {
                this.alpha = alpha
                translationY = offsetY
                scaleX = scale
                scaleY = scale
            }
            .clickable(enabled = alpha > 0.98f, onClick = onClick),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon()
            Text(label)
        }
    }
}
