import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.ksp)
    alias(libs.plugins.compose.compiler)
}

// Room schema export (app/schemas) — required to write tested migrations.
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

// Optional release signing: create keystore.properties (see AGENTS.md) before publishing.
val keystoreProps = Properties().apply {
    val f = rootProject.file("keystore.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}
fun keystoreProp(name: String): String = keystoreProps.getProperty(name, "")

android {
    namespace = "org.token.english"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "org.token.english"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // One build per marketplace; each embeds its own billing SDK (AGENTS.md §4).
    flavorDimensions += "store"
    productFlavors {
        create("bazaar") {
            dimension = "store"
            buildConfigField("String", "STORE", "\"bazaar\"")
            buildConfigField("String", "STORE_NAME", "\"کافه بازار\"")
            // Public RSA key from developers.cafebazaar.ir (required before release!)
            buildConfigField("String", "BAZAAR_RSA_KEY", "\"${keystoreProp("bazaarRsaKey")}\"")
        }
        create("myket") {
            dimension = "store"
            buildConfigField("String", "STORE", "\"myket\"")
            buildConfigField("String", "STORE_NAME", "\"مایکت\"")
            // Public key from developer.myket.ir (optional but recommended)
            buildConfigField("String", "MYKET_PUBLIC_KEY", "\"${keystoreProp("myketPublicKey")}\"")
            // Required by myket-billing-client's bundled manifest (official sample values)
            manifestPlaceholders["marketApplicationId"] = "ir.mservices.market"
            manifestPlaceholders["marketBindAddress"] = "ir.mservices.market.InAppBillingService.BIND"
            manifestPlaceholders["marketPermission"] = "ir.mservices.market.BILLING"
        }
        create("googlePlay") {
            dimension = "store"
            buildConfigField("String", "STORE", "\"googlePlay\"")
            buildConfigField("String", "STORE_NAME", "\"Google Play\"")
        }
    }

    signingConfigs {
        if (keystoreProp("storeFile").isNotEmpty()) {
            create("release") {
                storeFile = rootProject.file(keystoreProp("storeFile"))
                storePassword = keystoreProp("storePassword")
                keyAlias = keystoreProp("keyAlias")
                keyPassword = keystoreProp("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            // R8 code + resource optimization (AGP 9.3 optimization DSL).
            // Keep rules live in src/main/keepRules/*.keep; the default Android
            // rules (proguard-android-optimize.txt equivalent) come built in.
            optimization {
                enable = true
            }
            signingConfig = signingConfigs.findByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    // JVM unit tests: android.jar stubs are not mocked, and a ViewModel legitimately
    // reads android.os.SystemClock — let the stubs return defaults instead of
    // throwing "not mocked" (checklist B-6).
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

// Cafe Bazaar releases must carry the RSA purchase-verification key; without it
// Poolakey silently disables signature checks (checklist P3). Fail the release
// build instead of shipping an unverifiable build. Debug builds still fall back.
val bazaarRsaKeyMissing = keystoreProp("bazaarRsaKey").isBlank()
tasks.matching { it.name == "assembleBazaarRelease" || it.name == "bundleBazaarRelease" }.configureEach {
    doFirst {
        if (bazaarRsaKeyMissing) {
            throw GradleException(
                "bazaarRsaKey is missing from keystore.properties — Cafe Bazaar release builds " +
                    "require it (get it from developers.cafebazaar.ir).",
            )
        }
    }
}

// The content gate (`validateContent`) and its preBuild hook live in their own
// script — 300 lines of JSON rules that have nothing to do with packaging the
// APK (checklist B-6).
apply(from = rootProject.file("gradle/content-validation.gradle.kts"))

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
    // System splash compatibility: blank the default splash and hand off to the
    // custom SplashActivity (see themes.xml / SplashActivity.kt).
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose UI
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Local persistence (offline-first)
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    // Per-store billing — only the selected marketplace's SDK is packaged
    add("googlePlayImplementation", libs.androidx.billing)
    add("bazaarImplementation", libs.poolakey)
    add("myketImplementation", libs.myket.billing)

    testImplementation(libs.junit)
    // Real org.json on the unit-test classpath (android.jar only has stubs).
    testImplementation(libs.org.json)
    // ViewModel tests drive viewModelScope through a test Main dispatcher (B-6).
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
