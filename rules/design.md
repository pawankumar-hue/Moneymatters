# MONEY MATTERS — DESIGN SYSTEM

> Complete design rules for the Money Matters app.
> Use this as a reference for all UI design, prompts, and implementation.

---

## 1. COLOR PALETTE

### 1.1 Sunrise (Light Mode — Default)

| Token | Hex | RGB | Use Case |
| :--- | :--- | :--- | :--- |
| `--bg-base` | `#FFFBF5` | 255, 251, 245 | Screen background |
| `--bg-elevated` | `#FFFFFF` | 255, 255, 255 | Cards, modals, sheets |
| `--bg-sunk` | `#F5EFE6` | 245, 239, 230 | Sunken sections, input backgrounds |
| `--text-primary` | `#1A1814` | 26, 24, 20 | Headlines, titles, primary text |
| `--text-secondary` | `#6B6457` | 107, 100, 87 | Body text, descriptions |
| `--text-muted` | `#9F968A` | 159, 150, 138 | Captions, hints, timestamps, placeholders |
| `--accent-primary` | `#FF6B35` | 255, 107, 53 | Primary CTA, buttons, links, streak |
| `--accent-soft` | `#FFE4D6` | 255, 228, 214 | Accent backgrounds, logo containers, tints |
| `--success` | `#2D8B5F` | 45, 139, 95 | Success states, savings, progress, wins |
| `--success-soft` | `#D4EDD9` | 212, 237, 217 | Success backgrounds, positive indicators |
| `--danger` | `#D63B3B` | 214, 59, 59 | Errors, warnings, broke alerts, scam |
| `--danger-soft` | `#FDE2E2` | 253, 226, 226 | Danger backgrounds, negative indicators |
| `--info` | `#2E5AAC` | 46, 90, 172 | Info, calculator, neutral actions |
| `--info-soft` | `#DCE7FA` | 220, 231, 250 | Info backgrounds |
| `--streak` | `#FF8C42` | 255, 140, 66 | Streak flame, progress indicators |
| `--gold` | `#F4B740` | 244, 183, 64 | XP, badges, achievements, premium |

### 1.2 Midnight (Dark Mode)

| Token | Hex | RGB | Use Case |
| :--- | :--- | :--- | :--- |
| `--bg-base` | `#15110D` | 21, 17, 13 | Screen background |
| `--bg-elevated` | `#1F1A14` | 31, 26, 20 | Cards, modals, sheets |
| `--bg-sunk` | `#0F0B08` | 15, 11, 8 | Deepest zones, bottom nav |
| `--text-primary` | `#FAF4EA` | 250, 244, 234 | Headlines, titles, primary text |
| `--text-secondary` | `#B8AFA3` | 184, 175, 163 | Body text, descriptions |
| `--text-muted` | `#7A7168` | 122, 113, 104 | Captions, hints, timestamps |
| `--accent-primary` | `#FF8856` | 255, 136, 86 | Primary CTA, buttons, links |
| `--accent-soft` | `#3A2418` | 58, 36, 24 | Accent backgrounds |
| `--success` | `#4FAE7B` | 79, 174, 123 | Success states |
| `--success-soft` | `#1A2E22` | 26, 46, 34 | Success backgrounds |
| `--danger` | `#E85A5A` | 232, 90, 90 | Errors, warnings |
| `--danger-soft` | `#3A1A1A` | 58, 26, 26 | Danger backgrounds |
| `--info` | `#5B7CC9` | 91, 124, 201 | Info, calculator |
| `--info-soft` | `#1A2438` | 26, 36, 56 | Info backgrounds |
| `--streak` | `#FFA45C` | 255, 164, 92 | Streak flame |
| `--gold` | `#FFCB57` | 255, 203, 87 | XP, badges, achievements |

### 1.3 Category Colors (Consistent Everywhere)

