<div align="center">
  <img src="app/src/main/res/drawable/logo.png" alt="MoneyMatters Logo" width="120" style="border-radius: 24px;" />
  <h1>MoneyMatters</h1>
  <p><b>Financial Literacy Platform for Indian College Youth</b></p>
  <p>Empowering the next generation of India with practical, jargon-free financial education from scratch to advanced wealth creation.</p>
</div>

---

## 📖 About The Project

Financial education is notably absent from traditional college curriculums, leaving millions of Indian college students and young graduates vulnerable to debt traps, predatory loan apps, bad investment choices, and confusion over taxes and corporate salaries.

**MoneyMatters** solves this problem by delivering a **structured, gamified, multi-language curriculum** (23 comprehensive modules) crafted specifically for Indian contexts. From understanding pocket money budgeting and bank accounts to filing ITR, analyzing stock markets, building an emergency fund, decoding CTC components, and launching campus startups — MoneyMatters transforms complex finance into bite-sized, actionable knowledge.

---

## ✨ Key Features & Capabilities

### 🎓 23 Practical Learning Modules (Zero to Advanced)
1. **Module 1–5: Financial Foundations**
   - Basic Understanding of Money & Time Value of Money
   - Real-Life College Budgeting (50/30/20 Rule adapted for students)
   - Smart Saving Strategies & Inflation Realities
   - Building a Student Emergency Fund
   - The Traps of Credit Cards, BNPL & Instant Loan Apps

2. **Module 6–10: Banking, Investing & Basic Taxation**
   - Modern Banking, UPI, Net Banking & Fraud Prevention
   - Investment Basics: Mutual Funds, Index Funds, SIP vs FD
   - Path to Financial Independence & Compounding
   - Health, Life & Cyber Insurance Fundamentals
   - Student Tax Basics, PAN/Aadhaar Linking, and TDS

3. **Module 11–15: Career, Freelancing & Digital Economy**
   - Real-World Financial Decision-Making & Big Purchases
   - Behavioral Finance & Money Psychology
   - Advanced Freelance Taxation (Section 44ADA, GST for Freelancers)
   - Creator Economy, Cross-Border Payments (PayPal, Wise, Forex)
   - Corporate Salary Breakdown (CTC, Basic, HRA, EPF, Gratuity, ESOPs)

4. **Module 16–23: Indian Ecosystem, Startups & Special Schemes**
   - Turning 18 Checklist (Voter ID, PAN, Bank KYC, Credit Score initiation)
   - Tier-2, Tier-3 & Rural Financial Inclusion
   - Student Startups, Grants & Incubator Funding
   - Central & State Government Scholarships and Schemes
   - Women’s Financial Rights, Schemes, and Disability Grants

---

### 🌐 Multilingual Accessibility
- Full localized curriculum available in **English, Hindi, Hinglish, Bengali, Gujarati, Kannada, Malayalam, Marathi, Punjabi, Tamil, and Telugu**.

### 🎮 Gamification & Interactive Learning
- **Interactive Flashcards & Story Reader**: Engaging story-style slide progression.
- **Knowledge Checks & Quizzes**: Instant answer explanations with XP rewards.
- **Streak & Achievement System**: Gamified milestones to keep students motivated daily.
- **AI Saarthi**: In-app AI financial doubt resolution engine tailored for college questions.

---

## 🛠️ Architecture & Tech Stack

MoneyMatters is built using **modern Android development best practices**, clean architecture, and reactive programming:

| Layer / Concern | Technology / Library |
|---|---|
| **Language** | Kotlin 1.9 (100% Coroutines & Flow) |
| **UI Framework** | Jetpack Compose (Declarative UI) + Material 3 |
| **Architecture** | MVVM / MVI Clean Architecture with Single Activity pattern |
| **Dependency Injection** | Dagger Hilt |
| **Navigation** | Navigation Compose |
| **Local Storage** | Jetpack DataStore (Preferences & Proto) |
| **Backend & Cloud** | Firebase Authentication, Cloud Firestore & Security Rules |
| **Serialization** | KotlinX Serialization JSON |
| **Animation & Media** | Airbnb Lottie Compose & Coil Image Loading |
| **Monetization / Subscriptions** | RevenueCat SDK |
| **Build System** | Gradle Kotlin DSL (`build.gradle.kts`) with Version Catalog (`libs.versions.toml`) |

---

## 📁 Repository Structure

```
moneymatters/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/moneymatters/       # Application Source Code
│   │   │   │   ├── data/                   # Repositories, Data Sources, DataStore
│   │   │   │   ├── di/                     # Hilt Dependency Injection Modules
│   │   │   │   ├── domain/                 # Domain Models, Use Cases, Business Logic
│   │   │   │   ├── ui/                     # Jetpack Compose Screens, Theme, Components
│   │   │   │   └── viewmodel/              # StateFlow-backed Architecture ViewModels
│   │   │   ├── assets/                     # Packaged JSON module cards and lessons
│   │   │   ├── res/                        # Drawables, Strings, Icons, Fonts
│   │   │   └── AndroidManifest.xml
│   │   └── test/                           # Unit Tests
│   └── build.gradle.kts                    # App-level build configuration
├── gradle/
│   ├── wrapper/                            # Gradle Wrapper binaries and config
│   └── libs.versions.toml                  # Central Version Catalog
├── verified modules/                       # Master multilingual curriculum markdown sources
├── firestore.rules                         # Production-grade Firestore security rules
├── DESIGN.md                               # UI/UX design tokens and design philosophy
├── build.gradle.kts                        # Root project build configuration
├── settings.gradle.kts                     # Project settings & plugin resolution
└── README.md                               # Project documentation
```

---

## 🚀 Getting Started & Local Setup

### Prerequisites
- **Android Studio Iguana | 2023.2.1** or newer (Ladybug / Koala recommended)
- **JDK 17** (configured in Gradle JDK settings)
- **Android SDK API 34** (compileSdk 34, minSdk 24)

### Clone and Run
1. Clone the repository:
   ```bash
   git clone https://github.com/pawankumar-hue/Moneymatters.git
   cd Moneymatters
   ```
2. Open the project folder in **Android Studio**.
3. Allow Gradle to synchronize dependencies.
4. Run the app on an Emulator (API 26+) or a physical Android device:
   ```bash
   ./gradlew installDebug
   ```

---

## 🔒 Security & Privacy
- Zero sensitive credentials or hardcoded keys committed to version control.
- Firebase Firestore secured with granular authenticated user rules (`firestore.rules`).
- Encrypted local preferences utilizing `androidx.security:security-crypto`.

---

## 🤝 Contributing
Contributions are welcome! If you'd like to improve the curriculum, add new interactive calculators, or enhance translations:
1. Fork the Project.
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`).
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`).
4. Push to the Branch (`git push origin feature/AmazingFeature`).
5. Open a Pull Request.

---

## 📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
