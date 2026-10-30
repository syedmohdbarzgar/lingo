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

**Shape:** cards 22dp (`shapes.large`), fields 14dp, **buttons and chips are pills**
(`shapes.extraLarge = 50%`). **Shadow:** 10dp elevation at 8% ink (`AppCard`) — the shadow is what
stops a card reading as paper; a hairline alone never did.

**Motion** (`component/Reveal.kt`): each card on an opening screen fades in (240ms) and rises 26dp
(320ms) with `FastOutSlowInEasing`, 55ms after the card above it. The first card never waits, the
end state is the only state after ~0.5s, and because the motion is a `graphicsLayer` the layout never
shifts and nothing is hidden if animation is off. A new conversation turn reveals the same way.

## What stays (non-negotiable)

Forced RTL with English through `EnglishText`/`AnswerField`; feedback as icon **and** text; 48dp touch
targets; cards spanning the available width; spacing from `AppSpacing`; no raw colors in features —
every value here lives behind `MaterialTheme`/`LocalAppExtendedColors`.

## Open follow-ups

- The launcher icon and the launch splash already use the brand blue, so reverting the accent left
  them consistent rather than stale.