| Category | Hex | Emoji | Use |
| :--- | :--- | :--- | :--- |
| Food / Mess | `#FF8C42` | 🍛 | Mess fee, canteen, groceries |
| Rent / Hostel | `#5B7CC9` | 🏠 | Accommodation, hostel fee |
| Travel / Auto | `#9B6BCC` | 🚕 | Transport, auto, bus, metro |
| Books / Study | `#2D8B5F` | 📚 | Education, books, courses |
| Fun / Going out | `#E8558E` | 🎉 | Entertainment, movies, eating out |
| Bills / Recharge | `#F4B740` | 📱 | Phone bill, electricity, subscriptions |
| Savings | `#2D8B5F` | 💰 | Saved money, emergency fund |
| Investment | `#2E5AAC` | 📈 | SIP, stocks, mutual funds |
| Shopping | `#E8558E` | 🛍️ | Clothes, gadgets, Amazon |
| Medical | `#D63B3B` | 🏥 | Health, medicine, insurance |
| Salary / Income | `#2D8B5F` | 💼 | Pocket money, salary, freelance |
| Other | `#9F968A` | 📦 | Miscellaneous expenses |

---

## 2. TYPOGRAPHY

### 2.1 Font Families

| Font | Use | Fallback | Format |
| :--- | :--- | :--- | :--- |
| **Inter** | English text, numbers | Roboto, system-ui | Variable TTF |
| **Noto Sans Devanagari** | Hindi, Hinglish, Devanagari script | Mangal, system-ui | Variable TTF |
| **JetBrains Mono** | Currency numbers, codes, version | Fira Code, monospace | Variable TTF |

### 2.2 Type Scale

| Style | Size | Weight | Tracking | Line Height | Use Case |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Display | 36sp | 800 (ExtraBold) | -0.02em | 40sp | Celebrations, big numbers, "₹10,000 saved!" |
| Headline | 28sp | 700 (Bold) | -0.01em | 32sp | Module titles, section headers |
| Title | 22sp | 600 (SemiBold) | 0 | 28sp | Lesson titles, card headers, screen titles |
| Body Large | 18sp | 500 (Medium) | 0 | 26sp | Quiz questions, important body text |
| Body | 16sp | 500 (Medium) | 0 | 24sp | Card content, paragraphs, lists |
| Label Strong | 14sp | 700 (Bold) | 0.01em | 20sp | Button text, XP count, streak label |
| Label | 14sp | 600 (SemiBold) | 0.01em | 20sp | Tab labels, badge text, category names |
| Caption | 12sp | 500 (Medium) | 0 | 16sp | Helper text, timestamps, version, hints |
| Mono | 14sp | 600 (SemiBold) | 0.04em | 20sp | Currency numbers (₹10,000), codes |

### 2.3 Number Formatting Rules

- **Indian comma system:** `₹1,00,000` (NOT `₹100,000`)
- **Currency symbol:** Always `₹` prefix — `₹500`, `₹10,000`, `₹1.5L`
- **Decimal for paise:** `₹500.50` (only when needed)
- **Large amounts (Indian units):** `₹1.2L`, `₹4.5 Cr`, `₹12.5K`
- **Font:** Always use **JetBrains Mono** for numbers (visual rhythm)

---

## 3. SPACING SYSTEM

### 3.1 Spacing Scale (4dp grid)

```
0   4   8   12   16   20   24   32   40   48   64   80   96
```

| Token | Value | Common Use |
| :--- | :--- | :--- |
| `--space-xs` | 4dp | Icon gaps, tight inline spacing |
| `--space-sm` | 8dp | List item internal padding |
| `--space-md` | 12dp | Card internal padding, input padding |
| `--space-lg` | 16dp | Screen edge padding, section internal |
| `--space-xl` | 20dp | List item spacing |
| `--space-2xl` | 24dp | Card-to-card gap, section spacing |
| `--space-3xl` | 32dp | Large section breaks |
| `--space-4xl` | 40dp | Hero spacing |
| `--space-5xl` | 48dp | Major section breaks |
| `--space-6xl` | 64dp | Screen top/bottom padding |

### 3.2 Corner Radius Scale

```
0   8   12   16   20   24   28   999(pill)
```

| Token | Value | Use Case |
| :--- | :--- | :--- |
| `--radius-sm` | 8dp | Tags, small badges |
| `--radius-md` | 12dp | Buttons, inputs, small cards |
| `--radius-lg` | 16dp | Cards, lesson cards |
| `--radius-xl` | 20dp | Large cards, modals |
| `--radius-2xl` | 24dp | Pill buttons, search bars |
| `--radius-3xl` | 28dp | Bottom sheet top corners |
| `--radius-pill` | 999dp | Full pill buttons, badges, avatars |

