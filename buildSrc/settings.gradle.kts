// buildSrc is its own build, so the root settings do not reach it: import the
// root catalog explicitly to keep `org.json` (the artifact the content gate and
// the unit tests both run on) and the Kotlin plugin version in one place.
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
