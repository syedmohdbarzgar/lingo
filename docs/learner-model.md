# Learner model, engines & the adaptive layer

> Moved out of the root `AGENTS.md` (checklist v10). The package layout, the dependency
> rule and the UI/DI conventions stay in the root file; this is *how the learner model is
> computed*.

- Engines are pure and swappable: `Sm2ReviewScheduler`, `DefaultMasteryEngine`,
  `DefaultLearningPlanner`, plus the knowledge layer — `KnowledgeGraph` (prerequisite ordering and
  unlocking) and `DefaultKnowledgeEngine` (how one graded answer moves a node's mastery and review
  date). The UI must never learn how intervals/mastery are computed (technical spec §19).
- The adaptive layer on top of that graph is also pure and tested: `PrerequisiteEngine`
  (readiness / remediation / dependency depth), `AdaptiveLearningPlanner` (the ordered, justified
  "what to study now and why"), `DefaultMasteryProfileEngine` (mastery split by dimension —
  recognition / recall / comprehension / application / production), and `AdaptiveExerciseSelector`
  (front-loads the exercise formats whose mastery dimension is still weak).
- **The adaptive layer is wired to the UI (checklists B-1/B-6).** `GetTodayPlanUseCase` builds the
  graph from the seeded items, plans, and returns the ordered `LearningAction`s inside
  `TodayPlan.actions`; `HomeScreen` renders them with each action's own `reasonFa` (the "برنامهٔ
  امروز" card). `GetFocusPlanUseCase` then asks `RemediationEngine` for the weak spot — one node,
  why it is worth drilling now, what it blocks, and how it is verified (`FocusPlan`) — and Home
  renders it as the "نقطهٔ ضعف" card whose CTA opens the focused session (`lesson/{lessonId}?focusItemId=`).
- **A focused session (B-1) is the engine's decision, not a screen's.** It shows only the lesson's
  exercises that are evidence about the node (the same `KnowledgeEvidence` rule that writes the
  learner model), keeps their authored order, leads with the engine's reason, and reports the
  node's mastery against `FocusPlan.reassessThreshold`. Only a node the engine itself calls weak
  (mastery below `RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD`, with evidence) gets a block; a node
  the learner never studied is *taught*, not drilled, so `GetFocusPlanUseCase` returns `null` for it.
- **The remediation bar is one bar (v9 B-1).** `RemediationEngine.DEFAULT_REASSESS_THRESHOLD` equals
  `DEFAULT_WEAKNESS_THRESHOLD` (0.5), and `RemediationEngine.assess` decides from the node's
  **stored** `knowledge_state.mastery` (not the session profile, which seeds an untouched dimension
  at 1f), so a node cannot leave the plan while the engine still says NEEDS_MORE, and a first correct
  answer cannot read as REMEDIATED.
- **`AdaptiveExerciseSelector` + `DefaultMasteryProfileEngine` steer a session's queue.** Every
  graded answer is folded into a per-dimension `MasteryProfile` (recognition / recall /
  comprehension / application / production) and the next exercise is chosen with the selector, so
  the kinds of knowing still untested lead the session instead of the next authored item. Two rules
  keep it safe: with no evidence the sort is stable, so a first-teach lesson runs exactly as
  authored, and a missed exercise still comes back later in the session (a failed dimension ties
  with an untouched one at 0, and the tie is broken by queue position). The profile is
  **session-scoped** — there is no per-dimension column yet (see [`docs/debt.md`](debt.md)).
- **Planner time budget (checklist B-2):** review is never dropped for lack of budget — its
  estimate is capped at half the day's target and it always leads — and the remaining actions
  stop at the **first** one that does not fit instead of skipping ahead to a cheaper one. With the
  daily goal already met the plan collapses to the review block; it is never empty.
- **Study time is interaction-based with an idle cap (P7 rework).** `StudyStats` — what feeds the
  daily goal, the planner's budget and the streak — only grows while the learner is on a **resumed**
  lesson/review screen *and* has acted within `StudyTimePolicy.IDLE_CAP_MILLIS` (4 min): the
  screen's lifecycle ticker calls `onStudyTick()` every `TICK_MILLIS` (30 s) and each accepted tick
  credits `TICK_SECONDS` (30). Leaving the screen, backgrounding the app, or idling past the cap
  stops the crediting — the earlier `init` loop in both ViewModels credited time merely for the app
  being open and could fill the 15-minute goal with no studying at all.
- **C2 is deliberately vocabulary/discourse-focused (checklist A-8).** The graph carries GRAMMAR
  nodes at A1–C1 (13/11/8/9/4) but **none at C2** — C2's 13 nodes are 4 VOCABULARY and 9 DISCOURSE.
  The structural work at that level (inversion, the subjunctive, inversion after *never*) ships
  inside the `c2.rhetoric` lesson's own exercises rather than as a graph grammar node, because C2
  mastery is about precision, style and argument, not new forms. Stated here so "0 C2 grammar
  items" reads as the design, not a gap; adding C2 grammar nodes is a content decision, not a bug.
- **The learner model is per knowledge item, not per exercise or per skill.** Every graded answer
  is attributed to the curriculum nodes its lesson teaches (`KnowledgeEvidence`, a pure tested rule)
  and written to `knowledge_state` with an atomic read-modify-write, exactly like skill mastery.
  `DefaultKnowledgeEngine` reuses `Sm2ReviewScheduler` for intervals/ease and `MasteryEngine` for
  mastery, so "known" means one thing everywhere. Surfaced on the Progress screen.
- **Progress is three independent skill tracks, not one blended number (P2).**
  `SkillProfileEngine` (pure, in `domain/engine`) folds three sources into one `SkillProfile` per
  axis: the *persisted* per-skill rows (`skill_mastery`), the curriculum side
  (`KnowledgeState.exposureCount` as practice and `lapses` as misses) and the review side
  (`review_attempt` joined to `review_item`, correct = GOOD/EASY — the same rule as
  `observeAttemptAggregate`). The axes are `GRAMMAR`, `VOCABULARY`, `READING` — exactly the three
  the placement test tags; `LISTENING`/`SPEAKING`/`WRITING` are not tracks because no learnable
  content answers only to them. **Mastery is read from the persisted rows, never recomputed**, so
  the EWMA stays single-source. The status is **evidence-gated**: `NOT_STARTED` at zero evidence,
  `BUILDING` below `DEFAULT_BUILDING_EVIDENCE = 5` attempts, and only past that does mastery
  separate `CONSOLIDATING` from `CONSOLIDATED` (0.75) — checking mastery first would call a single
  correct answer (mastery seeded at 1.0) consolidated. `GetSkillProfilesUseCase` returns
  `emptyList()` when no content is seeded, and Home + Progress both render the shared
  `SkillTrackProgress` composable (Home compact), so the two screens cannot disagree. Node
  accounting is not a clean partition and the tests say so: of the 89 nodes, GRAMMAR claims 49,
  VOCABULARY 15 and READING 16, four nodes belong to two tracks
  (`grammar.passive-simple`, `discourse.reason-cause`, `discourse.narrative-tenses`,
  `vocab.idioms-and-figurative`) and 13 belong to none (a node that only trains LISTENING or
  PHONOLOGY).
