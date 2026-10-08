// A-7: the missing A1 foundations — the alphabet and letter names, spelling your
// name, and numbers 0–20. Until now the first lesson was "Greetings", so a
// complete beginner met English words before ever meeting the letters.
//
//   node scripts/p1_foundation_lessons.mjs
//
// Adds three lessons at the front (orders 1–3, every existing lesson shifts by
// three), their vocabulary, exercises (with explanations), and knowledge items —
// two VOCABULARY nodes and one PHONOLOGY node per lesson — then makes "Greetings"
// depend on the alphabet. Bumps contentVersion 14 → 15 in all five bundle files.
// Idempotent: re-running after it was applied aborts with a message.

import { readFileSync, writeFileSync } from "node:fs";

const dir = "app/src/main/assets/content";
const read = (f) => JSON.parse(readFileSync(`${dir}/${f}`, "utf8"));
const write = (f, o) => writeFileSync(`${dir}/${f}`, JSON.stringify(o, null, 2) + "\n");

// --------------------------------------------------------------- lessons

const tipAlphabet = [
  "## قاعده",
  "انگلیسی ۲۶ حرف دارد: از A تا Z. هر حرف دو شکل دارد — حرف بزرگ (capital) در آغاز نام‌ها و جمله‌ها، و حرف کوچک (small) در بقیهٔ جاها. پنج حرف صدادار (vowel) داریم: A, E, I, O, U؛ بقیه بی‌صدا (consonant) هستند.",
  "## جدول",
  "A B C D E F G",
  "H I J K L M N",
  "O P Q R S T U",
  "V W X Y Z",
  "حروف صدادار: A, E, I, O, U",
  "## مثال‌ها",
  "My name starts with a capital letter. — نام من با حرف بزرگ شروع می‌شود.",
  "The letter A is the first letter. — حرف A اولین حرف است.",
  "E is a vowel. — E یک حرف صدادار است.",
  "## خطاهای رایج",
  "شکل بزرگ و کوچک را قاطی نکنید: در آغاز جمله و نام همیشه حرف بزرگ می‌آید: Sara نه sara.",
  "نام حرف را با صدایش اشتباه نگیرید: نام حرف B «بی» است، اما در واژه صدای /b/ می‌دهد.",
  "## تلفظ",
  "نامِ حروف را جدا از صدایشان یاد بگیرید: B «بی» (/biː/) است ولی در واژه صدای /b/ می‌دهد.",
  "واکهٔ نامِ حروف بلند است: A /eɪ/, E /iː/, I /aɪ/, O /oʊ/, U /juː/.",
  "حروف صدادار در واژه‌ها صداهای متفاوتی می‌سازند؛ به تفاوت a در cat و cake گوش کنید.",
].join("\n");

const tipSpelling = [
  "## قاعده",
  "هجی‌کردن (spelling) یعنی گفتن حروف یک واژه به ترتیب، مثلاً S-A-R-A. برای پرسیدن از How do you spell …? استفاده کن. حرف تکراری را با double می‌گوییم: L-L می‌شود double L.",
  "## جدول",
  "How do you spell your name? — نامت را چطور هجی می‌کنی؟",
  "S-A-R-A — اِس-اِی-آر-اِی",
  "double L — دو تا L",
  "## مثال‌ها",
  "Can you spell your surname, please? — می‌توانی نام خانوادگی‌ات را هجی کنی؟",
  "My name has a double L. — نام من دو تا L دارد.",
  "Write your email in small letters. — ایمیلت را با حروف کوچک بنویس.",
  "## خطاهای رایج",
  "در ایمیل و نشانی اینترنتی همه‌چیز را با حرف کوچک بنویسید.",
  "حرف‌های تکراری را جا نیندازید: address دو تا d و دو تا s دارد.",
  "## تلفظ",
  "نام حروف در هجی‌کردن بلند و روشن گفته می‌شود؛ W را «double U» (/ˈdʌbljuː/) می‌گوییم.",
  "H /eɪtʃ/ را با نام‌های کوتاه اشتباه نگیرید؛ کشش واکهٔ آخرش را نگه دارید.",
  "در هجی‌کردن بین حروف مکث کنید تا شنونده آن‌ها را جدا بشنود.",
].join("\n");

