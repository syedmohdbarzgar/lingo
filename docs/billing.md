# Billing — subscription × 3 marketplaces

> Moved out of the root `AGENTS.md` (checklist v10). This is the billing half of the
> connectivity policy; the offline/online rules themselves live in
> [`docs/connectivity.md`](connectivity.md).

Product decision (user-confirmed): **all content is subscription-gated with a 7-day free trial**
(`TrialClock.TRIAL_MILLIS` = `7 * 24h`, consumed against elapsed real time so it survives app
restarts; it lives in local DataStore only, so clearing app data restarts the trial);
two plans — `sub_monthly` (monthly) and `sub_yearly` (yearly). Each release build targets one
market and embeds only that market's billing SDK:

| Flavor | Store | SDK (verified via javap/docs Oct 2026) | Notes |
|---|---|---|---|
| `bazaar` | کافه بازار | `com.github.cafebazaar.Poolakey:poolakey:2.2.0` | Real subscriptions (`subscribeProduct`). Local RSA key via `keystore.properties` → `bazaarRsaKey`; falls back to `SecurityCheck.Disable` until set — **must be set before release**. |
| `myket` | مایکت | `com.github.myketstore:myket-billing-client:1.19` | Myket has **no subscription products** (official KB). Plans are consumables: 30/365-day local expiry, consumed after purchase so renewal can re-buy. Public key → `myketPublicKey`. Its manifest needs `manifestPlaceholders` (`marketApplicationId`/`marketBindAddress`/`marketPermission`, official sample values) — already set in `app/build.gradle.kts`. |
| `googlePlay` | Google Play | `com.android.billingclient:billing:9.1.0` | Subs (`SUBS`), first offer, acknowledge + purchase listener, 32-day grace mirrored locally. |

Rules:

- UI/domain only see `core/billing/BillingGateway` — store specifics live in the flavor's
  `PlatformBillingGateway`. Adding a 4th market = new flavor + one file, zero UI changes.
- Access rules are pure & unit-tested: `EntitlementPolicy` (SUBSCRIPTION → COMPANION_APP →
  TRIAL → LOCKED, with an `AccessReason` so the UI never calls a free grant a purchase) in
  `core/billing/Billing.kt`.
- **Free-access companion (product decision, Oct 2026, user-confirmed): while the Zaribar app
  (`org.token.zaribar`) is installed the subscription is free.** The grant follows the install
  and is re-checked on every foreground (`EnglishApp.onActivityResumed`) and whenever Home,
  the paywall or Settings opens — it never outlives the install. Detection is one local
  `PackageManager` lookup (`core/billing/CompanionApp.kt`, offline, no permission); the
  package must stay listed in the manifest `<queries>` block or API 30+ package visibility
  hides it. **The install is only trusted when its signing certificate matches
  `CompanionApp.EXPECTED_SIGNING_SHA256`** (N-1: the package id alone is spoofable by any APK
  that declares it); `CompanionSignature` does the normalization/matching and is unit-tested
  because the Android lookup cannot run on the JVM. **Release builds fail closed while that
  digest is empty**: `app/build.gradle.kts` reads the constant and throws for every `assemble*`
  /`bundle*` *Release* task, exactly like `bazaarRsaKey`; debug builds keep the runtime warning. While the grant is active, the paywall
  replaces the plans with the **free state only — no store link**: the companion is already installed,
  so a download button would offer the learner what they already have.
- **Free route, advertised only where it applies (N-2/N-3).** `CompanionApp.openDownloadPage`
  (Bazaar deep link `bazaar://details?id=org.token.zaribar`, web fallback) is offered to the
  learner who does **not** hold the trusted grant: the paywall carries a "راه رایگان: «زریبار»" card
  next to the price, the Settings subscription card repeats the route with the same button, and
  the Home trial banner names it in one line. All three read the one pure rule
  `CompanionOffer.isOffered(companionStatus, access, reason)` (`core/billing/Billing.kt`,
  unit-tested): it hides the offer on a device that already holds the grant and for a paying
  subscriber, so the link can never be shown to someone it would not help. The lookup reports a
  three-way `CompanionStatus` (N-3): a device holding a Zaribar signed by an **untrusted** key is
  told to *update* it from the store — never granted, and never told to install what it already
  has.
- **Store policy on the Bazaar link (N-4, checked Oct 2026).** The offer opens Cafe Bazaar in every
  flavor today. Google Play: for US users, linking out to another store's app download is allowed
  under the external-content-links program (enrollment, US-only links, destination disclosure —
  support.google.com/googleplay/android-developer/answer/16470497); outside the US no blanket
  permission is established, so a `googlePlay` build must not ship the offer until that program is
  met or the link is restricted. Myket: no published rule was found that prohibits pointing at a
  competitor store (its clause 6.9 concerns paid-service payment routing) — keep the offer, and
  get written confirmation before a Myket release if the risk matters. **Decision (v14,
  user-confirmed): keep the offer in all three flavors for now** — `googlePlay` is not published
  on Play yet; the compliance question is re-opened in [`release.md`](release.md) before the first
  Play release.
- Gating: `AppNavGraph` redirects any non-exempt route to `Routes.PAYWALL` when LOCKED;
  exempt routes are onboarding/placement/settings. Home shows a calm trial-countdown banner
  (with the free-route line when the companion is absent) and — when the companion grant is
  active — a "subscription is free" banner **without** a store link.
- The paywall's small print is **storefront-aware** (P3 honesty pass): only Bazaar/Play
  promise auto-renewal and cancellation; Myket says what it actually is — a fixed-length
  consumable with no auto-renew and nothing to cancel. `PaywallUiState.storefront` (from
  `billing.storefront`) carries the choice, and `SubscriptionMath.flatPeriodExpiry` is the
  single rule that restores Myket access after a reinstall (owned `purchaseTime` + a flat
  30/365 days).
- Store is source of truth; active subscriptions are mirrored locally with a 32-day grace so
  short offline spells don't lock the learner out.
- Release signing: fill `keystore.properties` (`storeFile/storePassword/keyAlias/keyPassword`,
  plus `bazaarRsaKey`, `myketPublicKey`). The file must stay untracked — it holds secrets.
  Run `scripts/release_check.sh` before a release: it answers the same questions as the Gradle
  gates up front (signing key, `bazaarRsaKey`, the companion digest) and prints exactly what to
  paste where, instead of failing 6 minutes into an `assemble*Release`. See
  [`docs/release.md`](release.md) for the release ritual itself.
- Do not add any other network features while billing is the sole `INTERNET` user (see
  [`docs/connectivity.md`](connectivity.md)).
