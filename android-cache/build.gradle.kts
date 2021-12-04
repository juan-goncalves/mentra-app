plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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
        getByName("release") {
            isMinifyEnabled = false

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

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(project(":domain-layer"))
    implementation(project(":data-layer"))

    implementation(Deps.kotlin_stdlib)
    implementation(Deps.androidx_preference_ktx)
    implementation(Deps.dagger_hilt)

    api(Deps.androidx_room_runtime)
    api(Deps.androidx_room_ktx)

    kapt(Deps.dagger_hilt_compiler)
    kapt(Deps.androidx_room_compiler)

    testImplementation(project(":test-utils"))
    testImplementation(Deps.junit)
    testImplementation(Deps.hamcrest)
    testImplementation(Deps.mockk)
    testImplementation(Deps.androidx_core_testing)
    testImplementation(Deps.dagger_hilt_android_testing)
    testImplementation(Deps.androidx_room_testing)
    testImplementation(Deps.robolectric)
    testImplementation(Deps.coroutines_test)
}