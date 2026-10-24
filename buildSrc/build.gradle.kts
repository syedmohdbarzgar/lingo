// The content gate (AGENTS.md §5) lives here so ONE Kotlin implementation serves
// both consumers: the `validateContent` Gradle task (which must run before
// `preBuild`, so it can never depend on compiling :app — that would be a task
// cycle) and `ContentValidationTest` on the unit-test classpath.
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
}

repositories {
    // buildSrc is its own build: the root's FAIL_ON_PROJECT_REPOS does not apply
    // here, and the validator needs the real org.json (android.jar only has stubs).
    mavenCentral()
}

// Match the app's Java 11 line: on JDK 25 the Java task would target 25 while
// Kotlin's highest target is 24, and Gradle fails the build on that mismatch.
java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_11)
    }
}

dependencies {
    implementation(libs.org.json)
}
