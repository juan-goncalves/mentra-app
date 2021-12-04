plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation(Deps.kotlin_stdlib)
    implementation(Deps.coroutines)
    implementation(Deps.coroutines_test)
    implementation(Deps.junit)
    implementation(Deps.hamcrest)
    implementation(Deps.mockk)
}