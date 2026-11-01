package io.github.waiphy0aung.ember

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.waiphy0aung.ember.ui.hearth.HearthScreen
import io.github.waiphy0aung.ember.ui.theme.EmberTheme

/** The only activity: it hosts every Ember screen. */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            EmberTheme {
                HearthScreen(versionName = BuildConfig.VERSION_NAME)
            }
        }
    }
}
