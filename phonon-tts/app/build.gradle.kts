plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.phonontts"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.phonontts"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // MediaPipe Tasks Audio ships arm64-v8a natives and Phonon is a CPU model for
        // 64-bit devices; building only that ABI keeps the APK honest (an x86 emulator
        // cannot run it, which is why T7 asks for a physical arm64 device).
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    // The 87 MB .litertlm bundle is memory-mapped, not read: compressing it would make
    // every launch unpack it into the cache first.
    androidResources {
        noCompress += listOf("litertlm")
    }

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("com.google.android.material:material:1.13.0")
    // MediaPipe Text-to-Speech task: com.google.mediapipe.tasks.audio.texttospeech.*
    implementation("com.google.mediapipe:tasks-audio:1.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // On-device check of the model and the task API (T7): the emulator cannot run
    // these natives, so the test is the device evidence for synthesis and playback.
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
}
