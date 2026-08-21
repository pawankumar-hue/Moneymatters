# Design System & UI Architecture: MoneyMatters

## 1. Design Philosophy: High-Engagement Financial Education
**MoneyMatters** is designed specifically for Gen-Z college students and early-career individuals in India. Traditional finance apps are often intimidating, text-heavy, and uninspiring. MoneyMatters reimagines financial education by blending:
- **Bite-Sized Gamification**: Story-driven modules, real-time quizzes, XP progress, and milestone badges.
- **Deep-Dark Modern Aesthetic**: An immersive, battery-efficient dark theme built with true blacks, slate surfaces, and calibrated high-contrast neon accents.
- **Micro-Interactions & Haptics**: Snappy feedback, dynamic gradient borders, fluid state transitions, and responsive gestures.

---

## 2. Color Palette & Visual Foundations

### A. Surfaces & Backgrounds
| Token | Hex | Role |
|---|---|---|
| `SurfaceBackground` | `#0A0E17` | Deep obsidian background minimizing eye strain |
| `SurfaceCardPrimary` | `#121826` | Primary card and module container surface |
| `SurfaceCardElevated` | `#1A2234` | Modal dialogs, floating action buttons, popovers |
| `BorderSubtle` | `#232E48` | Hairline dividers and inactive component outlines |

### B. Accent & Semantic Palette
| Token | Hex | Role |
|---|---|---|
| `ElectricViolet` | `#6366F1` | Primary brand accent & active navigation states |
| `CyberEmerald` | `#10B981` | Success states, investments, profits, correct answers |
| `VibrantAmber` | `#F59E0B` | Badges, streak counters, warnings, XP highlights |
| `CrimsonAlert` | `#EF4444` | Errors, high-risk financial warnings, debt alerts |
| `SkyCyan` | `#06B6D4` | Secondary interactive elements, links, info badges |

### C. Typography Hierarchy
- **Heading Display (24sp - 32sp / Bold)**: Clean sans-serif with tight tracking for milestone titles and module headers.
- **Body Core (14sp - 16sp / Regular & Medium)**: High-legibility typography optimized for bilingual reading (English, Hindi, Hinglish, regional scripts).
- **Caption & Meta (11sp - 13sp / Semi-Bold)**: Uppercase badges, timestamps, tags, and progress metrics.

---

## 3. Core Component Guidelines

### A. Story & Reel Progression Bar
- **Visual**: Segmented linear progress indicators at the top of card-based lessons.
- **Behavior**: Smooth transitions between conceptual cards, auto-pausing on touch hold, and instant restart/review capabilities.

### B. Interactive Financial Scenario Cards
- **Visual**: Dual-tone interactive cards with embedded calculators, dynamic sliders, and comparison splits (e.g., SIP vs FD, Old vs New Tax Regime).
- **Feedback**: Color-coded feedback states with instant explanations on quiz and decision selections.

### C. AI Saarthi Financial Assistant Interface
- **Layout**: Clean conversational thread with distinct sender/receiver bubbles, dynamic suggested quick-prompts, and inline financial formula rendering.

### D. Milestone Badges & Gamified Profile
- **Visual**: Hexagonal and circular badge tokens with luminous gradient borders (`#6366F1` → `#06B6D4`).
- **Data Display**: Streak counter, modules mastered, net financial IQ score, and certificate unlock progress.

---

## 4. Accessibility & Cross-Device Ergonomics
- **Minimum Tap Target**: 48dp on all touch elements.
- **Contrast Compliance**: WCAG AA standards (minimum 4.5:1 contrast ratio across all text and background combinations).
- **Responsive Layout**: Fluid Jetpack Compose layout trees with adaptive margins for compact phones and larger displays.
