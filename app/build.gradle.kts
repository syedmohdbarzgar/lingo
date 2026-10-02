import groovy.json.JsonSlurper
import java.io.File
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

// ---------------------------------------------------------------------------
// Content validation — `./gradlew validateContent`
//
// The curriculum is plain JSON (AGENTS.md §5), so the Kotlin compiler cannot
// catch content mistakes. Without a gate these fail silently in production:
//   * a dangling `lessonId` orphans a lesson with no error at all,
//   * `correctIndex` outside the options array only throws while the learner
//     is mid-lesson,
//   * a duplicate id is dropped by Room (the id is the primary key), quietly
//     losing an exercise,
//   * a wrong-typed `type` is skipped by ContentParser, so the exercise just
//     never appears.
// The task collects every problem instead of stopping at the first one, and
// runs before a build so bad content never reaches a device.
// ---------------------------------------------------------------------------
tasks.register("validateContent") {
    group = "verification"
    description = "Validates the bundled content JSON in src/main/assets/content."
    val contentDir = layout.projectDirectory.dir("src/main/assets/content")
    inputs.dir(contentDir)

    doLast {
        val levels = setOf("A1", "A2", "B1", "B2", "C1", "C2")
        // Mirrors ContentParser.supportedTypes — `speaking` parses but is suspended.
        val exerciseTypes = setOf("multiple_choice", "fill_blank", "translation", "listening")
        val errors = mutableListOf<String>()
        val dir = contentDir.asFile

        fun report(where: String, what: String) {
            errors += "  - $where: $what"
        }

        fun load(name: String): Map<*, *>? {
            val file = File(dir, name)
            if (!file.exists()) {
                report(name, "file is missing")
                return null
            }
            return try {
                JsonSlurper().parse(file) as Map<*, *>
            } catch (e: Exception) {
                report(name, "not valid JSON (${e.message})")
                null
            }
        }

        fun items(root: Map<*, *>?, key: String, file: String): List<Map<*, *>> {
            val raw = root?.get(key)
            if (raw == null) {
                report(file, "missing top-level \"$key\" array")
                return emptyList()
            }
            val array = raw as? List<*>
            if (array == null) {
                report(file, "\"$key\" is not an array")
                return emptyList()
            }
            return array.mapIndexedNotNull { index, entry ->
                entry as? Map<*, *> ?: run {
                    report("$file/$key[$index]", "entry is not an object")
                    null
                }
            }
        }

        fun text(o: Map<*, *>, key: String): String = (o[key] as? String)?.trim().orEmpty()
        fun number(o: Map<*, *>, key: String): Int? = (o[key] as? Number)?.toInt()
        fun array(o: Map<*, *>, key: String): List<*> = o[key] as? List<*> ?: emptyList<Any?>()

        fun collectIds(rows: List<Map<*, *>>, where: String): Set<String> {
            val ids = mutableListOf<String>()
            rows.forEachIndexed { index, row ->
                val id = text(row, "id")
                if (id.isEmpty()) report("$where[$index]", "missing id") else ids += id
            }
            ids.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.sorted()
                .forEach { report(where, "duplicate id \"$it\"") }
            return ids.toSet()
        }

        fun checkChoiceAnswer(id: String, o: Map<*, *>) {
            val options = array(o, "options").map { it.toString() }
            if (options.size != 4) report(id, "expected 4 options, found ${options.size}")
            options.forEachIndexed { index, option ->
                if (option.isBlank()) report(id, "option $index is blank")
            }
            if (options.map { it.lowercase() }.distinct().size != options.size) {
                report(id, "options contain duplicates")
            }
            val correct = number(o, "correctIndex")
            when {
                correct == null -> report(id, "missing correctIndex")
                correct !in options.indices -> report(id, "correctIndex $correct outside 0..${options.size - 1}")
            }
        }

        fun checkExercise(id: String, type: String, o: Map<*, *>) {
            when (type) {
                "multiple_choice" -> {
                    if (text(o, "question").isEmpty()) report(id, "blank question")
                    checkChoiceAnswer(id, o)
                }
                "fill_blank" -> {
                    val sentence = text(o, "sentence")
                    if (sentence.isEmpty()) report(id, "blank sentence")
                    if (!sentence.contains("___")) report(id, "sentence has no ___ blank marker")
                    if (array(o, "accepted").isEmpty()) report(id, "no accepted answers")
                }
                "translation" -> {
                    if (text(o, "prompt").isEmpty()) report(id, "blank prompt")
                    if (array(o, "accepted").isEmpty()) report(id, "no accepted answers")
                }
                "listening" -> {
                    if (text(o, "text").isEmpty()) report(id, "blank text")
                    if (array(o, "accepted").isEmpty()) report(id, "no accepted answers")
                }
            }
        }

        val files = listOf("lessons.json", "vocabulary.json", "exercises.json", "placement.json", "knowledge.json")
        val roots = files.associateWith { load(it) }

        // --- bundle version -------------------------------------------------
        val versions = files.associateWith { file ->
            val root = roots[file]
            val version = root?.let { number(it, "contentVersion") }
            if (version == null) report(file, "missing top-level contentVersion")
            version ?: -1
        }
        if (versions.values.toSet().size > 1) {
            report("contentVersion", "files disagree — ${versions.entries.joinToString { "${it.key}=${it.value}" }}")
        }

        // --- lessons --------------------------------------------------------
        val lessons = items(roots["lessons.json"], "lessons", "lessons.json")
        val lessonIds = collectIds(lessons, "lessons")
        lessons.forEach { lesson ->
            val id = text(lesson, "id").ifEmpty { "<lesson without id>" }
            if (text(lesson, "level") !in levels) report(id, "invalid level \"${text(lesson, "level")}\"")
            listOf("title", "titleFa", "topic").forEach { key ->
                if (text(lesson, key).isEmpty()) report(id, "blank $key")
            }
            if ((number(lesson, "order") ?: 0) <= 0) report(id, "order must be a positive integer")
            if ((number(lesson, "estimatedMinutes") ?: 0) <= 0) report(id, "estimatedMinutes must be positive")
        }
        lessons.mapNotNull { number(it, "order") }.groupingBy { it }.eachCount()
            .filterValues { it > 1 }.keys.sorted()
            .forEach { report("lessons.json", "order $it is used by more than one lesson") }

        // --- vocabulary -----------------------------------------------------
        val vocabulary = items(roots["vocabulary.json"], "vocabulary", "vocabulary.json")
        collectIds(vocabulary, "vocabulary")
        val seenWords = mutableListOf<String>()
        vocabulary.forEachIndexed { index, word ->
            val id = text(word, "id").ifEmpty { "vocabulary[$index]" }
            if (text(word, "word").isEmpty()) report(id, "blank word")
            if (text(word, "translation").isEmpty()) report(id, "blank translation")
            if (text(word, "level") !in levels) report(id, "invalid level \"${text(word, "level")}\"")
            if (array(word, "examples").isEmpty()) report(id, "no examples")
            val lessonId = text(word, "lessonId")
            if (lessonId.isNotEmpty() && lessonId !in lessonIds) report(id, "lessonId \"$lessonId\" is not a lesson")
            seenWords += "${text(word, "word").lowercase()} @ $lessonId"
        }
        seenWords.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.sorted()
            .forEach { report("vocabulary.json", "duplicate word in the same lesson: \"$it\"") }

        // --- exercises ------------------------------------------------------
        val exercises = items(roots["exercises.json"], "exercises", "exercises.json")
        val exerciseIds = collectIds(exercises, "exercises")
        val ordersPerLesson = mutableMapOf<String, MutableList<Int>>()
        exercises.forEachIndexed { index, exercise ->
            val id = text(exercise, "id").ifEmpty { "exercises[$index]" }
            val lessonId = text(exercise, "lessonId")
            if (lessonId !in lessonIds) report(id, "lessonId \"$lessonId\" is not a lesson")
            val type = text(exercise, "type")
            if (type !in exerciseTypes) report(id, "unsupported type \"$type\"")
            val order = number(exercise, "order") ?: 0
            if (order <= 0) report(id, "order must be a positive integer")
            else ordersPerLesson.getOrPut(lessonId) { mutableListOf() } += order
            checkExercise(id, type, exercise)
        }
        ordersPerLesson.forEach { (lessonId, orders) ->
            orders.groupingBy { it }.eachCount().filterValues { it > 1 }.keys.sorted()
                .forEach { report(lessonId, "exercise order $it is used twice") }
        }

        // --- placement ------------------------------------------------------
        val placement = items(roots["placement.json"], "questions", "placement.json")
        collectIds(placement, "placement.questions")
        placement.forEachIndexed { index, question ->
            val id = text(question, "id").ifEmpty { "placement[$index]" }
            if (id in exerciseIds) report(id, "id already used by an exercise")
            if (text(question, "level") !in levels) report(id, "invalid level \"${text(question, "level")}\"")
            if (text(question, "question").isEmpty()) report(id, "blank question")
            checkChoiceAnswer(id, question)
        }

        // --- knowledge graph -------------------------------------------------
        val knowledge = items(roots["knowledge.json"], "knowledge", "knowledge.json")
        val knowledgeIds = collectIds(knowledge, "knowledge")
        val knowledgeTypes = setOf("GRAMMAR", "VOCABULARY", "DISCOURSE", "PHONOLOGY")
        val prerequisiteOf = mutableMapOf<String, List<String>>()
        knowledge.forEachIndexed { index, item ->
            val id = text(item, "id").ifEmpty { "knowledge[$index]" }
            if (text(item, "type").uppercase() !in knowledgeTypes) {
                report(id, "invalid knowledge type \"${text(item, "type")}\"")
            }
            if (text(item, "level") !in levels) report(id, "invalid level \"${text(item, "level")}\"")
            listOf("title", "titleFa").forEach { key ->
                if (text(item, key).isEmpty()) report(id, "blank $key")
            }
            val prerequisites = array(item, "prerequisites").map { it.toString() }
            prerequisites.filterNot { it in knowledgeIds }.sorted().forEach { missing ->
                report(id, "prerequisite \"$missing\" is not a knowledge item")
            }
            val taught = array(item, "lessons").map { it.toString() }
            if (taught.isEmpty()) report(id, "teaches no lesson")
            if (taught.distinct().size != taught.size) report(id, "duplicate lesson reference")
            taught.filterNot { it in lessonIds }.sorted().forEach { unknown ->
                report(id, "lesson \"$unknown\" is not a lesson")
            }
            if (array(item, "skills").isEmpty()) report(id, "no skills")
            prerequisiteOf[id] = prerequisites
        }
        // A cycle would make the curriculum impossible to order (audit §22).
        knowledgeIds.sorted().forEach { start ->
            val pending = ArrayDeque(prerequisiteOf[start].orEmpty().filter { it in knowledgeIds })
            val visited = mutableSetOf<String>()
            var cyclic = false
            while (pending.isNotEmpty()) {
                val current = pending.removeLast()
                if (current == start) {
                    cyclic = true
                    break
                }
                if (!visited.add(current)) continue
                prerequisiteOf[current]?.filter { it in knowledgeIds }?.forEach { pending += it }
            }
            if (cyclic) report("knowledge", "\"$start\" participates in a prerequisite cycle")
        }

        // --- coverage: a lesson with no content is invisible to the learner ---
        val exerciseLessonIds = exercises.map { text(it, "lessonId") }.toSet()
        val vocabularyLessonIds = vocabulary.map { text(it, "lessonId") }.toSet()
        val taughtLessonIds = knowledge.flatMap { item -> array(item, "lessons").map { it.toString() } }.toSet()
        lessonIds.sorted().forEach { lessonId ->
            if (lessonId !in exerciseLessonIds) report(lessonId, "lesson has no exercises")
            if (lessonId !in vocabularyLessonIds) report(lessonId, "lesson has no vocabulary")
            if (lessonId !in taughtLessonIds) report(lessonId, "no knowledge item teaches this lesson")
        }

        if (errors.isNotEmpty()) {
            throw GradleException(
                "Content validation failed (${errors.size} problem(s)):\n" + errors.joinToString("\n"),
            )
        }
        logger.lifecycle(
            "Content validation passed: ${lessons.size} lessons, ${vocabulary.size} words, " +
                "${exercises.size} exercises, ${placement.size} placement questions, " +
                "${knowledge.size} knowledge items (bundle v${versions["lessons.json"]}).",
        )
    }
}

// Gate every build on content validity: a dangling lessonId or an out-of-range
// correctIndex is a data bug the Kotlin compiler cannot see, and it would ship
// silently. Costs a few seconds, so it is always worth it.
tasks.named("preBuild") {
    dependsOn("validateContent")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.ktx)
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
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
