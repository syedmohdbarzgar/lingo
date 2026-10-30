import java.util.Properties
import org.token.english.content.ValidateContentTask

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
            // The app's own store page, used by Settings → «ارتباط با ناشر».
            // One value per flavor so the intent is the store's own documented one
            // instead of a guessed scheme: Cafe Bazaar's official intent guide gives
            // `bazaar://details?id=…` (and `setPackage("com.farsitel.bazaar")`),
            // with the public page at `cafebazaar.ir/app/<package>`.
            buildConfigField("String", "STORE_DEEP_LINK", "\"bazaar://details?id=\"")
            buildConfigField("String", "STORE_WEB_BASE", "\"https://cafebazaar.ir/app/\"")
            buildConfigField("String", "STORE_PACKAGE", "\"com.farsitel.bazaar\"")
            // The free-route offer may open Cafe Bazaar from this build (N-4).
            buildConfigField("boolean", "EXTERNAL_COMPANION_OFFER", "true")
            // Public RSA key from developers.cafebazaar.ir (required before release!)
            buildConfigField("String", "BAZAAR_RSA_KEY", "\"${keystoreProp("bazaarRsaKey")}\"")
        }
        create("myket") {
            dimension = "store"
            buildConfigField("String", "STORE", "\"myket\"")
            buildConfigField("String", "STORE_NAME", "\"مایکت\"")
            // Myket documents its own scheme for opening an app page
            // (`myket://details?id=…`, myket.ir knowledge base) with the public page
            // at `myket.ir/app/<package>`. STORE_PACKAGE stays EMPTY on purpose:
            // pinning this intent to `ir.mservices.market` is not documented (that
            // package id is documented for the billing service only), and the
            // `myket://` scheme already resolves to Myket alone — pinning would only
            // risk sending the user nowhere on a device where Myket is absent.
            buildConfigField("String", "STORE_DEEP_LINK", "\"myket://details?id=\"")
            buildConfigField("String", "STORE_WEB_BASE", "\"https://myket.ir/app/\"")
            buildConfigField("String", "STORE_PACKAGE", "\"\"")
            // The free-route offer may open Cafe Bazaar from this build (N-4).
            buildConfigField("boolean", "EXTERNAL_COMPANION_OFFER", "true")
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
            // Play is the exception: Google's "Linking to Google Play" guide documents
            // the HTTPS listing URL plus `setPackage("com.android.vending")` to open it
            // in the Play app — the `market://` details form is not what that guide
            // documents, so it is not used here. The same URL is both the deep link
            // and the browser fallback.
            buildConfigField(
                "String",
                "STORE_DEEP_LINK",
                "\"https://play.google.com/store/apps/details?id=\"",
            )
            buildConfigField(
                "String",
                "STORE_WEB_BASE",
                "\"https://play.google.com/store/apps/details?id=\"",
            )
            buildConfigField("String", "STORE_PACKAGE", "\"com.android.vending\"")
            // N-4 (decided v14): the free-route offer opens Cafe Bazaar in every
            // flavor while `googlePlay` is not published on Play. BEFORE THE FIRST
            // PLAY RELEASE flip this to false — unless the external-content-links
            // program is complied with. `PlayStoreLinkGateTest` proves every screen
            // and `CompanionApp.openDownloadPage` honor the flag, and
            // `docs/release.md` carries the re-decision point.
            buildConfigField("boolean", "EXTERNAL_COMPANION_OFFER", "true")
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
        // Compose screen smoke tests on the host JVM (checklist v15 B-6): Robolectric
        // needs the merged resources/manifest of the variant under test.
        unitTests.isIncludeAndroidResources = true
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

// The offline speech model is the one release input that is fetched, not committed:
// ~40 MB of Vosk assets (`scripts/fetch_vosk_model.sh`, git-ignored). A release built
// from a clone that skipped the fetch would install and run, then fail at the first
// take with «مدل گفتار پیدا نشد» — a dead feature that no compiler can catch. Fail
// the release build instead, on the same three markers `scripts/release_check.sh`
// trusts: the `uuid` marker (written last, after the whole tree landed), `am/` and
// `graph/` (a recogniser cannot load without either).
val voskModelDir = file("src/main/assets/vosk/vosk-model-small-en-us-0.15")
tasks.matching {
    (it.name.startsWith("assemble") || it.name.startsWith("bundle")) && it.name.contains("Release")
}.configureEach {
    // Local copy, same reason as the two gates above: a script-level `val` cannot be
    // serialized by the configuration cache.
    val modelDir = voskModelDir
    doFirst {
        val complete = File(modelDir, "uuid").exists() &&
            File(modelDir, "am").isDirectory &&
            File(modelDir, "graph").isDirectory
        if (!complete) {
            throw GradleException(
                "The offline speech model is missing or incomplete at ${modelDir.path} — a " +
                    "release would ship conversation practice that can only say «مدل گفتار پیدا " +
                    "نشد». Run `bash scripts/fetch_vosk_model.sh` before building a release.",
            )
        }
    }
}

// The content gate (`validateContent`, wired into preBuild) — the rules live in
// buildSrc's ContentValidator, ONE Kotlin implementation shared with
// ContentValidationTest (see that test for why buildSrc: compiling :app runs
// preBuild, so a gate that needed :app compilation would cycle).
val validateContent = tasks.register<ValidateContentTask>("validateContent") {
    group = "verification"
    description = "Validates the bundled content JSON in src/main/assets/content."
    contentDir.set(layout.projectDirectory.dir("src/main/assets/content"))
}
// Gate every build on content validity: a dangling lessonId or an out-of-range
// correctIndex is a data bug the Kotlin compiler cannot see, and it would ship
// silently. Costs a few seconds, so it is always worth it.
tasks.named("preBuild") {
    dependsOn(validateContent)
}

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

    // Conversation practice: fully offline speech recognition (Vosk runs on-device
    // from a model bundled in assets — no network, no cloud recognizer; see
    // docs/connectivity.md). JNA is a hard requirement of vosk-android and must be
    // the @aar artifact, or the native library loader fails at runtime. The `@aar`
    // suffix is written here because version catalogs cannot carry a packaging
    // classifier (declared as `libs.jna` it resolves to the jar and breaks).
    implementation(libs.vosk.android)
    implementation("net.java.dev.jna:jna:${libs.versions.jna.get()}@aar")

    // Per-store billing — only the selected marketplace's SDK is packaged
    add("googlePlayImplementation", libs.androidx.billing)
    add("bazaarImplementation", libs.poolakey)
    add("myketImplementation", libs.myket.billing)

    testImplementation(libs.junit)
    // Real org.json on the unit-test classpath (android.jar only has stubs).
    testImplementation(libs.org.json)
    // The shared content gate (buildSrc): ContentValidationTest runs the exact
    // rules `validateContent` enforces. buildSrc is fully built before this
    // script is evaluated, so its jar is already on disk — no task cycle.
    testImplementation(files(rootProject.file("buildSrc/build/libs/buildSrc.jar")))
    // ViewModel tests drive viewModelScope through a test Main dispatcher (B-6).
    testImplementation(libs.kotlinx.coroutines.test)
    // Screen smoke tests (checklist v15 B-6): one Compose assertion per main screen,
    // running on the host JVM via Robolectric — no emulator exists here, and a
    // dead androidTest suite would be a false signal. ui-test-manifest supplies the
    // host activity `createComposeRule` launches.
    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    testImplementation(libs.robolectric)
    // A real SQLite engine on the host JVM: the only way to execute the Room
    // migration chain with actual learner rows in a unit test (no device/emulator
    // is available here). See DatabaseUpgradeTest.
    testImplementation(libs.sqlite.jdbc)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