const tipNumbers = [
  "## قاعده",
  "عددهای ۰ تا ۱۲ واژهٔ مستقل دارند و از ۱۳ تا ۱۹ با پسوند -teen ساخته می‌شوند (thirteen, fourteen …). دهگان‌ها پسوند -ty دارند (twenty, thirty …). عدد را هم با رقم (digits) و هم با واژه (words) می‌نویسیم.",
  "## جدول",
  "0 zero — 1 one — 2 two — 3 three — 4 four — 5 five",
  "6 six — 7 seven — 8 eight — 9 nine — 10 ten",
  "11 eleven — 12 twelve — 13 thirteen — 15 fifteen — 18 eighteen",
  "20 twenty — 30 thirty — 100 a hundred",
  "## مثال‌ها",
  "There are twelve months in a year. — سال دوازده ماه دارد.",
  "Three plus two is five. — سه به‌علاوهٔ دو می‌شود پنج.",
  "There are twenty students in the class. — بیست دانش‌آموز در کلاس هستند.",
  "## خطاهای رایج",
  "شکل حروفی عدد را درست بنویسید: eight نه eigth، و twelve نه twelwe.",
  "شکل واژه‌ای و رقمی را قاطی نکنید: در متن رسمی twenty students بهتر از 20 students است.",
  "## تلفظ",
  "teen و ty را جدا کنید: thirteen (تکیه روی -teen) با thirty (تکیه روی هجای اول) فرق دارد.",
  "three با /θ/ شروع می‌شود — زبان میان دندان‌ها؛ با tree (درخت) اشتباه نکنید.",
  "twelve و twenty هر دو /tw/ دارند و با واکهٔ کوتاه /e/ شروع می‌شوند.",
].join("\n");

const newLessons = [
  {
    id: "a1.alphabet.lesson-01",
    level: "A1",
    title: "The Alphabet",
    titleFa: "الفبای انگلیسی",
    topic: "Foundations",
    estimatedMinutes: 6,
    order: 1,
    grammarTipFa: tipAlphabet,
  },
  {
    id: "a1.spelling.lesson-01",
    level: "A1",
    title: "Spelling Your Name",
    titleFa: "هجی کردن نام",
    topic: "Foundations",
    estimatedMinutes: 6,
    order: 2,
    grammarTipFa: tipSpelling,
  },
  {
    id: "a1.numbers.lesson-01",
    level: "A1",
    title: "Numbers 0–20",
    titleFa: "اعداد صفر تا بیست",
    topic: "Foundations",
    estimatedMinutes: 6,
    order: 3,
    grammarTipFa: tipNumbers,
  },
];

// --------------------------------------------------------------- vocabulary

const word = (id, w, translation, definition, pronunciation, partOfSpeech, example, collocations, explanationFa, lessonId) => ({
  id,
  word: w,
  translation,
  definition,
  pronunciation,
  level: "A1",
  partOfSpeech,
  examples: [example],
  collocations,
  lessonId,
  explanationFa,
});

const L1 = "a1.alphabet.lesson-01";
const L2 = "a1.spelling.lesson-01";
const L3 = "a1.numbers.lesson-01";

