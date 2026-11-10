# Learner model, engines & the adaptive layer

> Moved out of the root `AGENTS.md` (checklist v10). The package layout, the dependency
> rule and the UI/DI conventions stay in the root file; this is *how the learner model is
> computed*.

- Engines are pure and swappable: `Sm2ReviewScheduler`, `DefaultMasteryEngine`,
  `DefaultLearningPlanner`, plus the knowledge layer — `KnowledgeGraph` (prerequisite ordering and
  unlocking) and `DefaultKnowledgeEngine` (how one graded answer moves a node's mastery and review
  date). The UI must never learn how intervals/mastery are computed (technical spec §19).
- **One answer is evidence once (Phase 1, Room v9).** `ProgressRepository.claimAnswer` stores the
  day's answer per exercise in `question_attempt` *before* anything is written, and the use cases
  write the skill meter and the graph only from a claimed answer. Re-answering the question that was
  just answered — the retry button, or re-running the lesson — therefore moves nothing: without the
  claim the EWMA (+0.2 a step) crossed the `STRONG_MASTERY` bar on seven taps of one question, and
  the streak grew with them. The claim is keyed on the **exercise**, not its format, because a
  remediation block drills several different questions that share a shape and every one of them is
  real work. One exception is deliberate: a miss followed by a hit on the same question the same day
  counts (fixing it is the evidence), while a hit followed by another miss does not punish twice.
  `question_attempt` deliberately keeps only the day's answer (one row per exercise, overwritten);
  the *history* is a separate, append-only table — see the persistence bullet below.
