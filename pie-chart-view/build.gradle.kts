plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdk = 31
    buildToolsVersion = "31.0.0"

    defaultConfig {
        minSdk = 23
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
}

dependencies {
    implementation(fileTree("libs") { arrayOf("*.jar") })

    implementation(Deps.kotlin_stdlib)
    implementation(Deps.androidx_core_ktx)
    implementation(Deps.androidx_appcompat)
    implementation(Deps.androidx_constraint_layout)

    testImplementation(Deps.junit)

    androidTestImplementation(Deps.androidx_junit)
    androidTestImplementation(Deps.espresso)
}