const newVocabulary = [
  word("a1.alphabet.word.alphabet", "alphabet", "الفبا", "All the letters from A to Z", "/ˈælfəbet/", "noun",
    "The English alphabet has twenty-six letters.", ["learn the alphabet"],
    "کل ۲۶ حرف انگلیسی. برای شمردن حروف بگو letters، نه alphabets.", L1),
  word("a1.alphabet.word.letter", "letter", "حرف", "One symbol of the alphabet", "/ˈletər/", "noun",
    "The letter A is the first one.", ["capital letter", "letter sound"],
    "یک نماد از الفبا؛ شکل بزرگش capital letter و شکل کوچکش small letter است.", L1),
  word("a1.alphabet.word.capital-letter", "capital letter", "حرف بزرگ", "The big form of a letter", "/ˈkæpɪtl ˈletər/", "noun",
    "Every name starts with a capital letter.", ["start with a capital letter"],
    "شکل بزرگ حرف؛ در آغاز نام‌ها و جمله‌ها می‌آید: Sara نه sara.", L1),
  word("a1.alphabet.word.small-letter", "small letter", "حرف کوچک", "The usual, lowercase form of a letter", "/smɔːl ˈletər/", "noun",
    "Write your email address in small letters.", ["in small letters"],
    "شکل کوچک و رایج حرف؛ در ایمیل و نشانی اینترنتی همه را کوچک می‌نویسیم.", L1),
  word("a1.alphabet.word.vowel", "vowel", "حرف صدادار", "A, E, I, O and U", "/ˈvaʊəl/", "noun",
    "The letters A, E, I, O and U are vowels.", ["vowel sound"],
    "پنج حرف صدادار A, E, I, O, U؛ بقیهٔ حروف بی‌صدا (consonant) هستند.", L1),
  word("a1.alphabet.word.sound", "sound", "صدا", "What you hear when you say a letter", "/saʊnd/", "noun",
    "Every letter has its own sound.", ["letter sound", "the sound of a word"],
    "صدایی که می‌شنوی، جدا از نام حرف؛ نام B «بی» است ولی صدایش /b/ است.", L1),

  word("a1.spelling.word.spell", "spell", "هجی کردن", "To say the letters of a word in order", "/spel/", "verb",
    "Can you spell your name, please?", ["spell a word", "how do you spell"],
    "حرف‌های یک واژه را به ترتیب گفتن: S-A-R-A.", L2),
  word("a1.spelling.word.name", "name", "نام", "What someone or something is called", "/neɪm/", "noun",
    "My name is Sara.", ["first name", "full name"],
    "نام کوچک؛ نام خانوادگی surname یا family name است.", L2),
  word("a1.spelling.word.surname", "surname", "نام خانوادگی", "Your family name", "/ˈsɜːrneɪm/", "noun",
    "Her surname is Ahmadi.", ["family surname", "write your surname"],
    "نام خانوادگی؛ در گفتار محاوره‌ای last name و family name هم می‌گویند.", L2),
  word("a1.spelling.word.address", "address", "نشانی", "Where you live, or an email location", "/ˈædres/", "noun",
    "Please write your address here.", ["email address", "home address"],
    "نشانی خانه یا ایمیل؛ املایش دو d و دو s دارد.", L2),
  word("a1.spelling.word.double", "double", "دوتایی", "Two of the same letter or thing", "/ˈdʌbl/", "adjective",
    "My name has a double L.", ["double letter"],
    "حرف تکراری؛ در هجی‌کردن می‌گوییم double L یعنی دو تا L.", L2),
  word("a1.spelling.word.email", "email", "ایمیل", "A message sent over the internet", "/ˈiːmeɪl/", "noun",
    "Send me an email, please.", ["email address", "send an email"],
    "پیام اینترنتی؛ در نشانی ایمیل همه را با حرف کوچک بنویس.", L2),

  word("a1.numbers.word.zero", "zero", "صفر", "The number 0", "/ˈzɪəroʊ/", "number",
    "Zero comes before one.", ["below zero"],
    "عدد ۰؛ در شماره‌تلفن محاوره‌ای «o» هم گفته می‌شود.", L3),
  word("a1.numbers.word.one", "one", "یک", "The number 1", "/wʌn/", "number",
    "I have one brother.", ["one more"],
    "عدد ۱؛ با won («برنده شد») هم‌صدا است، پس به بافت جمله دقت کن.", L3),
  word("a1.numbers.word.two", "two", "دو", "The number 2", "/tuː/", "number",
    "Two plus two is four.", ["two of them"],
    "عدد ۲؛ با to و too هم‌صدا است.", L3),
  word("a1.numbers.word.three", "three", "سه", "The number 3", "/θriː/", "number",
    "There are three books on the desk.", ["three times"],
    "عدد ۳؛ با /θ/ گفته می‌شود و با tree (درخت) اشتباه نشود.", L3),
  word("a1.numbers.word.twelve", "twelve", "دوازده", "The number 12", "/twelv/", "number",
    "A year has twelve months.", ["twelve o'clock"],
    "عدد ۱۲؛ شبیه twenty نیست — twelve دوازده و twenty بیست است.", L3),
  word("a1.numbers.word.twenty", "twenty", "بیست", "The number 20", "/ˈtwenti/", "number",
    "There are twenty students in the class.", ["twenty years old"],
    "عدد ۲۰؛ تکیه روی هجای اول است و با thirty اشتباه نشود.", L3),
  word("a1.numbers.word.hundred", "hundred", "صد", "The number 100", "/ˈhʌndrəd/", "number",
    "A hundred is ten times ten.", ["a hundred percent"],
    "عدد ۱۰۰؛ جمعش hundreds است: hundreds of people.", L3),
  word("a1.numbers.word.number", "number", "عدد", "A word or symbol used for counting", "/ˈnʌmbər/", "noun",
    "What is your phone number?", ["phone number", "a big number"],
    "عدد یا شماره؛ حرف d در تلفظ خوانده نمی‌شود (/ˈnʌmbər/).", L3),
];