### 3.3 Elevation (Shadows)

> IMPORTANT: Tint shadows to background hue (warm brown) — NOT pure black.

| Token | Value | Use Case |
| :--- | :--- | :--- |
| `--shadow-sm` | `0 1px 2px rgba(26,24,20,0.04)` | Subtle, 1dp |
| `--shadow-md` | `0 4px 12px rgba(26,24,20,0.06)` | Cards, 3dp |
| `--shadow-lg` | `0 12px 32px rgba(26,24,20,0.10)` | Modals, 6dp |
| `--shadow-xl` | `0 24px 48px rgba(26,24,20,0.15)` | Celebration overlays, 12dp |

---

## 4. COMPONENT RULES

### 4.1 Buttons

| Type | Background | Text | Height | Radius | State |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Primary** | `--accent-primary` | White | 56dp | `--radius-2xl` (pill) | Pressed: scale 0.96, opacity 0.92 |
| **Secondary** | Transparent | `--accent-primary` | 56dp | `--radius-2xl` | Border: 1.5dp accent |
| **Tertiary** | Transparent | `--accent-primary` | 48dp | `--radius-pill` | No border |
| **Danger** | `--danger` | White | 56dp | `--radius-2xl` | For destructive actions only |
| **Disabled** | `--bg-sunk` | `--text-muted` | 56dp | `--radius-2xl` | Opacity 0.5, no haptic |

### 4.2 Cards

| Type | Background | Border | Radius | Shadow |
| :--- | :--- | :--- | :--- | :--- |
| **Lesson Card** | `--bg-elevated` | `--border` | `--radius-lg` | `--shadow-md` |
| **Module Card** | `--bg-elevated` | `--border` | `--radius-md` | `--shadow-sm` |
| **Stat Card** | `--accent-soft` | None | `--radius-md` | None |
| **Quiz Option** | `--bg-elevated` | `--border` | `--radius-md` | None |
| **Selected Quiz** | `--accent-soft` | `--accent-primary` | `--radius-md` | None |
| **Correct Quiz** | `--success-soft` | `--success` | `--radius-md` | None |
| **Wrong Quiz** | `--danger-soft` | `--danger` | `--radius-md` | None |

### 4.3 Inputs

| State | Border | Background | Label |
| :--- | :--- | :--- | :--- |
| Default | `--border` (1.5dp) | `--bg-elevated` | Above input |
| Focused | `--accent-primary` (2dp) | `--bg-elevated` | Above, accent color |
| Error | `--danger` (2dp) | `--bg-elevated` | Above, danger color |
| Disabled | `--border` (1dp) | `--bg-sunk` | Above, muted |

### 4.4 Bottom Navigation

| Property | Value |
| :--- | :--- |
| Height | 80dp (including safe area) |
| Background | `--bg-elevated` |
| Top border | 1dp `--border` |
| Active icon | Filled, `--accent-primary` |
| Inactive icon | Outlined, `--text-muted` |
| Label size | 12sp |
| Tab count | 3 (Learn, Track, Profile) |

---

## 5. ICONOGRAPHY

### 5.1 Library

| Library | Use | Weight |
| :--- | :--- | :--- |
| **Material Symbols** | All UI icons | Rounded |
| **Emoji** | Card content, categories, notifications | Unicode |

### 5.2 Standard Icons

| Action | Material Symbol | Emoji Alternative |
| :--- | :--- | :--- |
| Home / Learn | `home` | 🏠 |
| Tools / Track | `calculate` | 📊 |
| Profile | `person` | 👤 |
| Streak | `local_fire_department` | 🔥 |
| Money | `payments` | 💰 |
| Win | `celebration` | 🎉 |
| Quiz | `quiz` | 🎯 |
| Calculator | `function` | 🔢 |
| Time | `schedule` | ⏰ |
| Save | `bookmark` | 🔖 |
| Share | `share` | 📤 |
| Settings | `settings` | ⚙️ |
| Lock | `lock` | 🔒 |
| Close | `close` | ✕ |
| Back | `arrow_back` | ← |
| Notification | `notifications` | 🔔 |

### 5.3 Emoji Policy

