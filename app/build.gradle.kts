import java.io.FileInputStream
import java.util.*

plugins {
    id("com.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(FileInputStream(keystorePropertiesFile))
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"
    testOptions.unitTests.isIncludeAndroidResources = true

    defaultConfig {
        applicationId = "me.juangoncalves.mentra"
        minSdk = 26
        targetSdk = 31
        versionCode = 3
        versionName = "1.1.1"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = keystoreProperties.getProperty("storeFile")?.let { File(it) }
            storePassword = keystoreProperties.getProperty("storePassword")
        }
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            manifestPlaceholders["crashlyticsCollectionEnabled"] = false
            resValue("string", "app_name", "Mentra Debug")
        }
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            manifestPlaceholders["crashlyticsCollectionEnabled"] = true
            resValue("string", "app_name", "Mentra")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
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

    buildFeatures {
        viewBinding = true
    }

    kapt {
        correctErrorTypes = true
    }
}


dependencies {
    implementation(project(":domain-layer"))
    implementation(project(":data-layer"))
    implementation(project(":android-cache"))
    implementation(project(":android-network"))
    implementation(project(":pie-chart-view"))

    implementation(Deps.kotlin_stdlib)
    implementation(Deps.android_coroutines)

    implementation(Deps.androidx_core_ktx)
    implementation(Deps.androidx_appcompat)
    implementation(Deps.androidx_activity_ktx)
    implementation(Deps.androidx_fragment_ktx)
    implementation(Deps.androidx_preference_ktx)
    implementation(Deps.androidx_constraint_layout)
    implementation(Deps.androidx_swipe_refresh_layout)
    implementation(Deps.androidx_viewpager2)
    implementation(Deps.androidx_work_ktx)
    implementation(Deps.androidx_lifecycle_viewmodel_ktx)
    implementation(Deps.androidx_lifecycle_saved_state)
    implementation(Deps.androidx_lifecycle_livedata_ktx)
    implementation(Deps.google_material)
    implementation(Deps.google_oss_licenses)

    implementation(Deps.dagger_hilt)
    implementation(Deps.androidx_hilt_work)

    implementation(Deps.coil)
    implementation(Deps.coil_svg)
    implementation(Deps.fading_edge_layout)
    implementation(Deps.mp_android_chart)

    implementation(platform(Deps.firebase_platform))
    implementation(Deps.firebase_analytics)
    implementation(Deps.firebase_crashlytics)

    kapt(Deps.dagger_hilt_compiler)
    kapt(Deps.androidx_hilt_compiler)
    kapt(Deps.androidx_lifecycle_compiler)

    kaptTest(Deps.dagger_hilt_compiler)

    testImplementation(project(":test-utils"))
    testImplementation(Deps.junit)
    testImplementation(Deps.hamcrest)
    testImplementation(Deps.mockk)
    testImplementation(Deps.androidx_core_testing)
    testImplementation(Deps.dagger_hilt_android_testing)
    testImplementation(Deps.robolectric)
    testImplementation(Deps.coroutines_test)

    androidTestImplementation(Deps.androidx_junit)
    androidTestImplementation(Deps.espresso)
    androidTestImplementation(Deps.androidx_work_testing)
}