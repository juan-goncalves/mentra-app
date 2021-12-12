import org.gradle.api.tasks.testing.logging.TestLogEvent.*

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://storage.googleapis.com/r8-releases/raw")
    }

    dependencies {
        classpath(Deps.r8)
        classpath(Deps.android_gradle)
        classpath(Deps.kotlin_gradle_plugin)
        classpath(Deps.dagger_hilt_gradle_plugin)
        classpath(Deps.google_oss_licenses_gradle_plugin)
        classpath(Deps.google_services)
        classpath(Deps.firebase_crashlytics_gradle_plugin)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        maven("https://storage.googleapis.com/r8-releases/raw")
    }
}

tasks.create<Delete>("clean") {
    delete(rootProject.buildDir)
}

subprojects {
    tasks.withType<Test> {
        testLogging {
            events = setOf(PASSED, SKIPPED, FAILED)
        }
    }
}