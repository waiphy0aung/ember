package io.github.waiphy0aung.ember.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

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

private val LocalEmberColors = staticCompositionLocalOf { EmberColors() }

/** Wraps every screen so it can read Ember's colours. */
@Composable
fun EmberTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalEmberColors provides EmberColors(), content = content)
}

object EmberTheme {
    val colors: EmberColors
        @Composable get() = LocalEmberColors.current
}
