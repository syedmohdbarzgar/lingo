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
scripts/release_check.sh            # checks the signing key, bazaarRsaKey, the companion digest,
                                    # and that the offline Vosk model is in assets
scripts/release_check.sh --print-template   # prints a keystore.properties template
```

**Companion digest re-read (N-1 — before every release).** `CompanionApp.EXPECTED_SIGNING_SHA256`
must match the certificate of the Zaribar build **the store publishes** (verified in Oct 2026
against the Cafe Bazaar build — same V2 digest as the sideloaded one; a changed key would fail
closed for every store install). To re-read it:

```bash
# 1. Ask Bazaar for the published artifact of the companion:
curl -sS -H 'Content-Type: application/json' --data-binary @- \
  'https://api.cafebazaar.ir/rest-v1/process/AppDownloadInfoRequest' <<'JSON'
{"properties":{"language":2,"clientVersionCode":1100301,"androidClientInfo":{"sdkVersion":33,"cpu":"x86,armeabi-v7a,armeabi"},"clientVersion":"11.3.1","isKidsEnabled":false},"singleRequest":{"appDownloadInfoRequest":{"downloadStatus":1,"packageName":"org.token.zaribar","referrers":[]}}}
JSON
# 2. Download the first URL of singleReply.appDownloadInfoReply.fullPathUrls, then:
$ANDROID_HOME/build-tools/<ver>/apksigner verify --print-certs zaribar.apk   # read the V2 digest
```

**Fallback when the API breaks (N-5).** The call above is undocumented and hard-codes
`clientVersionCode 1100301`, so it may stop answering without notice. It is not the only way:
open the Zaribar page in the Bazaar app on a device and download it there, or pull the APK from
any device that already installed it (`adb shell pm path org.token.zaribar` + `adb pull` every
listed path — this is also the only way to read a Myket/Play copy, if one exists), then run the
same `apksigner verify --print-certs` and compare the V2 digest. `scripts/release_check.sh`
prints this reminder (with the fallback) on every pre-flight, so the re-read cannot be skipped.

**Known item for the next release notes (B-11, decided v14).** Study time became interaction-based
(P7): a lesson or review screen only credits time while the learner has acted within the last
4 minutes — the cap stands as a confirmed product decision. Daily totals and streaks earned under
the old auto-counting were left untouched, not corrected; the release notes should say so once.

**Before the first Google Play release (N-4, decided v14).** The free-route offer opens Cafe
Bazaar in **every flavor** today — confirmed to stay that way while `googlePlay` is not published
on Play. Shipping it on Play requires either complying with Google's external-content-links
program (US-only links, enrollment, destination disclosure) or gating the offer out of the
`googlePlay` flavor — the gate is one value: flip `EXTERNAL_COMPANION_OFFER` to `false` in the
`googlePlay` flavor of `app/build.gradle.kts`. Every offer surface and
`CompanionApp.openDownloadPage` read that flag, and `PlayStoreLinkGateTest` proves it (it fails
if a surface stops reading it, or if this paragraph stops naming it). Re-decide at that point
([`billing.md`](billing.md)).

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
