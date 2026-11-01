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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.night)
            .safeDrawingPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BasicText(
            text = stringResource(R.string.hearth_title),
            style = TextStyle(color = colors.text, fontSize = 28.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.align(Alignment.Start),
        )
        Spacer(Modifier.weight(1f))
        UnlitLogs(Modifier.size(width = 180.dp, height = 72.dp))
        Spacer(Modifier.height(24.dp))
        BasicText(
            text = stringResource(R.string.hearth_empty_title),
            style = TextStyle(color = colors.text, fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
        )
        Spacer(Modifier.height(8.dp))
        BasicText(
            text = stringResource(R.string.hearth_empty_body),
            style = TextStyle(color = colors.textQuiet, fontSize = 15.sp, textAlign = TextAlign.Center),
        )
        Spacer(Modifier.weight(1f))
        BasicText(
            text = stringResource(R.string.hearth_version, versionName),
            style = TextStyle(color = colors.textQuiet, fontSize = 13.sp),
        )
    }
}

/** Two crossed, unlit logs: the hearth with no flames yet. */
@Composable
private fun UnlitLogs(modifier: Modifier = Modifier) {
    val colors = EmberTheme.colors
    Canvas(modifier) {
        val logSize = Size(width = size.width * 0.86f, height = size.height * 0.24f)
        val topLeft = Offset(
            x = (size.width - logSize.width) / 2f,
            y = (size.height - logSize.height) / 2f,
        )
        val corner = CornerRadius(logSize.height / 2f)
        rotate(degrees = -10f) {
            drawRoundRect(color = colors.log, topLeft = topLeft, size = logSize, cornerRadius = corner)
        }
        rotate(degrees = 10f) {
            drawRoundRect(color = colors.logLight, topLeft = topLeft, size = logSize, cornerRadius = corner)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF120D0B, widthDp = 390, heightDp = 844)
@Composable
private fun HearthScreenPreview() {
    EmberTheme {
        HearthScreen(versionName = "0.1.0")
    }
}
