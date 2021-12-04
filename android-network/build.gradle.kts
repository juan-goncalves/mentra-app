import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    defaultConfig {
        minSdk = 26
        targetSdk = 31
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("debug") {
            buildConfigField(
                "String",
                "CurrencyLayerApiKey",
                keystoreProperties.getProperty("currencyLayerApiKey"),
            )
        }
        getByName("release") {
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )

            buildConfigField(
                "String",
                "CurrencyLayerApiKey",
                keystoreProperties.getProperty("currencyLayerApiKey"),
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":domain-layer"))
    implementation(project(":data-layer"))

    implementation(Deps.kotlin_stdlib)
    implementation(Deps.dagger_hilt)

    api(Deps.retrofit)
    api(Deps.moshi)
    api(Deps.okhttp3_logging_interceptor)

    kapt(Deps.dagger_hilt_compiler)

    testImplementation(project(":test-utils"))
    testImplementation(Deps.junit)
    testImplementation(Deps.hamcrest)
    testImplementation(Deps.mockk)
    testImplementation(Deps.androidx_core_testing)
    testImplementation(Deps.coroutines_test)
}