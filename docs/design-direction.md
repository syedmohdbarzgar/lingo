# Design direction — Xiaomi-inspired structure, Lingo brand blue (v28)

> The interface used to be Material 3 with its default surfaces: white cards, hairline outlines,
> 12–16dp corners, and elevation almost never used. That is what a review called *"like paper"* —
> a page of outlined rectangles with no depth, where nothing reads as a surface you can touch.
> v28 replaces that layer with a Xiaomi-inspired one: a bright canvas, large rounded surfaces and a
> soft shadow language.
>
> The **brand colour stayed Lingo blue**. Xiaomi's documented palette is orange, and an earlier pass
> of this change adopted it as the accent; that was reverted — the blue is the shipped identity and
> the store assets are built on it. What is borrowed from Xiaomi is the *structure* (surface, shape,
> shadow, motion), not its hue.

## Sources

- Xiaomi design system, as documented for AI agents in
  [`ndroussi/design-md-for-ai` — `design-md/xiaomi/DESIGN.md`](https://github.com/ndroussi/design-md-for-ai/blob/main/design-md/xiaomi/DESIGN.md):
  palette, type scale, 22px card radius, pill buttons, soft shadow values, "don't overuse orange as
  a full-background treatment".
- Xiaomi's own [MiSans design notes](https://hyperos.mi.com/font/en/about/) — clarity, readability,
  cross-language consistency — and the [MiSans family/licensing page](https://hyperos.mi.com/font/en/download/).

**Not claimed:** Xiaomi publishes no numeric HyperOS app tokens (radii, spacing, animation
durations) that we could verify, so the numbers below are *our* tokens chosen to match that
language — they are not presented as official Xiaomi specifications.

**MiSans is not bundled.** It is a Latin/CJK family with a licensing attribution requirement and no
Persian coverage; the app keeps Vazirmatn for Persian UI and the system face for English content.

## Tokens (`core/designsystem`)

| Role | Value | Note |
|---|---|---|
| Accent (`colorScheme.primary`) | `#2169EF` (`Brand.Primary600`) | Actions, selection, progress — never a whole background |
| Filled surface with white text (`brandStrong`) | `#1856C7` (`Brand.Primary700`) | White on `#2169EF` is 4.0:1; on this it is 5.5:1 |
| Soft callout (`primaryContainer`) | `#E8F2FF` | Ink `#0F2D6B` |
| Canvas | `#FFFFFF` | `#F5F6F8` for grouping |
| Ink / body / muted | `#191919` / `#222222` / `#6B7280` | |
| Border | `#D9DEE5` | Hairline, always paired with the shadow |
| Dark canvas / surface / border | `#0E0E10` / `#17171A` / `#2B2B31` | Accent lightens to `#60A5FA` (`Brand.Primary400`), container `#17366F` |

**Shape:** cards 22dp (`shapes.large`), fields 14dp, **buttons and chips are pills** — and
**dialogs 20dp / bottom sheets 22dp top corners** (`DialogShape` / `SheetShape`). Material3's dialog
and sheet defaults resolve from `shapes.extraLarge`, which is the pill token here, so every dialog and
sheet came up as a capsule until both call sites were given their own shape (v31, user report:
«خمیدگی گوشه‌ها در دیالوگ و bottom sheet خیلی زیاد است»).
(`shapes.extraLarge = 50%`). **Shadow:** 10dp elevation at 8% ink (`AppCard`) — the shadow is what
stops a card reading as paper; a hairline alone never did.

**Motion** (`core/designsystem/Motion.kt` + `component/Reveal.kt`). Every duration in the app is a
token in `AppMotion`, so "how long does this take" has one answer; nothing in the system exceeds
0.26s for a route change or 0.18s for a change inside a screen.

| Move | What it looks like |
|---|---|
| Forward / back (`SCREEN_MS` 260) | The outgoing screen slides 1/12 of the width in the direction of travel and fades; the incoming one arrives from 1/5 of the opposite edge and fades in. Below 34 the splash hand-off is the same idea, declared by the activity (see `SplashActivity`). |
| Sibling tabs (Home → Review → Vocabulary → Progress) | **Cross-fade only** (`SCREEN_MS / 2`). The four bottom-bar tabs are the same level, so sliding them sideways claimed a depth the move does not have — and it was the heaviest transition, paid on the most frequent move. The rule is `isSiblingMove(from, to)`, declared once on the `NavHost`. |
| In-screen step (`STEP_MS` 180, `STEP_SHIFT_DP` 8) | The next lesson question, the next review card, the next teaching page and a new conversation turn fade and rise a few dp, keyed on the thing that changed so grading a card never re-plays it. |
| Page reveal (`REVEAL_*`) | Each card fades in (240ms) and rises 26dp (300ms) with `FastOutSlowInEasing`, 40ms after the card above it, over at most **8 slots** — so the whole page settles in 580ms (`REVEAL_TOTAL_MS`) however many cards it has. The first card never waits. |

The reveal runs on **one clock** (`rememberPageReveal` + a single `Animatable`), and every card reads
its own window out of that clock inside `graphicsLayer`. The earlier version gave each card its own
coroutine and delay, which cost 2N animatables on a page and captured the delay at first composition —
a card appearing above another (the plan or the focus card arriving a moment later) left the slots
below on the old timing, so the page could reveal out of order. Order is now a property of the layout,
not of when each card started. Because the motion is a `graphicsLayer`, layout never shifts, the
scroll range never changes mid-animation, and nothing is hidden if animation is off. `AppMotionTest`
pins the sibling rule and the ordering property; the pixels stay a device check.

## What stays (non-negotiable)

Forced RTL with English through `EnglishText`/`AnswerField`; feedback as icon **and** text; 48dp touch
targets; cards spanning the available width; spacing from `AppSpacing`; no raw colors in features —
every value here lives behind `MaterialTheme`/`LocalAppExtendedColors`.

**No bare number in Persian copy.** Every count, percentage, measure, step label and two-count phrase
goes through `core/common/BidiText.kt` (`bidiNumber`, `persianMeasure`, `persianPercent`,
`persianCountPair`, `persianStepLabel`), never string interpolation: those helpers add a directional
isolate and a non-breaking space, so «۱۵ دقیقه» cannot become «دقیقه ۱۵» or break across lines on
device. Engines that author Persian (`reasonFa`) use the same functions, since `core/common` is the
one place both layers may depend on. Pinned by `BidiTextTest`, `BidiNumbersTest` and
`DailyGoalCopyTest`.

## Open follow-ups

- The launcher icon and the launch splash already use the brand blue, so reverting the accent left
  them consistent rather than stale.