- ✅ ALLOWED in card content (lesson emojis from JSON)
- ✅ ALLOWED for category badges (🍛 🏠 🚕)
- ✅ ALLOWED in notifications for quick scan ("🔥 streak 7!")
- ❌ NOT in app UI chrome (nav, buttons, headers)
- ❌ NOT where Material icon exists (use icon for share, settings, etc.)

---

## 6. ANIMATION & MOTION

### 6.1 Timing

| Token | Value | Use Case |
| :--- | :--- | :--- |
| `--duration-fast` | 100ms | Tap feedback, button press |
| `--duration-normal` | 220ms | Card transitions, screen nav |
| `--duration-slow` | 280ms | Modal open, screen transitions |
| `--duration-celebration` | 480ms | Badge unlock, level up |
| `--duration-shimmer` | 1400ms | Loading shimmer loop |

### 6.2 Easing

| Token | Value | Use Case |
| :--- | :--- | :--- |
| `--ease-standard` | `cubic-bezier(0.2, 0, 0, 1)` | Material emphasized |
| `--ease-decelerate` | `cubic-bezier(0, 0, 0, 1)` | Entrance animations |
| `--ease-accelerate` | `cubic-bezier(0.3, 0, 1, 1)` | Exit animations |
| `--spring-bouncy` | `spring(dampingRatio: 0.6, stiffness: 380)` | Celebrations, logo reveal |
| `--spring-smooth` | `spring(dampingRatio: 0.8, stiffness: 300)` | Card flips, sheet open |
| `--spring-instant` | `spring(dampingRatio: 0.9, stiffness: 500)` | Button press, dot pulse |

### 6.3 Required Animations

| Event | Animation | Duration | Easing |
| :--- | :--- | :--- | :--- |
| Tap button | Scale 0.96 | 100ms | --spring-instant |
| Swipe card | Slide out + next slides in | 220ms | --ease-standard |
| XP earned | Number ticks up + "+30" floats | 480ms | --spring-bouncy |
| Streak day+ | Flame glows + grows | 300ms | --spring-smooth |
| Badge unlock | Scale 0→1, hold, confetti | 1500ms | --spring-bouncy |
| Quiz correct | Green pulse + check pop | 300ms | --spring-bouncy |
| Quiz wrong | Shake x2 + red flash | 250ms | --spring-smooth |
| Module complete | Confetti + trophy rise | 2000ms | --spring-bouncy |
| Level up | Full-screen overlay + fanfare | 1500ms | --spring-bouncy |
| Paywall open | Slide-up with bounce | 320ms | --spring-bouncy |
| Pull refresh | Coin spinner | 800ms | Loop |

### 6.4 Reduced Motion (MANDATORY)

- Respect system `prefers-reduced-motion` setting
- All infinite loops → static
- All spring physics → instant (0ms duration)
- Confetti → static image
- Scale animations → opacity fade only

---

## 7. ACCESSIBILITY RULES

| Rule | Standard |
| :--- | :--- |
| Contrast ratio | WCAG AA minimum (4.5:1 body, 3:1 large) |
| Touch targets | Minimum 48×48dp (comfortable 56×56dp) |
| Content descriptions | All icons must have labels |
| Screen reader | Full TalkBack support |
| Font scaling | Support 1.0x, 1.3x, 2.0x |
| Color independence | Don't rely on color alone — add icons/text |
| Dark mode | Available, auto-detect system setting |
| Offline | All lessons work without network |

---

## 8. VOICE & TONE

### 8.1 Personality

| Do | Don't |
| :--- | :--- |
| Friendly (older brother vibe) | Academic / lecture-style |
| Simple (8th class level) | Technical jargon |
| Encouraging ("Tum kar sakte ho!") | Negative / guilt-trip |
| Indian context (hostel, mess, UPI) | Western-only examples |
| Hinglish (natural mix) | Pure formal Hindi or English |

### 8.2 Vocabulary Rules

| Avoid | Use Instead |
| :--- | :--- |
| "Fiscal discipline" | "Paisa sambhalna" |
| "Diversified portfolio" | "Alag-alag jagah paisa lagana" |
| "Compound Annual Growth Rate" | "Saal bhar paisa badhna" |
| "Liquidity" | "Turant paisa nikal sakte ho" |
| "Budgeting involves allocating" | "Paisa ko hisson mein baantna" |

