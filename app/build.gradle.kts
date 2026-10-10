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
    // Copied into a *local* on purpose: a task action that reads a script member
    // (a top-level `val` is a member of the generated script class) cannot be
    // serialized by the configuration cache — the build would fail with
    // "cannot serialize Gradle script object references".
    val rsaKeyMissing = bazaarRsaKeyMissing
    doFirst {
        if (rsaKeyMissing) {
            throw GradleException(
                "bazaarRsaKey is missing from keystore.properties — Cafe Bazaar release builds " +
                    "require it (get it from developers.cafebazaar.ir).",
            )
        }
    }
}

// The free-access companion grant (checklist N-1) is only trustworthy when its
// signing digest is configured: with an empty digest set the app accepts any APK
// that merely declares `org.token.zaribar`, which is exactly the paywall bypass.
// The digest is public (a certificate hash, not a secret), so it lives in the
// Kotlin source and this gate reads it from there — release builds fail closed
// until the real Zaribar key is pasted in, the same stance as bazaarRsaKey above.
// Debug builds keep the runtime warning instead (AGENTS.md §4a / §10).
val companionSourceFile = file("src/main/java/org/token/english/core/billing/CompanionApp.kt")
tasks.matching {
    (it.name.startsWith("assemble") || it.name.startsWith("bundle")) && it.name.contains("Release")
}.configureEach {
    // Same local-copy rule as the bazaar gate above; the file is read at execution
    // time so pasting the digest takes effect without a clean build.
    val sourceFile = companionSourceFile
    doFirst {
        // Anchored to the *declaration* line: the constant is also named in the
        // object's KDoc, and `[^=]*` would happily run across it to the next `=`
        // anywhere in the file (`const val PACKAGE = …`). No match = fail closed.
        val digest = Regex("""val EXPECTED_SIGNING_SHA256[^\n]*?=\s*([^\n]*)""")
            .find(sourceFile.readText())
            ?.groupValues?.get(1)?.trim()
            .orEmpty()
        if (digest.isEmpty() || digest.startsWith("emptySet")) {
            throw GradleException(
                "CompanionApp.EXPECTED_SIGNING_SHA256 is empty — a release must not ship a free " +
                    "subscription that trusts the companion's package id alone (any APK declaring " +
                    "org.token.zaribar would unlock the paid tier). Paste the Zaribar release " +
                    "certificate digest from `keytool -printcert -jarfile zaribar.apk`.",
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
