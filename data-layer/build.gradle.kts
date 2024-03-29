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
    implementation(project(":domain-layer"))

    implementation(Deps.kotlin_stdlib)

    testImplementation(project(":test-utils"))
    testImplementation(Deps.junit)
    testImplementation(Deps.hamcrest)
    testImplementation(Deps.mockk)
    testImplementation(Deps.coroutines_test)
}