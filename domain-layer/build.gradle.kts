plugins {
    id("java-library")
    id("kotlin")
    id("kotlin-kapt")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation(Deps.kotlin_stdlib)
    api(Deps.coroutines)
    api(Deps.either)
    api(Deps.dagger)
    api(Deps.kotlinx_datetime)

    kapt(Deps.dagger_compiler)

    testImplementation(project(":test-utils"))
    testImplementation(Deps.junit)
    testImplementation(Deps.hamcrest)
    testImplementation(Deps.mockk)
    testImplementation(Deps.coroutines_test)
}