- **A step remembers what was answered, not just how far (Phase 2, Room v9).** `lesson_attempt` is
  append-only: every graded answer is written with the step that asked it, the text the learner
  typed or picked, the verdict and the time — a retry is a second row, never a rewrite. Two things
  read it. First, resume: `StageResume.cursorFor` opens a reopened step on the **first question with
  no recorded answer**, because the one-integer cursor could not tell "answered question 4" from "was
  on question 4 when the process died", and a content update that reordered a step silently moved
  it onto a different question. A lesson with no recorded answers (an install from before the log)
  keeps its saved cursor rather than restarting. Second, the step's own completion: `lesson_state
  .stageCompletedAt` stores `stageId=millis` per finished step, stamped the **first** time it is
  completed — revisiting a done step does not rewrite when it was done — so "when" is a recorded
  fact. `ProgressRepository.reset()` clears the log with the rest of the learner's state.
- **A graded answer feeds the track it actually evidences (Phase 1).** The item's `skill` says which
  knowledge the answer is *about*; how it was produced decides the rest. A `sentence_production`
  answer credits `WRITING` as well as its own skill (`SubmitExerciseUseCase`), a conversation turn
  credits `SPEAKING` when it was spoken and `WRITING` when it was typed
  (`RecordConversationTurnUseCase`), and a `RETRY` verdict writes nothing — the app cannot tell a
  wrong answer from a recogniser error, and turning that doubt into a penalty is what the
  conversation screen refuses to do. Speaking lands on the lesson's `PHONOLOGY` nodes through the
  same `KnowledgeEvidence` rule the exercises use.
- **Finished steps are not mastery (Phase 1).** `LessonMastery` turns lesson state plus node evidence
  into `NOT_STARTED → IN_PROGRESS → COMPLETED → MASTERED`. `COMPLETED` means the required steps have
  been walked; `MASTERED` means every node the lesson teaches passes `KnowledgeGates.isSecured` (no
  node needs today's practice). Progress shows the second as a subset of the first, so «درس تمام‌شده»
  can no longer read as "learned". The 30-day claim stays `isLongTermMastered`.
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
  node's mastery against `FocusPlan.reassessThreshold`. A node is drilled for one of three reasons:
  the engine calls it weak (mastery below `RemediationEngine.DEFAULT_WEAKNESS_THRESHOLD`, with
  evidence); an explicit request names a strong-but-unsecured node for a **retest**; or an explicit
  request names a node whose own **schedule is due** (`KnowledgeGates.isDue`, the same date
  `nextReviewAt` the lesson writes). A node the learner never studied is *taught*, not drilled, and a
  secured node is refused even when its date has passed, so `GetFocusPlanUseCase` returns `null` for
  both.
- **The due node is served, not just scheduled (review→graph pass).** `GetDueKnowledgeReviewsUseCase`
  reads the node states, keeps the due ones the gates do not call secured, and asks
  `GetFocusPlanUseCase` for each node's drill — so a row exists only where the focused session will
  actually run, and it carries that session's lesson id. The review screen renders them in a
  "مرور هدفمند" card beside the word queue (the two halves are separate: an empty card queue can
  still owe graph reviews), and a failed read of this half degrades to "nothing due" instead of
  blanking the screen the cards already populate. Both reads run again whenever the screen is
  **resumed** — never mid-session, and without a spinner — and the finished card session offers a
  «مرور هدفمند» return, so work completed in the drill is no longer still listed, and work still owed
  is no longer two phases out of reach. Probes are bounded (`MAX_PROBES`, most overdue
  first, `DEFAULT_LIMIT` rows), because each probe re-reads the graph through the focus use case.
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
  authored, and a missed exercise still comes back later in the session  (a failed dimension ties with an untouched one at 0, and the tie is broken by queue position). The profile is
  **cross-session** (checklist B-1, Room v6): every answer also writes its dimension to
  `dimension_mastery`, and the next session seeds `MasteryProfile` from those rows before the first
  exercise, so the selector can steer immediately instead of waiting for an in-session answer —
  while an untouched dimension stays *absent* rather than 0, keeping "no evidence" distinct from
  "failed" (see [`docs/debt.md`](debt.md)).
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
- **A lesson ends with its conversation (v33).** The exercises prove the learner *recognises* the
  structure; the dialogue is where they have to *use* it in a reply, so a lesson is not finished when
  its last exercise is answered. `owedConversation` (pure, in `LearningPlanner.kt`) names the first
  completed lesson whose dialogue (four turns at A1, five or six at A2 and above) has not been walked
  through, and `canOpenLesson` refuses
  everything after it. `HomeViewModel` exposes that lesson as `HomeUiState.dialogueDue`, so the
  next-lesson card offers the conversation instead of the lesson and says why — the button and the
  permission read the same rule and cannot disagree. Completion is written by
  `ConversationPracticeViewModel` once every turn has been **answered** (a graded answer, not a skip)
  into `ConversationRepository`, one lesson id at a time; a lesson that ships no dialogue can never
  gate the path. An answer may be **spoken or typed**: the recogniser needs a downloaded model and a
  working microphone, and a learner without either used to be locked out of the conversation — and so
  out of the path — so the keyboard is always available next to the mic, and both routes are graded by
  the same `ConversationScorer` verdict. Typing is a second door to the same requirement, not a lower
  one.
- **A lesson is eight steps, not one page (v60).** `LearningStage` (pure, `domain/model`) fixes the
  authored order — context → grammar → vocabulary → practice → listening → writing → speaking →
  review — and `LessonStagePlan.of` derives the steps a lesson actually ships from the content that
  already exists: **no new JSON field**, so every shipped lesson gets the guided flow. A step the
  lesson has no material for is omitted rather than shown empty (no grammar tip → no GRAMMAR step;
  no dialogue and no inline speaking item → no SPEAKING step). `LearningStage.of` is the one mapping
  both the plan and the session use: `listening` is its own step, `sentence_production` is writing,
  a `speaking` item is speaking, and everything else is controlled practice. `id` is the stable
  string that is persisted — the enum name is never storage, so renaming a constant cannot move a
  learner's place.
- **Completion is per step, and a skip is not mastery.** A teaching step (context, grammar,
  vocabulary, review) ends on the learner's Continue; a step that carries exercises ends only when
  every one of them has been answered. Only `LISTENING` is optional
  (`LearningStage.required = false`) — a device with no English TTS voice cannot do it — and
  skipping writes `LessonStageProgress.skipped`, never `completed`, so
  `LessonStageState.percent` (completed **required** steps over required steps) cannot be inflated
  by a skip. Progress is persisted per lesson in `lesson_state.stage` / `completedStages` /
  `skippedStages` (comma-joined ids, empty by default) plus `stageCompletedAt` (Phase 2, Room v9),
  so an interrupted lesson resumes where it stopped and knows when each finished step was finished.
- **Resume is derived, not guessed.** On open the session rebuilds the plan from the lesson's own
  content, restores the completed/skipped sets, and opens the saved step **only while it still
  exists in this plan and is not already done** — otherwise the first step. Inside that step the
  cursor comes from the recorded answers, not from the saved index: the first question with no
  answer is the one shown (Phase 2, above). A focused drill (B-1) short-circuits all of it and opens
  on the exercises the engine picked. Progress comes from stage status alone, so merely opening a
  step moves nothing.
- **The learner model is per knowledge item, not per exercise or per skill.** Every graded answer
  is attributed to the curriculum nodes its lesson teaches (`KnowledgeEvidence`, a pure tested rule)
  and written to `knowledge_state` with an atomic read-modify-write, exactly like skill mastery.
  That write, the day's claim, the skill meter and the answer's history are **one transaction**
  (checklist 0.1): an answer either lands whole or not at all, so a crash mid-answer cannot credit
  knowledge the history has no answer for.
  `DefaultKnowledgeEngine` reuses `Sm2ReviewScheduler` for intervals/ease and `MasteryEngine` for
  mastery, so "known" means one thing everywhere. Surfaced on the Progress screen.
- **A review grade is graph evidence too (review→graph pass).** `SubmitReviewUseCase` writes the
  reviewed word's node in the card's own transaction (`KnowledgeRepository.recordRecall`), attributed
  by the same `KnowledgeEvidence` rule a lesson answer uses, so a vocabulary card and a vocabulary
  exercise can never disagree about which node they prove. Before this the graph was blind to the
  practice a mature learner spends most time on: cards moved the skill meters while every node stayed
  at the mastery of its last lesson answer. Two deliberate limits: **no exercise format** is recorded
  (a card is recall, not a *transfer* claim — see the entry in [`docs/debt.md`](debt.md)), and the
  queue itself is still the card queue: a node's own exercises are not yet re-served from its due
  date, which is the other half of the roadmap item.
- **One bar for «مسلط» (v30 mastery gate).** A first correct answer seeds mastery at
  `DefaultKnowledgeEngine.FIRST_HIT_MASTERY` (0.4), not 1.0 — one tap is evidence, not proof, and
  seeding it at full made a just-met node look learned to the planner and to the skill meters.
  `mastery >= STRONG_MASTERY` (0.85) is a **single constant** in `MasteryEngine.kt`, used by the
  node gate, the planner and the exercise selector, because the interval-only rule used to call a
  node mastered while the planner — deciding on `mastery >= 0.85` — still treated it as weak.
- **Two named gates, defined once (checklist 2.1, Room v7).** `KnowledgeGates` (in
  `KnowledgeEngine.kt`, mirrored by `DefaultKnowledgeEngine.isSecured` / `isLongTermMastered`) owns
  the two questions surfaces ask, so no screen compares floats of its own:
  - **`isSecured`** — the **planner's bar** (checklist 3, chapter 1). All four must hold:
    `mastery >= STRONG_MASTERY`; `consecutiveCorrect >= MIN_CONSECUTIVE` (3, so a miss voids it);
    `distinctTypeCount >= MIN_FORMATS_TO_SECURE` (2, one shape can be pattern-matched); and
    `lastReviewedAt - firstCorrectAt >= MIN_RETEST_SPAN_MS` (20 h — the node survived **a delayed
    retest**, not just a same-sitting streak). The first version of this gate only asked for
    `mastery >= 0.85` plus a recorded hit, and that last condition was satisfied after the first
    answer: EWMA 0.2 from 0.4 crosses 0.85 after eight quick hits from a single format, on one day,
    so the plan marched on past a node nothing had retested. A secured node needs no more practice
    today, which is what lets the plan open the next unlocked unit — so it is deliberately **not**
    the 30-day gate.
  - **`isLongTermMastered`** — the old 30-day gate, renamed: `isSecured` plus
    `repetitions >= MASTERED_MIN_REPETITIONS` (3) and an interval past
    `Sm2ReviewScheduler.MASTERED_INTERVAL_DAYS` (30). The format and streak rules are inside
    `isSecured`; repeating them here would let the two gates disagree about what "confirmed" means.
    Because securing already needs a second format, the two `phonology.*` nodes the content only
    exercises through translation (`phonology.spelling-aloud`, `phonology.number-sounds`) cannot
    reach either claim today — a content gap, recorded rather than papered over.
- **A strong node that is not secured is a retest, not a re-drill.** The planner keeps one clock
  parameter (`plan(..., now)`) for exactly this decision: a node at `STRONG_MASTERY` with a recorded
  hit but not secured
  - within `MIN_RETEST_SPAN_MS` of its last answer gets **no action today** — asking for more would
    re-run the drill the learner just passed (`KnowledgeGates.hasWaitedForRetest`);
  - after that gap gets a `LearningActionType.RETEST` action (priority between `REMEDIATE` and
    `LEARN`, `RETEST_MINUTES` = 3, reason «بازآزمایی برای تثبیت»), which opens a focused session on
    that node: `HomeScreen` routes it through the same `onOpenFocus(lessonId, itemId)` seam as the
    weak-spot card, and `GetFocusPlanUseCase` serves a strong-but-unsecured node **only when the
    plan names it explicitly** (auto-pick still never picks a consolidated node). A strong number
    with **no** recorded answer is not "awaiting" anything: it keeps its `PRACTISE` slot.
- **The learner model records how the claim was earned (Room v7).** `knowledge_state` now carries
  `firstCorrectAt` (when the node was first answered right, `NULL` until then) and
  `distinctTypesCorrect`, a **bitmask** of the `ExerciseFormat`s that have produced a correct answer
  — `KnowledgeState.distinctTypeCount` reads its population, so one format answered five times is
  still one confirmation. The format travels from the exercise (`AnswerChecker.formatOf`) through
  `KnowledgeRepository.recordAttempt` into `DefaultKnowledgeEngine.applyAttempt`. Migration 6→7 only
  adds the two columns and invents no dates: a legacy row keeps `firstCorrectAt` NULL and heals on
  its next correct answer, while its mask starts filling from that point.
- **A lesson's own gate (v30): first-pass accuracy.** The summary calls a lesson «تثبیت‌شده» only at
  `LessonUiState.CONSOLIDATED_PERCENT` (80) or better **on the first try at each exercise**, else
  «نیاز به مرور»; a miss the learner fixed on the repeat is progress but not the same as knowing it
  the first time. The repeat itself is the same question in a **new layout**
  (`shuffledForDisplay(variant = 1)`, word-bank included), because the order the learner just got
  wrong is the order they would answer from memory.
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
  correct answer (mastery seeded at `FIRST_HIT_MASTERY`, 0.4) consolidated. `GetSkillProfilesUseCase` returns
  `emptyList()` when no content is seeded, and Home + Progress both render the shared
  `SkillTrackProgress` composable (Home compact), so the two screens cannot disagree. Node
  accounting is not a clean partition and the tests say so: of the **233** nodes, GRAMMAR claims 49,
  VOCABULARY **159** and READING 16, four nodes belong to two tracks
  (`grammar.passive-simple`, `discourse.reason-cause`, `discourse.narrative-tenses`,
  `vocab.idioms-and-figurative`) and 13 belong to none (a node that only trains LISTENING or
  PHONOLOGY). The arithmetic is checked against the bundle: 49 + 159 + 16 = 224 claims, the four
  double-claimed nodes take it to 220 distinct, and 233 − 220 = 13.
  **What the tracks can actually collect is a content fact, measured in
  [`review-v64.md`](review-v64.md):** READING is reachable in 43 of 192 lessons (a passage in 36) and
  WRITING only through the 156 `sentence_production` items — so those two bars move slower than
  GRAMMAR and VOCABULARY on the same effort, and the dome is honest about it rather than mis-wired.
- **The error taxonomy answers a different question than the tracks (roadmap).** A track says how
  much is known; `MissTaxonomy` (pure, in `domain/engine`) says what the answers that went wrong
  were *about*: `VOCABULARY`, `GRAMMAR`, `COMPREHENSION` (reading + listening) and `PRODUCTION`
  (writing + speaking items — building a sentence is not knowing a rule). The category comes from
  the exercise's own authored skill (`AnswerChecker.skillOf`), never from a guess about why the
  learner picked an answer, which the app cannot observe. `GetMissProfileUseCase` joins the recent
  answer log to the authored exercises by id and folds it into `MissProfile`; Progress renders the
  mix and names the category with the **most misses** (count, not rate: one miss out of two is not
  a pattern), only once the window holds `MIN_ATTEMPTS = 5` answers. Content the bundle no longer
  ships is skipped rather than classified. **Review cards are classified too**, by the item's own
  content type: a card can say vocabulary or grammar — the only two things a recall card can train —
  and never comprehension or production. GOOD and EASY are hits, HARD and AGAIN are not (the same
  `ReviewResult.isSuccess` rule the tracks read), and a card whose item has left the queue is dropped
  rather than guessed, so a review-only week keeps the mix moving instead of freezing it at the first
  lesson. Both sources are pooled and then trimmed to the one window, so neither can crowd the other
  out. What is left of the roadmap item is the *adaptive* half: nothing yet steers what comes next
  from this mix (the plan still steers from the graph).