### 8.3 Sentence Patterns

| Pattern | Example |
| :--- | :--- |
| Concept intro | "Pehle tumhe X samajhna zaroori hai. [1 line simple]. Ab dekho kaise kaam karta hai..." |
| Math | "Formula dikhne mein scary hai, par simple hai. [Show formula]. Iska matlab: [plain]." |
| Action | "Ab tumhari baari. [Input field]. [See live result]. Bada easy tha na?" |
| Warning | "Yahan dhyan rakhna. Bahut log yahan galti karte hain: 1. ___ 2. ___" |

---

## 9. SCREEN-SPECIFIC RULES

### 9.1 Splash Screen

| Element | Specification |
| :--- | :--- |
| Logo container | 100×100dp, `--radius-lg`, `--accent-soft` bg |
| Logo emoji | 56sp, centered |
| App name | Display style, `--text-primary`, centered |
| Tagline | Body style, `--text-secondary`, centered |
| Loader | 3 dots, 8dp, `--accent-primary`, sequential pulse |
| Version | Caption style, `--text-muted`, 32dp from bottom |
| Background | Vertical gradient `--bg-base` → `#FFF3E8` |
| Total duration | ~3 seconds |

### 9.2 Home Screen

| Element | Specification |
| :--- | :--- |
| Progress header | Streak + XP + Badges (compact trio) |
| Today's task | Primary card, accent title, progress bar |
| Daily bonus | Secondary card, outlined style |
| Module map | Horizontal scroll, 80dp cards |
| Bottom nav | 3 tabs, 80dp height |

### 9.3 Lesson Playback

| Element | Specification |
| :--- | :--- |
| Card size | Full-width, 16dp side margins |
| Card padding | 24dp all sides |
| Max text per card | 3 lines, 12 words per line |
| Emoji size | 48-64dp, centered |
| Swipe threshold | 300dp horizontal drag |
| Progress bar | Top, 6dp height, accent fill |
| Exit confirmation | "Streak save kar le before leaving?" |

### 9.4 Calculator Screen

| Element | Specification |
| :--- | :--- |
| Input fields | 56dp height, `--radius-md`, `--bg-elevated` |
| Slider | Accent track, 20dp thumb |
| Result panel | `--accent-soft` bg, large mono numbers |
| Chart | Vico/MPAndroidChart, category colors |
| Live update | 200ms tween on input change |

---

## 10. PRODUCTION CHECKLIST

Before any design/development is considered "done":

- [ ] All colors match the hex values above
- [ ] All text uses Inter (English) or Noto Sans Devanagari (Hindi)
- [ ] Numbers use JetBrains Mono with Indian comma system
- [ ] Touch targets ≥ 48dp
- [ ] Contrast ratios pass WCAG AA
- [ ] Reduced motion fallback exists
- [ ] Dark mode renders correctly
- [ ] No pure black (`#000000`) or pure white (`#FFFFFF`) anywhere
- [ ] No purple/blue gradients (AI-default trap)
- [ ] No beige+brass premium (DTC-default trap)
- [ ] Animations use spring physics (not linear)
- [ ] No mixed font families within a headline
- [ ] No emoji in UI chrome (only in card content)
- [ ] No placeholder "lorem ipsum" text
- [ ] No fake-precision numbers (92%, 4.1×) without real data

---

## 11. QUICK REFERENCE CARD

```
COLORS
  Accent:  #FF6B35 (Orange)
  Success: #2D8B5F (Green)
  Danger:  #D63B3B (Red)
  Info:    #2E5AAC (Blue)
  Streak:  #FF8C42 (Flame)
  Gold:    #F4B740 (XP/Badges)

FONTS
  English: Inter
  Hindi:   Noto Sans Devanagari
  Numbers: JetBrains Mono

SPACING
  Screen edge:  16dp
  Card padding: 24dp
  Card gap:     24dp

RADIUS
  Buttons: 24dp (pill)
  Cards:   16dp
  Inputs:  12dp

BUTTON
  Height: 56dp
  Width:  Full-width (primary)

TOUCH TARGET
  Minimum: 48dp
  Comfortable: 56dp
```

---

*Last updated: August 2026*
*Version: 1.0.0*
*For: Money Matters — Shipathon 2026*
