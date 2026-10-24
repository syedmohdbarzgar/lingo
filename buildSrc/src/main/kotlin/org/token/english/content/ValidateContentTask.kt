package org.token.english.content

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

/**
 * `./gradlew validateContent` — the content gate, wired into `preBuild` by
 * `app/build.gradle.kts` and run on its own in CI before the unit tests.
 *
 * The task is a thin shell on purpose (checklist B-6): every rule lives in
 * [ContentValidator], which `ContentValidationTest` runs on the JVM, so the gate
 * that blocks a build and the test that guards the suite cannot drift apart.
 * A task *type* (rather than a `doLast` in a build script) also keeps the action
 * configuration-cache safe — it reads only its annotated properties.
 */
abstract class ValidateContentTask : DefaultTask() {

    /** `app/src/main/assets/content` — the five JSON files that make up the bundle. */
    @get:InputDirectory
    abstract val contentDir: DirectoryProperty

    @TaskAction
    fun validateContent() {
        val report = ContentValidator.validate(contentDir.get().asFile)
        if (report.errors.isNotEmpty()) {
            throw GradleException(
                "Content validation failed (${report.errors.size} problem(s)):\n" +
                    report.errors.joinToString("\n"),
            )
        }
        // Non-blocking nudge (checklist A-1): an exercise without `explanationFa`
        // teaches nothing after a miss. ExplanationCoverageTest holds the hard bar;
        // here we only surface the backlog to content authors while they edit.
        report.warnings.forEach { logger.warn(it) }
        logger.lifecycle(report.summary)
    }
}
