package app.kwlee.authentica.presentation.theme

import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing

object MotionTokens {
    // Google Material reference easings.
    val emphasizedEasing: Easing = FastOutSlowInEasing
    val enterEasing: Easing = LinearOutSlowInEasing
    val exitEasing: Easing = FastOutLinearInEasing
    val standardEasing: Easing = FastOutSlowInEasing

    const val fabRotateDurationMs = 220
    const val submenuEnterDurationMs = 220
    const val submenuExitDurationMs = 160
    const val submenuStaggerDelayMs = 36
    const val submenuSlideOffsetDivisor = 2
}
