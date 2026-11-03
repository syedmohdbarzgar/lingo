#!/usr/bin/env node
/**
 * Bundle v33, second pass — answers the recogniser can actually hear.
 *
 * `SpeechVocabularyTest` holds every spoken answer to what a small English model
 * can recognise: no personal names, no digits, no letter-by-letter spelling. The
 * first pass of the v33 dialogues taught the right exchanges but wrote answers
 * like "It's Karimi, K-A-R-I-M-I" and "0912 345 6789" — a turn that can never be
 * graded CORRECT however well it is spoken.
 *
 * This rewrites those answers in place (same dialogues, same count), so the runner
 * can be applied again. Applied once, then archived: A-12 — never re-run it.
 */

import fs from "node:fs";
import { dirname, join } from "node:path";
import { fileURLToPath } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
const dataFile = (name) => join(here, name);

/** [file, exact text to replace, replacement] */
const EDITS = [
  // --- A1: alphabet — a letter on its own is not a word the model hears -------
  ["v33_data_a1_low.mjs",
    '["Let\'s start with the alphabet. Which letter comes after B?", "با الفبا شروع کنیم. بعد از B کدام حرف می‌آید؟", "حرف بعدی را بگو.", ["C", "The letter C"], "حرف را تنها و واضح بگو؛ جملهٔ کامل لازم نیست."]',
    '["Let\'s start with the alphabet. Can you say a word that begins with \'b\'?", "با الفبا شروع کنیم. کلمه‌ای می‌گویی که با \'b\' شروع شود؟", "یک کلمه که با b شروع می‌شود بگو.", ["Book.", "Banana."], "کلمه را تنها و واضح بگو؛ جملهٔ کامل لازم نیست."]'],
  ["v33_data_a1_low.mjs",
    '["Good. Can you say the first five letters?", "خوب است. پنج حرف اول را می‌توانی بگویی؟", "پنج حرف اول الفبا را به ترتیب بگو.", ["A, B, C, D, E", "A B C D E"], "حروف را پشت سر هم و با مکث کوتاه بگو."]',
    '["Good. How many letters are there in the alphabet?", "خوب است. الفبای انگلیسی چند حرف دارد؟", "تعداد حروف الفبا را بگو.", ["Twenty-six letters.", "There are twenty-six."], "عدد را به‌صورت کلمه بگو، نه رقم."]'],
  ["v33_data_a1_low.mjs",
    '["Now let\'s spell a word. How do you spell \'book\'?", "حالا یک کلمه را هجی کنیم. book را چطور هجی می‌کنی؟", "کلمهٔ book را حرف‌به‌حرف بگو.", ["B-O-O-K", "B O O K"], "در انگلیسی هجی کردن یعنی گفتن نام هر حرف، نه صدای آن."]',
    '["Now let\'s spell a word. Does \'apple\' begin with a vowel?", "حالا یک کلمه را هجی کنیم. apple با حرف صدادار شروع می‌شود؟", "جواب بده و بگو با چه صدایی شروع می‌شود.", ["Yes, it does.", "Yes, it begins with \'a\'."], "صدادارهای انگلیسی a، e، i، o، u هستند."]'],
  ["v33_data_a1_low.mjs",
    '["Very good. Can you spell your first name for me?", "خیلی خوب. می‌توانی اسم کوچکت را برایم هجی کنی؟", "اسم کوچکت را هجی کن.", ["A-L-I", "My name is Ali, A-L-I"], "اسم را اول بگو، بعد حروفش را جدا."]',
    '["Very good. Is \'z\' near the end of the alphabet?", "خیلی خوب. حرف z نزدیک پایان الفباست؟", "جواب بده و بگو کجاست.", ["Yes, it\'s the last letter.", "Yes, it\'s at the end."], "z آخرین حرف الفبای انگلیسی است."]'],

  // --- A1: spelling desk -----------------------------------------------------
  ["v33_data_a1_low.mjs",
    '["Yes, I\'d like to join a beginner class.", "Yes, I want to register for an English class."]',
    '["Yes, I\'d like to join a beginner class.", "Yes, I want to register for a beginner class."]'],
  ["v33_data_a1_low.mjs",
    '["My last name is Karimi. K-A-R-I-M-I.", "It\'s Karimi, K-A-R-I-M-I."]',
    '["It\'s on the form, in block capitals.", "I wrote it on the form for you."]'],
  ["v33_data_a1_low.mjs",
    '["It\'s zero nine one two, three four five, six seven eight nine.", "My number is 0912 345 6789."]',
    '["It\'s on the form as well.", "The same number as on the form."]'],

  // --- A1: greetings / introductions ----------------------------------------
  ["v33_data_a1_low.mjs",
    '["Nice to meet you, Sara. I\'m Amir.", "I\'m Amir. Nice to meet you."]',
    '["Nice to meet you. I\'m in your class.", "I\'m the new student here. Nice to meet you."]'],
  ["v33_data_a1_low.mjs",
    '["Nice to meet you, Reza. I\'m Neda.", "Hello Reza, I\'m Neda. Nice to meet you."]',
    '["Nice to meet you. I\'m a friend of the host.", "Hello. I\'m a guest here too."]'],
  ["v33_data_a1_low.mjs",
    '["I\'m from Shiraz.", "I come from Shiraz, in the south of Iran."]',
    '["I\'m from the south of Iran.", "I come from the south of Iran."]'],
  ["v33_data_a1_low.mjs",
    '["Hi Sara, nice to meet you.", "Hello Sara. It\'s nice to meet you."]',
    '["Hi, nice to meet you.", "Hello, it\'s nice to meet you."]'],
  ["v33_data_a1_low.mjs",
    '["In the evening I read a book and practise English.", "I usually study English in the evening."]',
    '["In the evening I read a book and study.", "I usually study in the evening."]'],
  ["v33_data_a1_low.mjs",
    '["Why are you learning English?", "Why do you study English?"]',
    '["Why do you study every evening?", "Why do you want to learn this language?"]'],

  // --- A1: high half ---------------------------------------------------------
  ["v33_data_a1_high.mjs",
    '["He works in an engineering company.", "He works for a company in Tehran."]',
    '["He works in an engineering company.", "He works for a big company."]'],
  ["v33_data_a1_high.mjs",
    '["No, it isn\'t mine. It\'s Maryam\'s.", "It\'s not mine. It belongs to Maryam."]',
    '["No, it isn\'t mine. It might be hers.", "It\'s not mine. It belongs to someone else."]'],
  ["v33_data_a1_high.mjs",
    '["Her name is Mimi. She\'s small and white.", "It\'s called Mimi, and it\'s very small."]',
    '["She\'s small and white.", "It\'s a small white cat."]'],
  ["v33_data_a1_high.mjs",
    '["What\'s its name? Is it big or small?", "اسمش چیست؟ بزرگ است یا کوچک؟", "بگو اسمش میمی است و کوچک است."',
    '["What\'s it like? Is it big or small?", "چه شکلی است؟ بزرگ است یا کوچک؟", "بگو کوچک و سفید است."'],
  ["v33_data_a1_high.mjs",
    '["I want to go to Enghelab Square.", "How can I get to Enghelab Square?"]',
    '["I want to go to the main square.", "How can I get to the main square?"]'],
  ["v33_data_a1_high.mjs",
    '["Yes, that\'s Mahsa. She\'s in my class.", "That\'s Mahsa. She\'s our classmate."]',
    '["Yes, that\'s the girl from my class.", "That\'s my classmate."]'],
  ["v33_data_a1_high.mjs",
    '["I live in the centre of Tehran.", "I live in Tehran, near the main square."]',
    '["I live in the centre of the city.", "I live near the main square."]'],

  // --- A2 -------------------------------------------------------------------
  ["v33_data_a2.mjs",
    '["Gate B12 at eleven. Is the flight on time?", "Thank you. Is there any delay?"]',
    '["Gate twelve, at eleven. Is the flight on time?", "Thank you. Is there any delay?"]'],
  ["v33_data_a2.mjs",
    '["Yes, it\'s under Karimi. My number is 0912 345 6789.", "The name is Karimi, and my phone number is 0912 345 6789."]',
    '["Yes, it\'s under my family name.", "I\'ll write it on the form for you."]'],
  ["v33_data_a2.mjs",
    '["Yes, please. The booking is under Ahmadi.", "Yes, that\'s fine. The name is Ahmadi."]',
    '["Yes, please. The booking is under my family name.", "Yes, that\'s fine. Use my family name."]'],
  ["v33_data_a2.mjs",
    '["I\'d like a return ticket to Tehran, please.", "I need a flight to Tehran and back."]',
    '["I\'d like a return ticket to the capital, please.", "I need a flight there and back."]'],
  ["v33_data_a2.mjs",
    '["Yes, I have a reservation under Rezaei for two nights.", "Yes, it\'s under Rezaei."]',
    '["Yes, I have a reservation for two nights.", "Yes, the reservation is for two nights."]'],
  ["v33_data_a2.mjs",
    '["Where is the lift? And is there Wi-Fi in the room?", "Thanks. Do you have a Wi-Fi password?"]',
    '["Where is the lift? And is there internet in the room?", "Thanks. Is there internet in the room?"]'],
  ["v33_data_a2.mjs",
    '["I\'d like a ticket to Isfahan, please.", "One ticket to Isfahan, please."]',
    '["To the south, please, one ticket.", "I\'d like a ticket to the south."]'],
  ["v33_data_a2.mjs",
    '["Happy New Year! Thanks for inviting me.", "Happy New Year to you too! Thank you."]',
    '["Same to you. Thanks for inviting me.", "Thank you for inviting me. Same to you."]'],
  ["v33_data_a2.mjs",
    '["Could I speak to Mr Rezaei, please?", "Hello, is Mr Rezaei available?"]',
    '["Could I speak to the manager, please?", "Hello, is the manager available?"]'],
  ["v33_data_a2.mjs",
    '["This is Sara Ahmadi, about the new contract.", "It\'s Sara Ahmadi. I\'m calling about the contract."]',
    '["This is the supplier\'s assistant, about the new contract.", "It\'s about the new contract, from the supplier."]'],
  ["v33_data_a2.mjs",
    '["Yes, but let me give it again: 0912 345 6789.", "He does, but here it is again: 0912 345 6789."]',
    '["Yes, but let me give it again, slowly.", "He does, but let me repeat it."]'],
  ["v33_data_a2.mjs",
    '["I\'ll start with \'Dear Mr Brown\'.", "With a greeting: Dear Mr Brown,"]',
    '["I\'ll open with a formal greeting.", "With a greeting and the client\'s name."]'],

  // --- B2 / C2 --------------------------------------------------------------
  ["v33_data_b.mjs",
    '["Let\'s test two messages with a small audience.", "A simple A/B test on the headline would tell us most."]',
    '["Let\'s test two messages with a small audience.", "A simple test on the headline would help most."]'],
  ["v33_data_b.mjs",
    '["My order hasn\'t arrived, and it\'s been two weeks.", "I\'m calling about order 4471, which is still missing."]',
    '["My order hasn\'t arrived, and it\'s been two weeks.", "I\'m calling about an order that is still missing."]'],
  ["v33_data_c.mjs",
    '["It invites argument unless the standard is defined.", "Vague. \'Commercially reasonable efforts, as set out in Schedule 2\' fixes it."]',
    '["It invites argument unless the standard is defined.", "Vague. \'Commercially reasonable efforts\' needs a defined standard."]'],
];

let applied = 0;
for (const [file, from, to] of EDITS) {
  const path = dataFile(file);
  const text = fs.readFileSync(path, "utf8");
  // Re-runnable: an edit already applied is simply skipped.
  if (text.includes(to)) continue;
  if (!text.includes(from)) {
    console.error(`v33 fixes: not found in ${file}:\n  ${from.slice(0, 90)}…`);
    process.exit(1);
  }
  fs.writeFileSync(path, text.replace(from, to));
  applied += 1;
}
console.log(`v33 fixes: ${applied} answers rewritten for the recogniser`);
