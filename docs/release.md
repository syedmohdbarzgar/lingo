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

Release `assemble*`/`bundle*` tasks **fail closed**, in three ways:

1. they refuse to build until `keystore.properties` holds `bazaarRsaKey`;
2. they refuse to build until `CompanionApp.EXPECTED_SIGNING_SHA256` is filled in — otherwise any
   APK declaring `org.token.zaribar` would unlock the paid tier (see [`docs/billing.md`](billing.md));
3. they refuse to build until the offline Vosk model is unpacked into
   `src/main/assets/vosk/vosk-model-small-en-us-0.15` (checked by the `uuid` marker plus `am/` and
   `graph/`, the same three markers `scripts/release_check.sh` trusts). The model is fetched
   (`scripts/fetch_vosk_model.sh`) and git-ignored, so a release built from a clone that skipped the
   fetch would install and then have a conversation-practice screen that can only report
   «مدل گفتار پیدا نشد» — a dead feature no compiler catches.

Debug is unaffected, so the everyday loop never needs those values.

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

## Readiness check (Phase 4, Nov 2026)

Measured on the dev machine, no device attached. Everything below is a real run, not an
assertion of intent:

| Gate | Command | Result |
| --- | --- | --- |
| Content | `./gradlew validateContent --rerun` | pass |
| Unit + typecheck | `./gradlew :app:testBazaarDebugUnitTest` | **580 tests / 67 classes, 0 failures** |
| Lint | `./gradlew :app:lintBazaarDebug` | **0 errors**, 28 warnings (9 `UnusedResources`, 5 `ObsoleteSdkInt`, 4 `UseKtx`, the rest version nags) |
| Flavors | `assembleBazaarDebug`, `assembleMyketDebug`, `assembleGooglePlayDebug` | 3/3 build; the only permission difference is the marketplace billing one |
| DB upgrade | `DatabaseUpgradeTest`, `RoomMigrationTest` (in the unit suite) | v1→v9 chain executed on real SQLite with learner rows; every value survives, the v9 tables arrive empty |

APKs: `bazaar` 102,213,988 B, `myket` 102,417,951 B, `googlePlay` 102,963,716 B (~98 MiB each,
debug), all three shipping exactly `arm64-v8a`, `armeabi-v7a`, `x86_64`. `INTERNET` is still the
only online permission and still billing-only ([`connectivity.md`](connectivity.md)).

### 16 KB page size — found broken, fixed here

Android 15+ devices can boot with 16 KB memory pages, and a native library whose `LOAD` segments
are only 4 KB-aligned cannot be loaded there: conversation practice would die at model load on new
hardware, with the whole rest of the app fine. `libvosk.so` from **vosk-android 0.3.47** was exactly
that (measured with `llvm-readelf -l`, per ABI, in the shipped APK):

| vosk-android | arm64-v8a | armeabi-v7a | x86 | x86_64 |
| --- | --- | --- | --- | --- |
| 0.3.47 (was shipped) | 0x1000 | 0x1000 | 0x1000 | 0x1000 |
| 0.3.75 (shipped now) | **0x4000** | 0x1000 | 0x1000 | **0x4000** |

The fix is two lines: `vosk = "0.3.75"` in [`gradle/libs.versions.toml`](../gradle/libs.versions.toml)
and an `ndk { abiFilters }` in `app/build.gradle.kts` that keeps `arm64-v8a`, `armeabi-v7a` and
`x86_64` only (the APK used to carry JNA's dead `armeabi`/`mips`/`mips64` natives plus a 32-bit
`x86` copy of `libvosk` — ~10 MB nobody can load). The 32-bit ABIs stay at 0x1000 by design: they
run on 4 KB pages only, and Google's requirement covers the 64-bit ABIs. Re-verify after any native
dependency bump:

```bash
unzip -l app/build/outputs/apk/bazaar/debug/app-bazaar-debug.apk | grep '\.so$'
$ANDROID_HOME/build-tools/<ver>/zipalign -c -P 16 -v 4 app/build/outputs/apk/bazaar/debug/app-bazaar-debug.apk
llvm-readelf -l lib/arm64-v8a/libvosk.so | grep LOAD   # every Align must be 0x4000
```

Both now pass: `zipalign -c -P 16` prints `Verification successful` and every 64-bit `.so` in the
APK is aligned 0x4000, `libvosk.so` included.

### What only a phone can answer (`scripts/verify_device.sh`)

No emulator can start on this machine (no hardware acceleration) and no phone was attached, so
these were **not** run: an install/upgrade on real hardware, the recogniser itself (loading the
model, a spoken take, a verdict), and the 16 KB load path on a 16 KB device. `scripts/verify_device.sh`
performs the hardware half deliberately — install, clear data, **airplane mode on**, cold start
(timed, seeding), second cold start, the seeder's own `seeded vN in … ms` line, and a logcat crash
sweep — and exits 0 as a no-op when nothing is attached.

## CI

`.github/workflows/ci.yml` runs the same two fast gates (content validation + unit tests) and
`lintBazaarDebug` on every push/PR; the three flavor assemblies only on schedule/manual
dispatch/tags. There is no emulator or device job. Only per-flavor unit-test tasks exist
(`testDebugUnitTest` is not a task under AGP 9's `onlyEnableUnitTestForTheTestedBuildType`).

## Toolchain facts (verified Oct 2026 — do not "fix" these casually)

- AGP **9.5.0** + Gradle **9.6.0** (AGP 9.5 refuses anything below Gradle 9.6), `compileSdk 37`,
  `minSdk 24`, JDK toolchain 25. AGP 9.5 still bundles Kotlin **2.2.10**, which is why the KSP
  (`2.2.10-2.0.2`) and Compose-compiler (`2.2.10`) pins did not move with it.
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
- **Native payload is ABI-filtered and 16 KB-aligned** (`arm64-v8a`, `armeabi-v7a`, `x86_64`;
  vosk-android 0.3.75). Both are release requirements, not preferences — the measurement table and
  the three-line re-check are in the readiness section above. Removing the `abiFilters` block puts
  ~10 MB of unloadable 32-bit/mips natives back in the APK.
- CI status on `afaa522` (v9) and every recent commit is **green**; the README carries the badge.
