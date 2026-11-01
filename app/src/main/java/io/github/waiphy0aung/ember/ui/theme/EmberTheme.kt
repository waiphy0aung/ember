package io.github.waiphy0aung.ember.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Ember's colours. Screens read them from [EmberTheme.colors]; no hex values outside this file. */
@Immutable
data class EmberColors(
    val night: Color = Color(0xFF120D0B),
    val surface: Color = Color(0xFF1E1714),
    val line: Color = Color(0xFF2E2420),
    val text: Color = Color(0xFFF6EEE7),
    val textQuiet: Color = Color(0xFFB3A396),
    val ember: Color = Color(0xFFFF8A3D),
    val emberLight: Color = Color(0xFFFFB37A),
    val emberDeep: Color = Color(0xFFE0582A),
    val log: Color = Color(0xFF4A362C),
    val logLight: Color = Color(0xFF5A4134),
)

/** Spacing steps. Screens use these instead of raw dp values. */
@Immutable
data class EmberSpacing(
    val xs: Dp = 4.dp,
    val s: Dp = 8.dp,
    val m: Dp = 16.dp,
    val l: Dp = 24.dp,
    val xl: Dp = 28.dp,
)

/** Text styles without colour; screens add the colour from [EmberTheme.colors]. */
@Immutable
data class EmberType(
    val display: TextStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    val title: TextStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    val body: TextStyle = TextStyle(fontSize = 15.sp),
    val caption: TextStyle = TextStyle(fontSize = 13.sp),
)

/** Fixed sizes of Ember's drawn pieces. */
@Immutable
data class EmberSizes(
    val unlitLogs: DpSize = DpSize(width = 180.dp, height = 72.dp),
)

private val LocalEmberColors = staticCompositionLocalOf { EmberColors() }
private val LocalEmberSpacing = staticCompositionLocalOf { EmberSpacing() }
private val LocalEmberType = staticCompositionLocalOf { EmberType() }
private val LocalEmberSizes = staticCompositionLocalOf { EmberSizes() }

/** Wraps every screen so it can read Ember's colours, spacing, type and sizes. */
@Composable
fun EmberTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalEmberColors provides EmberColors(),
        LocalEmberSpacing provides EmberSpacing(),
        LocalEmberType provides EmberType(),
        LocalEmberSizes provides EmberSizes(),
        content = content,
    )
}

object EmberTheme {
    val colors: EmberColors
        @Composable get() = LocalEmberColors.current

    val spacing: EmberSpacing
        @Composable get() = LocalEmberSpacing.current

    val type: EmberType
        @Composable get() = LocalEmberType.current

    val sizes: EmberSizes
        @Composable get() = LocalEmberSizes.current
}
