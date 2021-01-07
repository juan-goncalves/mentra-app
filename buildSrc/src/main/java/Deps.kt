// @formatter:off

object Versions {
    // Build config
    const val min_sdk = 26
    const val target_sdk = 29
    const val compile_sdk = 29

    // AndroidX
    const val androidx_core = "1.5.0-alpha01"
    const val androidx_appcompat = "1.3.0-alpha01"
    const val androidx_activity = "1.1.0"
    const val androidx_fragment = "1.3.0-alpha08"
    const val androidx_preference = "1.1.1"
    const val androidx_constraint_layout = "2.0.0-rc1"
    const val androidx_swipe_refresh_layout = "1.1.0"
    const val androidx_lifecycle = "2.3.0-alpha05"
    const val androidx_viewpager2 = "1.0.0"

    // Dagger Hilt
    const val dagger_hilt = "2.28-alpha"
    const val androidx_hilt_work = "1.0.0-alpha01"
    const val androidx_hilt_lifecycle_viewmodel = "1.0.0-alpha01"

}

@Suppress("unused")
object Deps {
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

    // Dagger Hilt
    const val dagger_hilt_gradle_plugin = "com.google.dagger:hilt-android-gradle-plugin:${Versions.dagger_hilt}"
    const val dagger_hilt = "com.google.dagger:hilt-android:${Versions.dagger_hilt}"
    const val dagger_hilt_androidx_compiler = "androidx.hilt:hilt-compiler:${Versions.dagger_hilt}"
    const val dagger_hilt_compiler = "com.google.dagger:hilt-android-compiler:${Versions.dagger_hilt}"
    const val androidx_hilt_work = "androidx.hilt:hilt-work:${Versions.androidx_hilt_work}"
    const val androidx_hilt_lifecycle_viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:${Versions.androidx_hilt_lifecycle_viewmodel}"

}

// @formatter:on