// --------------------------------------------------------------- exercises

let orderSeq = 0;
const ex = (o) => ({ ...o, order: ++orderSeq });
const mc = (id, lessonId, question, questionFa, options, correctIndex, skill, explanationFa) =>
  ({ id, lessonId, type: "multiple_choice", question, questionFa, options, correctIndex, skill, explanationFa });
const fill = (id, lessonId, sentence, accepted, explanationFa) =>
  ({ id, lessonId, type: "fill_blank", sentence, accepted, explanationFa });
const translation = (id, lessonId, prompt, accepted, bank, explanationFa) =>
  ({ id, lessonId, type: "translation", prompt, accepted, bank, explanationFa });
const listening = (id, lessonId, text, accepted, explanationFa) =>
  ({ id, lessonId, type: "listening", text, accepted, explanationFa });

const newExercises = [
  // --- alphabet ---------------------------------------------------------
  ex(mc("a1.alphabet.ex.01", L1,
    "How many letters are there in the English alphabet?",
    "الفبای انگلیسی چند حرف دارد؟",
    ["twenty-six", "twenty-four", "twenty-eight", "twenty-two"], 0, "VOCABULARY",
    "الفبای انگلیسی ۲۶ حرف دارد — از A تا Z.")),
  ex(mc("a1.alphabet.ex.02", L1,
    "Which letter comes after C?",
    "بعد از C کدام حرف می‌آید؟",
    ["D", "B", "E", "F"], 0, "VOCABULARY",
    "ترتیب الفبا A, B, C, D است؛ پس بعد از C حرف D می‌آید.")),
  ex(mc("a1.alphabet.ex.03", L1,
    "Which one is a capital letter?",
    "کدام‌یک حرف بزرگ است؟",
    ["A", "b", "d", "g"], 0, "VOCABULARY",
    "حرف بزرگ (capital) شکل بلند حرف است؛ b, d, g شکل کوچک‌اند.")),
  ex(mc("a1.alphabet.ex.04", L1,
    "Which letter is a vowel?",
    "کدام حرف صدادار است؟",
    ["E", "B", "C", "D"], 0, "VOCABULARY",
    "حروف صدادار A, E, I, O, U هستند؛ B, C, D بی‌صدا هستند.")),
  ex(translation("a1.alphabet.ex.05", L1,
    "حرف A اولین حرف الفباست.",
    ["a is the first letter", "a is the first letter of the alphabet", "the letter a is the first letter"],
    ["a", "is", "the", "first", "letter", "of", "alphabet", "second"],
    "برای اشاره به جایگاه یک حرف از the first letter استفاده کن؛ ترتیب جمله: A is the first letter.")),
  ex(listening("a1.alphabet.ex.06", L1,
    "The English alphabet has twenty-six letters.",
    ["the english alphabet has twenty six letters", "the english alphabet has 26 letters"],
    "به عدد twenty-six گوش کن و /s/ پایانی letters را بشنو — انتهای واژه‌ها در انگلیسی واضح‌تر از فارسی گفته می‌شود.")),
  ex(mc("a1.alphabet.ex.07", L1,
    'What does "vowel" mean?',
    "معنی «vowel» چیست؟",
    ["حرف صدادار", "حرف بزرگ", "حرف کوچک", "الفبا"], 0, "VOCABULARY",
    "vowel حرف صدادار است — A, E, I, O, U؛ حرف بزرگ capital letter است.")),
  ex(mc("a1.alphabet.ex.08", L1,
    "Which sentence is correct?",
    "کدام جمله درست است؟",
    ["The letter A is the first letter.", "Letter the A is first the letter.", "A the letter is first letter.", "First the letter is A letter."], 0, "GRAMMAR",
    "ترتیب جملهٔ انگلیسی ثابت است: نهاد + فعل + بقیه؛ «The letter A is the first letter» درست است و جابه‌جا کردن واژه‌ها جمله را بی‌معنی می‌کند.")),

  // --- spelling ---------------------------------------------------------
  ex(mc("a1.spelling.ex.01", L2,
    "Which spelling is correct?",
    "کدام املای درست است؟",
    ["morning", "mornin", "moorning", "mornning"], 0, "VOCABULARY",
    "واژهٔ morning دو n دارد و به g ختم می‌شود.")),
  ex(mc("a1.spelling.ex.02", L2,
    "Which word has a double letter?",
    "کدام واژه حرف تکراری دارد؟",
    ["letter", "name", "spell", "email"], 0, "VOCABULARY",
    "در letter حرف t دو بار آمده است؛ بقیهٔ واژه‌ها حرف تکراری ندارند.")),
  ex(fill("a1.spelling.ex.03", L2,
    "My ______ is Ahmadi.",
    ["surname", "family name", "last name"],
    "برای نام خانوادگی surname یا family name می‌گوییم؛ بعد از My شکل مفرد می‌آید.")),
  ex(translation("a1.spelling.ex.04", L2,
    "نامت را چطور هجی می‌کنی؟",
    ["how do you spell your name", "how do you spell your name please", "how do you spell your name?"],
    ["how", "do", "you", "spell", "your", "name", "what", "is"],
    "پرسش ثابت How do you spell …? است؛ بعد از do فعل لخت می‌آید.")),
  ex(listening("a1.spelling.ex.05", L2,
    "Can you spell your surname, please?",
    ["can you spell your surname please"],
    "به نام حروف داخل surname و مکث بین واژه‌ها گوش کن؛ هجی‌کردن حرف‌ها را جدا و روشن می‌گوید.")),
  ex(mc("a1.spelling.ex.06", L2,
    "Which spelling of the number 8 is correct?",
    "املای درست عدد ۸ کدام است؟",
    ["eight", "eigth", "eit", "eaght"], 0, "VOCABULARY",
    "عدد ۸ را eight می‌نویسیم: e-i-g-h-t؛ جای g و h را عوض نکن.")),
  ex(mc("a1.spelling.ex.07", L2,
    'What does "surname" mean?',
    "معنی «surname» چیست؟",
    ["نام خانوادگی", "نشانی", "هجی کردن", "دوتایی"], 0, "VOCABULARY",
    "surname یعنی نام خانوادگی؛ address نشانی و spell هجی کردن است.")),
  ex(fill("a1.spelling.ex.08", L2,
    "Please write your ______ address.",
    ["email", "e-mail"],
    "نشانی اینترنتی email address است و با حرف کوچک نوشته می‌شود.")),

  // --- numbers ----------------------------------------------------------
  ex(mc("a1.numbers.ex.01", L3,
    "Which number comes after nine?",
    "بعد از nine کدام عدد می‌آید؟",
    ["ten", "eleven", "twelve", "twenty"], 0, "VOCABULARY",
    "پس از nine عدد ten (۱۰) می‌آید؛ eleven یازده است.")),
  ex(mc("a1.numbers.ex.02", L3,
    "Which word is the number 20?",
    "کدام واژه عدد ۲۰ است؟",
    ["twenty", "twelve", "two", "ten"], 0, "VOCABULARY",
    "twenty بیست است؛ twelve دوازده و two دو است — شکل‌ها شبیه‌اند ولی معنی یکی نیست.")),
  ex(fill("a1.numbers.ex.03", L3,
    "There are ______ months in a year.",
    ["twelve", "12"],
    "سال دوازده ماه دارد؛ هم واژهٔ twelve و هم رقم 12 پذیرفته است.")),
  ex(fill("a1.numbers.ex.04", L3,
    "Three plus two is ______.",
    ["five", "5"],
    "سه به‌علاوهٔ دو پنج می‌شود؛ شکل واژه‌ای five است.")),
  ex(mc("a1.numbers.ex.05", L3,
    "How do you write the number 3 in words?",
    "عدد ۳ را با واژه چطور می‌نویسیم؟",
    ["three", "tree", "thre", "threa"], 0, "VOCABULARY",
    "عدد ۳ را three می‌نویسیم؛ tree به معنی درخت است.")),
  ex(translation("a1.numbers.ex.06", L3,
    "کلاس بیست دانش‌آموز دارد.",
    ["the class has twenty students", "there are twenty students in the class", "the classroom has twenty students"],
    ["the", "class", "has", "twenty", "students", "there", "are", "in"],
    "برای شمارش هم has و هم there are درست است؛ عدد پیش از اسم جمع می‌آید: twenty students.")),
  ex(mc("a1.numbers.ex.07", L3,
    'What does "hundred" mean?',
    "معنی «hundred» چیست؟",
    ["صد", "بیست", "دوازده", "صفر"], 0, "VOCABULARY",
    "hundred یعنی صد؛ twenty بیست، twelve دوازده و zero صفر است.")),
  ex(listening("a1.numbers.ex.08", L3,
    "There are twenty students in the class.",
    ["there are twenty students in the class"],
    "به /tw/ در آغاز twenty و تکیه روی هجای اولش گوش کن؛ با twelve اشتباه نکن.")),
];

