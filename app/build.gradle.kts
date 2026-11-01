plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
}

// CI passes these; local builds fall back to safe defaults.
val versionCodeFromCi = providers.environmentVariable("EMBER_VERSION_CODE").map(String::toInt).orElse(1)
val versionNameFromCi = providers.environmentVariable("EMBER_VERSION_NAME").orElse("0.1.0-local")

android {
    namespace = "io.github.waiphy0aung.ember"
    compileSdk = 36

    defaultConfig {
        applicationId = "io.github.waiphy0aung.ember"
        minSdk = 26
        targetSdk = 36
        versionCode = versionCodeFromCi.get()
        versionName = versionNameFromCi.get()
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Built unsigned. CI signs it with apksigner in a step where no Gradle code runs,
            // so no build plugin or library ever sees the signing key.
            signingConfig = null
        }
        // Ember Preview: same as release, installed beside it with its own separate data.
        // Signed by publish-preview.yml with the preview-only key, never the release key.
        create("preview") {
            initWith(getByName("release"))
            applicationIdSuffix = ".preview"
            versionNameSuffix = "-preview"
            matchingFallbacks += "release"
            signingConfig = null
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = true
        warningsAsErrors = true
        lintConfig = file("lint.xml")
    }
}

kotlin {
    compilerOptions {
        allWarningsAsErrors.set(true)
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
