package io.github.waiphy0aung.ember.ui.hearth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.waiphy0aung.ember.R
import io.github.waiphy0aung.ember.ui.theme.EmberTheme

/**
 * The hearth, Ember's home screen. In build 0.1 it only shows the empty state
 * and the installed version, to prove the path from code to phone works.
 */
@Composable
fun HearthScreen(
    versionName: String,
    modifier: Modifier = Modifier,
) {
    val colors = EmberTheme.colors
    val spacing = EmberTheme.spacing
    val type = EmberTheme.type
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.night)
            .safeDrawingPadding()
            .padding(horizontal = spacing.l, vertical = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = stringResource(R.string.hearth_title),
            style = type.display.copy(color = colors.text),
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(Modifier.weight(1f))
        UnlitLogs(Modifier.size(EmberTheme.sizes.unlitLogs))
        Spacer(Modifier.height(spacing.l))
        BasicText(
            text = stringResource(R.string.hearth_empty_title),
            style = type.title.copy(color = colors.text),
        )
        Spacer(Modifier.height(spacing.s))
        BasicText(
            text = stringResource(R.string.hearth_empty_body),
            style = type.body.copy(color = colors.textQuiet, textAlign = TextAlign.Center),
        )
        Spacer(Modifier.weight(1f))
        BasicText(
            text = stringResource(R.string.hearth_version, versionName),
            style = type.caption.copy(color = colors.textQuiet),
        )
    }
}

/** Share of the drawing's width each log covers, leaving room for the tilt. */
private const val LOG_LENGTH_FRACTION = 0.86f

/** Share of the drawing's height each log covers. */
private const val LOG_THICKNESS_FRACTION = 0.24f

/** How far each log tilts from flat, in degrees; the two tilt opposite ways to cross. */
private const val LOG_TILT_DEGREES = 10f

/** Two crossed, unlit logs: the hearth with no flames yet. */
@Composable
private fun UnlitLogs(modifier: Modifier = Modifier) {
    val colors = EmberTheme.colors
    Canvas(modifier) {
        val logSize = Size(
            width = size.width * LOG_LENGTH_FRACTION,
            height = size.height * LOG_THICKNESS_FRACTION,
        )
        val topLeft = Offset(
            x = (size.width - logSize.width) / 2f,
            y = (size.height - logSize.height) / 2f,
        )
        val corner = CornerRadius(logSize.height / 2f)
        rotate(degrees = -LOG_TILT_DEGREES) {
            drawRoundRect(color = colors.log, topLeft = topLeft, size = logSize, cornerRadius = corner)
        }
        rotate(degrees = LOG_TILT_DEGREES) {
            drawRoundRect(color = colors.logLight, topLeft = topLeft, size = logSize, cornerRadius = corner)
        }
    }
}

@Preview(widthDp = 390, heightDp = 844)
@Composable
private fun HearthScreenPreview() {
    EmberTheme {
        HearthScreen(versionName = "0.1.0")
    }
}

@Preview
@Composable
private fun UnlitLogsPreview() {
    EmberTheme {
        UnlitLogs(
            Modifier
                .background(EmberTheme.colors.night)
                .size(EmberTheme.sizes.unlitLogs),
        )
    }
}