// --------------------------------------------------------------- knowledge

const newKnowledge = [
  {
    id: "vocab.alphabet",
    type: "VOCABULARY",
    title: "The English alphabet and its letters",
    titleFa: "الفبای انگلیسی و حروف آن",
    level: "A1",
    prerequisites: [],
    lessons: [L1],
    skills: ["VOCABULARY", "LISTENING"],
  },
  {
    id: "vocab.spelling",
    type: "VOCABULARY",
    title: "Spelling names and addresses",
    titleFa: "هجی کردن نام و نشانی",
    level: "A1",
    prerequisites: ["vocab.alphabet"],
    lessons: [L2],
    skills: ["VOCABULARY", "WRITING"],
  },
  {
    id: "vocab.numbers",
    type: "VOCABULARY",
    title: "Numbers 0–20 and 100",
    titleFa: "اعداد ۰ تا ۲۰ و صد",
    level: "A1",
    prerequisites: [],
    lessons: [L3],
    skills: ["VOCABULARY", "LISTENING"],
  },
  // PHONOLOGY nodes: LISTENING-only evidence and never a prerequisite of anything
  // (PhonologyCoverageTest pins both rules).
  {
    id: "phonology.letter-names",
    type: "PHONOLOGY",
    title: "Letter names and vowel sounds",
    titleFa: "نام حروف و صداهای واکه‌ای",
    level: "A1",
    prerequisites: [],
    lessons: [L1],
    skills: ["LISTENING"],
  },
  {
    id: "phonology.spelling-aloud",
    type: "PHONOLOGY",
    title: "Saying letters aloud while spelling",
    titleFa: "گفتن حروف هنگام هجی کردن",
    level: "A1",
    prerequisites: [],
    lessons: [L2],
    skills: ["LISTENING"],
  },
  {
    id: "phonology.number-sounds",
    type: "PHONOLOGY",
    title: "The /θ/ in three and the -teen / -ty contrast",
    titleFa: "آوای /θ/ در three و تفاوت teen و ty",
    level: "A1",
    prerequisites: [],
    lessons: [L3],
    skills: ["LISTENING"],
  },
];

