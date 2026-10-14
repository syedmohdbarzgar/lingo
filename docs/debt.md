# Known design debt (accepted, tracked here)

> Moved out of the root `AGENTS.md` (checklist v10). Everything here is a deliberate,
> accepted trade-off, not a bug to fix silently.

- **Mastery by dimension is session-scoped.** `MasteryProfile` is folded from the answers of the
  session in progress; nothing persists per dimension, so the next session starts blind and the
  selector only adapts once the learner has answered inside that session (B-1). A cross-session
  version needs a per-dimension column/table (Room v6 + migration) and is the natural next step.
- **The companion signing digest is not configured yet** (`CompanionApp.EXPECTED_SIGNING_SHA256`
  is empty), so a debug build trusts the `org.token.zaribar` package id alone and any APK declaring
  it unlocks the paid tier there. Release builds now refuse to assemble until it is set, so the
  bypass cannot ship — but the free-access grant stays unverified until the digest lands. Paste it
  from `keytool -printcert -jarfile zaribar.apk` to close N-1; the check is already implemented,
  unit-tested (including the gate's own parse of the constant) and enforced at release. Still open:
  confirming every flavor hides/redirects the Bazaar-only install link appropriately.
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
  word" (A-2b: `affect`/`effect`) from a typo without a dictionary — on a vocabulary target the
  learner is still told to check the spelling. Accepted: the banner shows the correct answer
  alongside it, so the word itself is never ambiguous.
- `studied seconds` are credited on screen close/finish (best effort), not via a foreground timer.
- Single `:app` module; split into `:core:*` / `:feature:*` when build times or ownership demand it.
- Persian UI strings are inline in composables instead of `strings.xml` (single-locale MVP) —
  extract when a second locale lands.
