// Every plugin used by any module is declared here once, so all modules share one classpath.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose) apply false
}
