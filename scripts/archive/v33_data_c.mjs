// Conversation dialogues, C1 and C2: precise, hedged, professional English.
//
// Shape: [lessonId, sceneFa, partnerFa, turns[]]
// turn:  [question, questionFa, taskFa, answers[], tipFa]

export const cLevels = [
  ["c1.academic.lesson-01", "دفتر استاد، بازخورد به مقاله", "استاد", [
    ["Your introduction is clear, but the claim is too strong. Can you qualify it?", "مقدمه‌ات روشن است، اما ادعا بیش از حد قاطع است. می‌توانی آن را محدود کنی؟", "ادعا را با قید احتیاط بازنویسی کن.", ["I could say the findings suggest a link rather than prove one.", "Perhaps: 'the data indicate a tendency, though the sample is limited'."], "در نوشتار آکادمیک از suggest، indicate و tend to استفاده کن، نه prove."],
    ["Good. What is the gap your study fills?", "خوب است. شکافی که پژوهش تو پر می‌کند چیست؟", "شکاف را در یک جمله بگو.", ["Most studies look at cities; my work examines smaller towns.", "The literature rarely covers this age group, which my study does."], "شکاف پژوهشی = چه چیزی هنوز بررسی نشده."],
    ["And how do you justify the method?", "و روش را چطور توجیه می‌کنی؟", "بگو روش برای این نوع داده مناسب است.", ["A qualitative approach suits the question, since the aim is meaning, not measurement.", "The method was chosen because it captures how participants explain their choices."], "justify + since/because دال را وصل می‌کند."],
    ["When can I see the revised section?", "بخش اصلاح‌شده را کِی ببینم؟", "بگو تا سه‌شنبه می‌فرستی.", ["I'll send it by Tuesday afternoon.", "By Tuesday, if I can recheck the sources."], "by + روز یعنی تا پایان آن روز."],
  ]],

  ["c1.persuasion.lesson-01", "جلسهٔ تصمیم‌گیری شرکت", "همکار ارشد", [
    ["You want to move the launch. Convince me it's necessary.", "می‌خواهی زمان عرضه را عقب بیندازی. مرا قانع کن که لازم است.", "دلیل اصلی را با شاهد بگو.", ["Testing uncovered two blocking bugs, so launching now would risk our reputation.", "The last release taught us that untested features cost more later."], "so نتیجه را صریح بیان می‌کند."],
    ["Delaying costs us the holiday season. Surely that's worse?", "تعویق، فصل تعطیلات را از ما می‌گیرد. قطعاً بدتر نیست؟", "هزینه را بپذیر اما مقایسه کن.", ["The lost season is real, but a broken product would cost us the year.", "Granted, though a failed launch damages trust far longer than a delay."], "granted برای پذیرش بخشی از نقد است."],
    ["What would make you change your mind?", "چه چیزی نظرت را عوض می‌کند؟", "شرط مشخص بگذار.", ["If both bugs are fixed by Friday, I'd support the original date.", "Give me a tested build this week and I'll withdraw the request."], "if + شرط مشخص، موضع را انعطاف‌پذیر می‌کند."],
    ["All right. Let's review on Friday.", "بسیار خوب. جمعه بازبینی می‌کنیم.", "بپذیر و بگو چه چیزی ارائه می‌کنی.", ["Agreed. I'll bring the test report and a revised timeline.", "That's fair. I'll prepare the risk summary for Friday."], "agreed برای پایان مذاکره است."],
  ]],

  ["c1.culture.lesson-01", "خروج از سینما", "دوست علاقه‌مند به سینما", [
    ["So, what did you make of the ending?", "خب، پایان فیلم را چطور دیدی؟", "تفسیرت را با شاهد از فیلم بگو.", ["I read it as resignation: she stays because she's exhausted, not happy.", "The final shot suggests she chose silence over another argument."], "read ... as برای تفسیر است."],
    ["I thought it was hopeful. Doesn't the music change everything?", "من آن را امیدوارکننده دیدم. موسیقی همه‌چیز را عوض نمی‌کند؟", "بپذیر و ظرافت را اضافه کن.", ["The music softens it, certainly, but the dialogue stays unresolved.", "That's a good point; the score does pull against the images."], "certainly و indeed برای تأکید موافقتِ نسبی است."],
    ["Doesn't the director leave it open on purpose?", "کارگردان عمداً آن را باز نمی‌گذارد؟", "موافقت کن و بگو چرا مؤثر است.", ["Very likely, and that's what makes it stay with you afterwards.", "I'd say so. A closed ending would have been less honest."], "I'd say so برای نظر محتاطانه است."],
    ["Would you watch it again?", "دوباره می‌بینی‌اش؟", "بگو بله، برای دیدن جزئیات.", ["Definitely, though I'd watch it with subtitles next time.", "Yes. I suspect the early scenes set up more than I noticed."], "suspect برای حدس محتاطانه است."],
  ]],

  ["c1.phrasal-idioms.lesson-01", "دفتر کار بین‌المللی", "همکار انگلیسی‌زبان", [
    ["The client wants to push the deadline back again. How do you feel about that?", "مشتری می‌خواهد مهلت را دوباره عقب بیندازد. نظرت چیست؟", "بگو با این کار موافق نیستی و می‌خواهی محکم بایستی.", ["I think we should push back this time; we've already cut corners.", "Honestly, I'd rather hold our ground and renegotiate the scope."], "push back یعنی مقاومت کردن، نه عقب انداختن."],
    ["They might pull out if we refuse.", "اگر رد کنیم ممکن است کنار بکشند.", "بگو ریسک را می‌پذیری اما پیشنهاد جایگزین بده.", ["That's a risk, but we can offer a smaller first phase instead.", "Let's weigh it up: losing them once versus losing quality every time."], "weigh up یعنی سبک‌سنگین کردن."],
    ["Fair enough. Can you run the numbers?", "منصفانه است. می‌توانی محاسباتش را انجام دهی؟", "بگو تا فردا آماده می‌کنی.", ["I'll crunch the numbers and send them tonight.", "Yes, I'll put together a short cost breakdown by tomorrow."], "crunch the numbers یعنی محاسبهٔ دقیق."],
    ["And keep me in the loop, please.", "و مرا در جریان بگذار.", "بگو حتماً.", ["Of course, I'll loop you in on every email.", "Will do. I'll copy you on the thread."], "keep in the loop / loop in یعنی در جریان گذاشتن."],
  ]],

  ["c1.inference.lesson-01", "دفتر مدیر، خواندن یک ایمیل", "مدیر", [
    ["Read this email again. What is the client not saying?", "این ایمیل را دوباره بخوان. مشتری چه چیزی را نمی‌گوید؟", "بگو نارضایتی پنهان است.", ["They praise the design but repeat 'on time' three times.", "The tone is polite, yet the schedule complaints are pointed."], "yet برای تضاد میان دو بخش جمله است."],
    ["So what do they actually want?", "پس واقعاً چه می‌خواهند؟", "بگو تعهد زمانی می‌خواهند.", ["A firm date, and probably a penalty clause.", "They want reassurance more than changes to the product."], "probably برای استنتاج محتاطانه است."],
    ["How should we reply?", "چطور جواب بدهیم؟", "بگو با تعهد مشخص و شفاف.", ["We should confirm the date and explain what we've already fixed.", "Answer the concern directly, then offer a weekly update."], "address the concern یعنی به نگرانی پاسخ دادن."],
    ["Could you draft it before noon?", "می‌توانی پیش از ظهر پیش‌نویس کنی؟", "بگو تا ساعت یازده می‌فرستی.", ["I'll have a draft in your inbox by eleven.", "Yes, before eleven if the figures are confirmed."], "in your inbox = برایت ایمیل می‌کنم."],
  ]],

  ["c1.register.lesson-01", "شرکت، مکالمه با همکار", "همکار", [
    ["This is too formal for a chat message. Can you loosen it?", "این برای پیام چت خیلی رسمی است. می‌توانی راحت‌ترش کنی؟", "جملهٔ رسمی را غیررسمی بازنویسی کن.", ["Sure: 'Thanks, I'll get back to you tomorrow.'", "Change 'I shall revert' to 'I'll get back to you'."], "shall revert رسمی و کهنه است؛ get back to you طبیعی است."],
    ["Now make this one formal, for the regulator.", "حالا این یکی را رسمی کن، برای نهاد ناظر.", "جملهٔ محاوره‌ای را رسمی کن.", ["'We regret the delay and will provide an update within five working days.'", "Avoid contractions: 'we do not anticipate any further delay'."], "در نگارش رسمی از کوتاه‌نویسی پرهیز کن."],
    ["Why does register matter so much?", "چرا سبک زبانی این‌قدر مهم است؟", "بگو رابطه و فاصله را نشان می‌دهد.", ["It signals distance and respect, and it sets expectations.", "Using the wrong one can sound either cold or unprofessional."], "register یعنی سبک متناسب با موقعیت."],
    ["Which one should I use with this supplier?", "با این تأمین‌کننده کدام را به‌کار ببرم؟", "بگو نیمه‌رسمی و صریح.", ["Something in between: polite but direct.", "Semi-formal, since you've worked together for years."], "since دلیلِ انتخاب را می‌آورد."],
  ]],

  ["c1.cohesion.lesson-01", "ویرایش متن، میز کار", "ویراستار", [
    ["These paragraphs don't connect. What's missing?", "این پاراگراف‌ها به هم وصل نمی‌شوند. چه چیزی کم است؟", "بگو پیوند منطقی میان ایده‌ها نیست.", ["There's no signpost between the problem and the solution.", "The link between cause and consequence is implied, not stated."], "signpost یعنی نشانهٔ پیوند در متن."],
    ["How would you fix it?", "چطور درستش می‌کنی؟", "بگو با جملهٔ پیوندی شروع کن.", ["I'd add a sentence that carries the argument forward.", "Begin the paragraph with 'As a result', then keep the order."], "As a result از پیوندهای نتیجه است."],
    ["Isn't that repetitive?", "تکراری نیست؟", "بگو تکرار هدفمند است نه لفظی.", ["Not if the link adds meaning rather than repeating words.", "Repetition of the idea is fine; repetition of the wording isn't."], "not if برای رد یک نگرانی است."],
    ["Can you revise the whole section?", "می‌توانی کل بخش را بازنگری کنی؟", "بگو بله و زمان بده.", ["Yes, I'll restructure it and send it this evening.", "I can, though I'd like to keep the original examples."], "though در پایان جمله برای تناقض ملایم است."],
  ]],

  ["c2.nuance.lesson-01", "ویرایش یک متن حقوقی", "ویراستار ارشد", [
    ["'The company may terminate the agreement.' Is that precise enough?", "«شرکت می‌تواند قرارداد را فسخ کند.» به‌اندازهٔ کافی دقیق است؟", "بگو مبهم است و پیشنهاد دقیق بده.", ["'May' leaves discretion open; 'is entitled to' is tighter here.", "It's ambiguous. 'At its sole discretion' would say who decides."], "may ابهام اختیار دارد؛ is entitled to حق را روشن می‌کند."],
    ["Would 'shall' be better?", "shall بهتر نیست؟", "بگو بستگی به تعهد یا اختیار دارد.", ["Only if it's an obligation; here it's a right, so 'may' fits.", "'Shall' imposes a duty, which is not what this clause means."], "shall تعهد می‌سازد، نه اختیار."],
    ["And 'reasonable efforts'?", "و «تلاش معقول»؟", "بگو قابل تفسیر است و معیار می‌خواهد.", ["It invites argument unless the standard is defined.", "Vague. 'Commercially reasonable efforts' needs a defined standard."], "تعریف معیار، ابهام را کم می‌کند."],
    ["Let's use that wording throughout.", "از این عبارت در همه‌جا استفاده کنیم.", "بگو یکدست می‌کنی و فهرست تغییرات می‌دهی.", ["Agreed; I'll standardize it and log every change.", "I'll apply it consistently and send a change list."], "consistently یعنی به‌طور یکدست."],
  ]],

  ["c2.rhetoric.lesson-01", "دفتر استاد ادبیات", "استاد ادبیات", [
    ["Read this paragraph aloud. What does the rhythm do?", "این پاراگراف را بلند بخوان. ریتم چه کاری می‌کند؟", "بگو ریتم معنا را تقویت می‌کند.", ["The short final sentence lands like a verdict.", "The cadence slows at the end, which makes the claim feel final."], "cadence یعنی آهنگ جمله."],
    ["Is that manipulation or craft?", "این دستکاری است یا هنر؟", "تفکیک کن: ابزار در خدمت معنا یا جای آن.", ["Craft, when the form serves the argument rather than hiding it.", "It becomes manipulation when rhythm substitutes for evidence."], "when برای تعریف مرز است."],
    ["How would you rewrite it plainly?", "ساده‌اش را چطور می‌نویسی؟", "بازنویسی ساده ارائه کن.", ["'The policy failed, and the numbers show it.'", "Strip the metaphors: state the finding, then the data."], "plain یعنی بی‌آرایه و روشن."],
    ["Which version would you publish?", "کدام نسخه را منتشر می‌کنی؟", "بگو نسخهٔ میانه را، با دلیل.", ["The middle one: enough rhythm to be read, enough plainness to be trusted.", "I'd keep one image and drop the rest."], "میانه‌روی در بلاغت: یک تصویر، نه بیشتر."],
  ]],

  ["c2.science.lesson-01", "همایش علمی، پس از ارائه", "پژوهشگر همکار", [
    ["Your result contradicts the 2019 study. How do you explain that?", "نتیجهٔ شما با مطالعهٔ ۲۰۱۹ تناقض دارد. چطور توضیحش می‌دهید؟", "تفاوت روش را دلیل احتمالی بگو.", ["Our sample differs in age, which may explain the divergence.", "The two studies measure different things, so the conflict may be apparent."], "may explain برای احتمال علمی است."],
    ["Could your measurement be the problem?", "ممکن است مشکل از اندازه‌گیری شما باشد؟", "بپذیر و بگو چه بررسی کردی.", ["We ran a control to check that, and the pattern held.", "It's possible; we repeated the measurement three times."], "run a control یعنی گروه کنترل اجرا کردن."],
    ["What would falsify your claim?", "چه چیزی ادعای شما را رد می‌کند؟", "معیار رد را شفاف بگو.", ["A larger trial showing no effect would falsify it.", "If the effect disappears once we control for diet, my claim fails."], "falsify یعنی ابطال‌پذیر کردن."],
    ["Will you publish the negative results too?", "نتایج منفی را هم منتشر می‌کنید؟", "بگو بله، در پیوست.", ["Yes, in the supplementary material.", "We intend to, because selective reporting misleads."], "negative results نتایج بی‌اثر است."],
  ]],

  ["c2.literature.lesson-01", "کلاس ادبیات، بحث دربارهٔ یک رمان", "استاد", [
    ["What does the silence of the narrator achieve?", "سکوت راوی چه کاری می‌کند؟", "بگو فاصله و ابهام می‌سازد.", ["It gives the reader room to suspect what she cannot say.", "The gaps work as a form of testimony by omission."], "by omission یعنی با حذف."],
    ["Isn't that just vagueness?", "این فقط ابهام نیست؟", "تفکیک کن: ابهام عامدانه.", ["Deliberate, I'd argue: the gaps fall exactly where the trauma is.", "There's a difference between confusion and designed silence."], "deliberate یعنی عامدانه."],
    ["How does the ending change your reading?", "پایان، خوانش تو را چطور عوض می‌کند؟", "بگو پایان، خوانش اولیه را معکوس می‌کند.", ["It reframes everything: what looked like acceptance was resistance.", "The last line undermines the sympathy the novel built."], "reframe یعنی بازقالب‌بندی معنا."],
    ["Would you teach this book?", "این کتاب را درس می‌دهی؟", "بگو بله، با هشدار و زمینه.", ["Yes, with historical context and a warning about its violence.", "I would, though not to a first-year class."], "though برای محدود کردن پاسخ است."],
  ]],

  ["c2.argumentation.lesson-01", "مناظرهٔ تلویزیونی", "همتا در مناظره", [
    ["You claim the policy reduced poverty. Isn't that correlation only?", "شما می‌گویید سیاست فقر را کم کرد. این فقط همبستگی نیست؟", "بپذیر و شاهد اضافه کن.", ["Partly, though the timing and the control group make coincidence unlikely.", "It's correlation, and I'll add the natural experiment that supports causation."], "natural experiment یعنی آزمایش طبیعی."],
    ["Your opponent says the data was cherry-picked.", "رقیب شما می‌گوید داده‌ها گزینشی انتخاب شده‌اند.", "پاسخ بده، نه حمله.", ["All series are published; the criticism doesn't survive a look at the appendix.", "That's a strong claim without evidence, so let me show the full set."], "survive a look یعنی با بررسی رد می‌شود."],
    ["Define 'poverty' as you use it.", "«فقر» را چنان‌که به‌کار می‌برید تعریف کنید.", "تعریف عملیاتی بده.", ["Income below the national median, measured yearly.", "I mean relative poverty: income under sixty percent of the median."], "تعریف عملیاتی، مناظره را از کلی‌گویی نجات می‌دهد."],
    ["Concede one point.", "یک نکته را بپذیرید.", "یک ضعف واقعی را بپذیر.", ["The rollout in rural areas was too slow; that's a fair criticism.", "The design ignored part-time workers, and we should correct that."], "concede یعنی امتیاز دادن در بحث."],
  ]],

  ["c2.collocation.lesson-01", "ویرایش یک گزارش انگلیسی", "ویراستار", [
    ["'We made a big mistake in the decision.' Fix the collocation.", "«We made a big mistake in the decision.» همنشینی را درست کن.", "همنشینی طبیعی را بگو.", ["'We made a serious error in the decision-making process.'", "'We made a grave mistake when we decided.'"], "mistake با serious یا grave می‌آید، نه big در نگارش رسمی."],
    ["And 'very important problem'?", "و «very important problem»؟", "جایگزین طبیعی بده.", ["'A critical issue' or 'a pressing problem'.", "'A serious problem' is the natural choice."], "issue و problem با critical و pressing می‌آیند."],
    ["Why not just use strong adjectives everywhere?", "چرا همه‌جا صفت قوی به‌کار نبریم؟", "بگو تأکید مداوم اثرش را از دست می‌دهد.", ["Constant intensity flattens emphasis; the strong word stops standing out.", "If everything is critical, nothing is."], "intensity یعنی شدت."],
    ["Check the rest of the report for me?", "بقیهٔ گزارش را هم بررسی می‌کنی؟", "بگو بررسی می‌کنی و موارد را علامت می‌زنی.", ["I'll flag unnatural combinations in the margin.", "Yes, I'll mark each collocation that needs changing."], "flag یعنی علامت‌گذاری برای اصلاح."],
  ]],

  ["c2.irony.lesson-01", "کافه، بعد از یک جلسهٔ سخت", "دوست طناز", [
    ["So the meeting went well, did it?", "پس جلسه خوب پیش رفت، نه؟", "کنایه را بگیر و جواب طنزآمیز بده.", ["Oh, brilliantly. We only disagreed for two hours.", "Wonderful. We agreed to disagree, twice."], "کنایه با تناقض میان لحن و واقعیت ساخته می‌شود."],
    ["What actually happened?", "واقعاً چه شد؟", "واقعیت را صریح بگو.", ["No decision, another meeting on Thursday.", "We circled the same argument and postponed everything."], "circle an argument یعنی دور یک بحث چرخیدن."],
    ["Why do people say 'with respect' before being rude?", "چرا مردم قبل از بی‌احترامی می‌گویند «با احترام»؟", "توضیح بده: نرم‌کنندهٔ صوری است.", ["It's a cushion: the words claim respect while the tone withdraws it.", "Politeness as a warning shot, I think."], "cushion یعنی ضربه‌گیر."],
    ["Should I take that phrase seriously?", "باید آن عبارت را جدی بگیرم؟", "بگو بستگی به لحن دارد.", ["Listen to the tone: 'with respect' often precedes the disagreement.", "Sometimes. Usually it signals that what follows isn't respectful."], "often و usually برای تعمیم محتاطانه است."],
  ]],
];
