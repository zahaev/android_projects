import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

val baseUrl = localProperties.getProperty(
    "rickandmorty.base_url",
    "https://rickandmortyapi.com/api/"
)

android {
    namespace = "com.example.myapplication"

    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"

        minSdk = 26
        targetSdk = 34

        versionCode = providers
            .gradleProperty("APP_VERSION_CODE")
            .get()
            .toInt()

        versionName = providers
            .gradleProperty("APP_VERSION_NAME")
            .get()

        buildConfigField(
            "String",
            "BASE_URL",
            "\"$baseUrl\""
        )
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    sourceSets {
        named("main") {
            java {
                setSrcDirs(
                    files("src/main/java").filterNot {
                        it.absolutePath.contains(
                            "CharacterDetailActivity.kt"
                        ) ||
                                it.absolutePath.contains(
                                    "CharacterAdapter.kt"
                                )
                    }
                )
            }
        }
    }
}

dependencies {

    // ============================================================
    // ANDROIDX
    // ============================================================

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.material)

    // ============================================================
    // JETPACK COMPOSE
    // ============================================================

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.androidx.compose.runtime.livedata)


    // ============================================================
    // NAVIGATION
    // ============================================================

    implementation(libs.androidx.navigation.compose)


    // ============================================================
    // LIFECYCLE / VIEWMODEL
    // ============================================================

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)


    // ============================================================
    // HILT
    // ============================================================

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.hilt.navigation.compose)


    // ============================================================
    // COROUTINES
    // ============================================================

    implementation(libs.kotlinx.coroutines.android)


    // ============================================================
    // RETROFIT / MOSHI
    // ============================================================

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.moshi.kotlin)


    // ============================================================
    // ROOM
    // ============================================================

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    kapt(libs.androidx.room.compiler)


    // ============================================================
    // COIL
    // ============================================================

    implementation(libs.coil.compose)


    // ============================================================
    // TESTING
    // ============================================================

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.androidx.test.espresso.core)
}