// --------------------------------------------------------------- apply

const lessons = read("lessons.json");
if (lessons.lessons.some((l) => l.id === "a1.alphabet.lesson-01")) {
  console.error("foundation lessons already applied — nothing to do");
  process.exit(1);
}

const vocabulary = read("vocabulary.json");
const exercises = read("exercises.json");
const knowledge = read("knowledge.json");

// Make room at the front: every existing lesson shifts up by the number added.
for (const lesson of lessons.lessons) lesson.order += newLessons.length;
lessons.lessons = [...newLessons, ...lessons.lessons];

vocabulary.vocabulary = [...vocabulary.vocabulary, ...newVocabulary];
// Append in lesson order so `order` stays contiguous inside each lesson.
exercises.exercises = [...exercises.exercises, ...newExercises];
knowledge.knowledge = [...knowledge.knowledge, ...newKnowledge];

// Greetings now sits behind the alphabet: you cannot greet in written English
// before you know the letters (checklist A-7).
const greetings = knowledge.knowledge.find((k) => k.id === "vocab.greetings");
if (!greetings) throw new Error("vocab.greetings not found");
greetings.prerequisites = ["vocab.alphabet", ...greetings.prerequisites.filter((p) => p !== "vocab.alphabet")];

write("lessons.json", lessons);
write("vocabulary.json", vocabulary);
write("exercises.json", exercises);
write("knowledge.json", knowledge);

const from = lessons.contentVersion;
for (const file of ["lessons.json", "vocabulary.json", "exercises.json", "placement.json", "knowledge.json"]) {
  const o = read(file);
  o.contentVersion = from + 1;
  write(file, o);
}

console.log(
  `added ${newLessons.length} foundation lessons, ${newVocabulary.length} words, ` +
    `${newExercises.length} exercises, ${newKnowledge.length} knowledge items; ` +
    `contentVersion ${from} -> ${from + 1}`,
);
