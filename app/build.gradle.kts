plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
}

// CI passes these; local builds fall back to safe defaults.
val versionCodeFromCi = providers.environmentVariable("EMBER_VERSION_CODE").map(String::toInt).orElse(1)
val versionNameFromCi = providers.environmentVariable("EMBER_VERSION_NAME").orElse("0.1.0-local")
val keystorePath = providers.environmentVariable("EMBER_KEYSTORE_PATH")

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

    signingConfigs {
        create("ember") {
            // The release key lives only in GitHub Actions secrets and your offline backup.
            if (keystorePath.isPresent) {
                storeFile = file(keystorePath.get())
                storePassword = providers.environmentVariable("EMBER_KEYSTORE_PASSWORD").get()
                keyAlias = providers.environmentVariable("EMBER_KEY_ALIAS").get()
                keyPassword = providers.environmentVariable("EMBER_KEY_PASSWORD").get()
            }
        }
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
            signingConfig = if (keystorePath.isPresent) {
                signingConfigs.getByName("ember")
            } else {
                signingConfigs.getByName("debug")
            }
        }
        // Ember Preview: same as release, installed beside it with its own separate data.
        // Built unsigned from pull requests; publish-preview.yml signs it with the preview-only key.
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

    testImplementation(libs.junit4)
}
