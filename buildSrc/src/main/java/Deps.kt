// @formatter:off

object Versions {
    const val kotlin_stdlib = "1.6.0"
    const val coroutines = "1.5.2"
    const val android_gradle = "7.0.3"
    const val r8 = "3.1.42"

    const val androidx_core = "1.5.0-alpha01"
    const val androidx_appcompat = "1.3.0-alpha01"
    const val androidx_activity = "1.1.0"
    const val androidx_fragment = "1.3.0-alpha08"
    const val androidx_preference = "1.1.1"
    const val androidx_constraint_layout = "2.0.0-rc1"
    const val androidx_swipe_refresh_layout = "1.1.0"
    const val androidx_lifecycle = "2.4.0"
    const val androidx_viewpager2 = "1.0.0"
    const val androidx_work = "2.4.0"
    const val google_material = "1.2.1"
    const val androidx_core_testing = "2.1.0"
    const val androidx_room = "2.4.0-beta02"

    const val dagger = "2.28.3"
    const val dagger_hilt = "2.40.4"
    const val androidx_hilt = "1.0.0"

    const val firebase = "26.2.0"
    const val firebase_crashlytics_gradle_plugin = "2.4.1"
    const val google_services = "4.3.4"
    const val google_oss_licenses = "17.0.0"
    const val google_oss_licenses_gradle_plugin = "0.10.4"

    const val retrofit = "2.6.0"
    const val okhttp3_logging_interceptor = "4.2.1"

    const val coil = "1.4.0"
    const val fading_edge_layout = "1.0.0"
    const val mp_android_chart = "v3.1.0"
    const val either = "3.0.0"
    const val kotlinx_datetime = "0.3.1"

    const val junit = "4.13.1"
    const val hamcrest = "2.2"
    const val mockk = "1.12.1"
    const val robolectric = "4.7"
    const val androidx_junit = "1.1.1"
    const val espresso = "3.2.0"
}

@Suppress("unused")
object Deps {
    const val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin_stdlib}"
    const val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin_stdlib}"
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
    const val android_coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
    const val coroutines_test = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}"
    const val android_gradle =  "com.android.tools.build:gradle:${Versions.android_gradle}"
    const val r8 = "com.android.tools:r8:${Versions.r8}"

    const val androidx_core_ktx = "androidx.core:core-ktx:${Versions.androidx_core}"
    const val androidx_appcompat = "androidx.appcompat:appcompat:${Versions.androidx_appcompat}"
    const val androidx_activity_ktx = "androidx.activity:activity-ktx:${Versions.androidx_activity}"
    const val androidx_fragment_ktx = "androidx.fragment:fragment-ktx:${Versions.androidx_fragment}"
    const val androidx_preference_ktx = "androidx.preference:preference-ktx:${Versions.androidx_preference}"
    const val androidx_constraint_layout = "androidx.constraintlayout:constraintlayout:${Versions.androidx_constraint_layout}"
    const val androidx_swipe_refresh_layout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.androidx_swipe_refresh_layout}"
    const val androidx_viewpager2 = "androidx.viewpager2:viewpager2:${Versions.androidx_viewpager2}"
    const val androidx_lifecycle_viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.androidx_lifecycle}"
    const val androidx_lifecycle_saved_state = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.androidx_lifecycle}"
    const val androidx_lifecycle_livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.androidx_lifecycle}"
    const val androidx_lifecycle_compiler = "androidx.lifecycle:lifecycle-compiler:${Versions.androidx_lifecycle}"
    const val androidx_work_ktx = "androidx.work:work-runtime-ktx:${Versions.androidx_work}"
    const val androidx_work_testing = "androidx.work:work-testing:${Versions.androidx_work}"
    const val androidx_core_testing = "androidx.arch.core:core-testing:${Versions.androidx_core_testing}"
    const val google_material = "com.google.android.material:material:${Versions.google_material}"

    const val androidx_room_runtime = "androidx.room:room-runtime:${Versions.androidx_room}"
    const val androidx_room_ktx = "androidx.room:room-ktx:${Versions.androidx_room}"
    const val androidx_room_compiler = "androidx.room:room-compiler:${Versions.androidx_room}"
    const val androidx_room_testing = "androidx.room:room-testing:${Versions.androidx_room}"

    const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    const val dagger_compiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    const val dagger_hilt_gradle_plugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.dagger_hilt}"
    const val dagger_hilt = "com.google.dagger:hilt-android:${Versions.dagger_hilt}"
    const val dagger_hilt_compiler = "com.google.dagger:hilt-android-compiler:${Versions.dagger_hilt}"
    const val androidx_hilt_compiler = "androidx.hilt:hilt-compiler:${Versions.androidx_hilt}"
    const val androidx_hilt_work = "androidx.hilt:hilt-work:${Versions.androidx_hilt}"
    const val dagger_hilt_android_testing = "com.google.dagger:hilt-android-testing:${Versions.dagger_hilt}"

    const val firebase_platform = "com.google.firebase:firebase-bom:${Versions.firebase}"
    const val firebase_analytics = "com.google.firebase:firebase-analytics-ktx"
    const val firebase_crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
    const val firebase_crashlytics_gradle_plugin = "com.google.firebase:firebase-crashlytics-gradle:${Versions.firebase_crashlytics_gradle_plugin}"
    const val google_services = "com.google.gms:google-services:${Versions.google_services}"
    const val google_oss_licenses = "com.google.android.gms:play-services-oss-licenses:${Versions.google_oss_licenses}"
    const val google_oss_licenses_gradle_plugin = "com.google.android.gms:oss-licenses-plugin:${Versions.google_oss_licenses_gradle_plugin}"

    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    const val moshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
    const val okhttp3_logging_interceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp3_logging_interceptor}"

    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val coil_svg = "io.coil-kt:coil-svg:${Versions.coil}"
    const val fading_edge_layout = "com.github.bosphere.android-fadingedgelayout:fadingedgelayout:${Versions.fading_edge_layout}"
    const val mp_android_chart = "com.github.PhilJay:MPAndroidChart:${Versions.mp_android_chart}"
    const val either = "com.github.adelnizamutdinov:kotlin-either:${Versions.either}"
    const val kotlinx_datetime = "org.jetbrains.kotlinx:kotlinx-datetime:${Versions.kotlinx_datetime}"

    const val junit = "junit:junit:${Versions.junit}"
    const val hamcrest = "org.hamcrest:hamcrest-library:${Versions.hamcrest}"
    const val mockk = "io.mockk:mockk:${Versions.mockk}"
    const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    const val androidx_junit = "androidx.test.ext:junit:${Versions.androidx_junit}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
}

// @formatter:on