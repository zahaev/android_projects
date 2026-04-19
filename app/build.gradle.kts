plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)

    id("kotlin-kapt") // обязательно для Room

}

android {
    namespace = "com.example.myapplication"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.myapplication"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        // Чтение BASE_URL из local.properties с фоллбэком на дефолт
        val baseUrl = project.providers.gradleProperty("rickandmorty.base_url")
            .orElse("https://rickandmortyapi.com/api/")
            .get()

        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.livedata)

    implementation(libs.androidx.cardview)
        //БД
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Glide через version catalog
    implementation(libs.glide)

    //пагинация
    implementation("androidx.paging:paging-runtime:3.3.2")
    //ниже не трогать

// Coroutines for Room (рекомендуется для асинхронной работы)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

// JSON converter (Moshi)
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")

// Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

// OkHttp logging (для отладки запросов)
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")


}
configurations.all {
    exclude(group = "com.intellij", module = "annotations")
}
