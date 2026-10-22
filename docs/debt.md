# Known design debt (accepted, tracked here)

> Moved out of the root `AGENTS.md` (checklist v10). Everything here is a deliberate,
> accepted trade-off, not a bug to fix silently.

- **Mastery by dimension is session-scoped.** `MasteryProfile` is folded from the answers of the
  session in progress; nothing persists per dimension, so the next session starts blind and the
  selector only adapts once the learner has answered inside that session (B-1). A cross-session
  version needs a per-dimension column/table (Room v6 + migration) and is the natural next step.
- **The companion key was pinned from a sideloaded build — since verified against the store
  build (N-1, Oct 2026).** `CompanionApp.EXPECTED_SIGNING_SHA256` holds the Zaribar 3.4.10
  certificate (`CN=wolfandmoon`), read from the sideloaded APK on the device. The comparison
  itself is done: the APK Cafe Bazaar publishes for `org.token.zaribar` (versionCode 16, pulled
  through the store's download-info API) carries the **same** V2 digest, so store installs pass
  the check. A future key change would still fail closed (no free access — never a bypass), so
  re-read the digest from the store build before each release — the procedure is in
  [`release.md`](release.md). The Myket/Play builds of Zaribar (if any) remain unverified: both
  stores' copies can only be pulled from a device that installed them there. **Settled (N-2):** where the Bazaar link lives. Every download button used to sit in the
  installed state (Home, paywall, settings), where it offered the learner what they already had; the
  link now belongs to the not-installed state, and one pure rule — `CompanionOffer.isOffered` —
  decides it for the paywall, the Settings subscription card and the Home trial banner, so a device
  holding the grant can never be shown the offer again.
- **The 7-day trial is local.** Clear-data or a reinstall restarts it; there is no server to anchor
  it. Accepted (the alternative is the store's own free-trial offer). Precedence is
  SUBSCRIPTION → COMPANION_APP → TRIAL → LOCKED in `EntitlementPolicy`.
- Myket has no real subscriptions: expiry is local and re-derived from the owned purchase time —
  re-prove purchase through support if a user claims a lost subscription. A refund or revocation
  happens in the store and cannot be observed offline, so it is neither granted nor renewed: access
  is strictly bounded by the last reconciled expiry (`SubscriptionRecoveryTest` pins that a revoked
  purchase loses access once that mirror runs out). A server would be needed to revoke on demand.
- `bazaarRsaKey` unset → Poolakey verification disabled; the build falls back to
  `SecurityCheck.Disable`. Set it in `keystore.properties` before publishing to Bazaar (the
  Bazaar release tasks already refuse to assemble without it — same gate as the companion digest).
- Room is at `version = 5` with exported schemas (`app/schemas`). Schema changes must ship a
  migration **and** be added to `AppContainer.database` — Room throws at open time when a path from
  the installed version is missing, so a forgotten `addMigrations` crashes upgrading installs.
- Due counts capture `now` at collection time; a long-running session won't see newly-due items
  until the ViewModel is recreated.
- The daily reminder is pinned to 19:00 local (`DailyReminder.REMINDER_HOUR`); the time is not
  learner-configurable yet (checklist B-4).
- The ALMOST hint is suppressed for GRAMMAR items but cannot tell "one edit away from a *different*
  word" (A-2c/A-2b: `affect`/`effect`) from a typo without a dictionary — on a vocabulary target the
  learner is still told to check the spelling. Accepted: the banner shows the correct answer
  alongside it, so the word itself is never ambiguous.
- **Production grading is structural, not grammatical (B-10 residual).** A typed sentence passes
  when it carries every anchor in order and reuses ≥ 50% of one exemplar's words
  (`AnswerChecker.PRODUCTION_MIN_EXEMPLAR_OVERLAP`; `DocCodeConsistencyTest` pins this number to
  the code, checklist v15 B-12 — the doc had drifted to "60%"); a scramble that
  keeps the *anchors* in order, or a wrong **non-anchor** form (`goed`), can still pass, and a
  valid variant far from both authored exemplars is failed. The two named negatives (scramble at
  full overlap, wrong graded verb form) are already pinned in `ProductionRecallTest`; closing the
  rest needs more accepted variants per item (content) or a grammar judge (out of scope offline).
  Accepted for now.
- **Study time is credited by an interaction-based ticker (P7 rework, see
  [`learner-model.md`](learner-model.md)).** Each resumed lesson/review screen samples a tick every
  30 s and writes it only while the learner acted within the 4-minute idle cap
  (`StudyTimePolicy`); the old "credited on close, best effort" rule is gone. Residual trade-offs,
  **both accepted at v14 B-11 (user-confirmed)**: an unattended screen keeps earning for up to the
  4-minute cap before stopping (the cap stands as a product decision), and history earned under
  the old counting is left untouched — noted once in the next release
  ([`release.md`](release.md)).
- Single `:app` module; split into `:core:*` / `:feature:*` when build times or ownership demand it.
- Persian UI strings are inline in composables instead of `strings.xml` (single-locale MVP) —
  extract when a second locale lands.
