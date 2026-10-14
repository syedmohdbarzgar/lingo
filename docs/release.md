# Release prep & toolchain

> Moved out of the root `AGENTS.md` (checklist v10). The everyday dev loop (unit tests +
> `validateContent`, and `scripts/install_debug.sh`) stays in the root file; this is the
> release-point ritual and the toolchain facts not to "fix" casually.

## Release points only

```bash
./gradlew :app:assembleBazaarDebug      # Cafe Bazaar APK (Poolakey billing)
./gradlew :app:assembleMyketDebug       # Myket APK (myket-billing-client)
./gradlew :app:assembleGooglePlayDebug  # Google Play APK (Play Billing 9)
```

Release `assemble*`/`bundle*` tasks **fail closed**: they refuse to build until `keystore.properties`
holds `bazaarRsaKey` and `CompanionApp.EXPECTED_SIGNING_SHA256` is filled in (see
[`docs/billing.md`](billing.md)). Debug is unaffected, so the everyday loop never needs those values.

Before a release, run the pre-flight so a missing value does not surface six minutes into an
`assemble*Release`:

```bash
scripts/release_check.sh            # checks the signing key, bazaarRsaKey, the companion digest
scripts/release_check.sh --print-template   # prints a keystore.properties template
```

## CI

`.github/workflows/ci.yml` runs the same two fast gates (content validation + unit tests) and
`lintBazaarDebug` on every push/PR; the three flavor assemblies only on schedule/manual
dispatch/tags. There is no emulator or device job. Only per-flavor unit-test tasks exist
(`testDebugUnitTest` is not a task under AGP 9's `onlyEnableUnitTestForTheTestedBuildType`).

## Toolchain facts (verified Oct 2026 — do not "fix" these casually)

- AGP **9.3.3** + Gradle **9.5.0**, `compileSdk 37`, `minSdk 24`, JDK toolchain 25.
- **`gradlew` and `scripts/*.sh` must stay mode `100755` in the git index.** The dev machine is
  Windows with `core.fileMode=false`, so the executable bit is never detected or committed — which
  made every CI run die with **exit code 126** at the first `./gradlew` step (and at `Lint`), while
  all the setup steps passed. Fix with `git update-index --chmod=+x gradlew scripts/install_debug.sh`,
  then check `git ls-files -s gradlew` prints `100755`.
- **Built-in Kotlin** (AGP 9 default): *never* apply `org.jetbrains.kotlin.android`.
- KSP `2.2.10-2.0.2` must match the built-in KGP (2.2.10). AGP itself floors KSP to this version.
- `android.disallowKotlinSourceSets=false` in `gradle.properties` is **required** for KSP's generated
  sources under built-in Kotlin.
- The Compose compiler plugin version **must equal the built-in Kotlin version**
  (`composeCompiler = "2.2.10"` in `gradle/libs.versions.toml`). If you bump AGP, re-check both KSP
  and composeCompiler refs.
- Room needs KSP; Room errors like `[MissingType] ... not all of its dependencies could be resolved`
  almost always mean a **Kotlin compile error elsewhere** (check the sources first, e.g. a doc
  comment containing `/*` opens a nested comment in Kotlin).
- Kotlin block comments **nest**: never write `path/*.ext` inside `/** ... */`.
- CI status on `afaa522` (v9) and every recent commit is **green**; the README carries the badge.
