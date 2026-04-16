# HealthLoop — Play Store Deployment Checklist

> Complete checklist based on full project scan. Items marked ✅ are already done, ⚠️ need attention, ❌ are missing/blocking.

---

## 1. APPLICATION ID & VERSIONING

| Item | Status | Details |
|------|--------|---------|
| Change `applicationId` from `com.example.*` | ❌ **BLOCKING** | Currently `com.example.healthloop`. Google Play **rejects** `com.example.*` IDs. Change to something like `com.yourcompany.healthloop` |
| Set proper `versionCode` | ⚠️ | Currently `1`. Fine for first release, must increment for every update |
| Set proper `versionName` | ⚠️ | Currently `"1.0"`. Consider semantic versioning like `"1.0.0"` |
| `targetSdk = 35` | ✅ | Meets current Google Play requirements |
| `compileSdk = 35` | ✅ | Up to date |
| `minSdk = 28` | ✅ | Android 9+ (reasonable for health app) |

---

## 2. APP SIGNING

| Item | Status | Details |
|------|--------|---------|
| Generate release keystore (`.jks`) | ❌ **BLOCKING** | No signing config found. Run: `keytool -genkey -v -keystore healthloop-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias healthloop` |
| Add `signingConfigs` block in `build.gradle.kts` | ❌ **BLOCKING** | Add release signing config referencing your keystore |
| Link signing config to release build type | ❌ **BLOCKING** | Add `signingConfig = signingConfigs.getByName("release")` to the `release` block |
| Store keystore credentials securely | ❌ | Use `local.properties` or environment variables — never hardcode |
| Back up keystore file | ❌ | **CRITICAL**: If you lose the keystore, you can never update the app. Store backup securely offline |
| Enroll in Google Play App Signing | ❌ | Strongly recommended — lets Google manage your signing key |
| Add `*.jks` and `*.keystore` to `.gitignore` | ❌ | Not currently in `.gitignore` — add before committing |

---

## 3. BUILD CONFIGURATION

| Item | Status | Details |
|------|--------|---------|
| R8/ProGuard minification enabled | ✅ | `isMinifyEnabled = true` in release |
| Resource shrinking enabled | ✅ | `isShrinkResources = true` in release |
| ProGuard rules for Retrofit, Gson, Room, Hilt, Vico, Compose | ✅ | Well-configured in `proguard-rules.pro` |
| Test release build thoroughly | ❌ | Build release APK/AAB and test ALL features — minification can break reflection-based code |
| Generate Android App Bundle (AAB) | ❌ | Google Play requires AAB (not APK). Use `./gradlew bundleRelease` |
| Fix typo in `build.gradle.kts` | ⚠️ | Line 35 has stray text `}can you` — should be just `}` |

---

## 4. GOOGLE PLAY CONSOLE SETUP

| Item | Status | Details |
|------|--------|---------|
| Create Google Play Developer account | ❌ | Requires $25 one-time fee at https://play.google.com/console |
| Create app listing in Play Console | ❌ | |
| Select app category | ❌ | Recommended: **Health & Fitness** |
| Set content rating (IARC questionnaire) | ❌ | Required — answer questionnaire about app content |
| Set target audience and content | ❌ | App is NOT for children — declare this clearly |
| Set up pricing & distribution | ❌ | Free app (no in-app purchases detected) |

---

## 5. STORE LISTING ASSETS

| Item | Status | Details |
|------|--------|---------|
| App title (max 30 chars) | ❌ | e.g., "HealthLoop - Health Tracker" |
| Short description (max 80 chars) | ❌ | e.g., "Track health metrics, mood & get AI-powered insights" |
| Full description (max 4000 chars) | ❌ | Detailed feature description |
| App icon (512×512 PNG, 32-bit, no alpha) | ⚠️ | You have adaptive icons in-app — need a separate 512×512 hi-res icon for Play Store |
| Feature graphic (1024×500 PNG/JPG) | ❌ | Required banner image for Play Store listing |
| Screenshots: Phone (min 2, recommended 8) | ⚠️ | 9 screenshots exist in `screenshots/` folder — verify they meet Play Store specs: min 320px, max 3840px, 16:9 or 9:16 aspect ratio |
| Screenshots: 7-inch tablet | ⚠️ | Recommended if you support tablets |
| Screenshots: 10-inch tablet | ⚠️ | Recommended if you support tablets |
| Promo video (optional, YouTube URL) | ❌ | Optional but improves conversion |

---

## 6. PRIVACY, DATA SAFETY & LEGAL

| Item | Status | Details |
|------|--------|---------|
| Create Privacy Policy | ❌ **BLOCKING** | **Mandatory** for apps accessing health data and external APIs. Must cover: data collected (health metrics, mood, profile), AI API usage (OpenAI), data storage (local), data sharing, user rights |
| Host Privacy Policy at a public URL | ❌ **BLOCKING** | Host on your website, GitHub Pages, or a service like Termly/PrivacyPolicies.com |
| Add Privacy Policy link in app | ⚠️ | Should be accessible in Settings/About screen |
| Complete Data Safety form in Play Console | ❌ **BLOCKING** | Declare: health data collected, data sent to OpenAI API, data stored locally, no data sold |
| Terms of Service (optional but recommended) | ❌ | |
| Medical disclaimer | ❌ **IMPORTANT** | Health app — add clear disclaimer: "This app is not a substitute for medical advice" |
| AI-generated content disclaimer | ❌ | Disclose that AI Assistant responses are generated by AI and not medical advice |

### Data Safety Declaration (what to declare):

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| Health info (steps, sleep, water, weight, calories, exercise) | Yes | No* | App functionality |
| Mood/emotional state | Yes | No* | App functionality |
| Personal info (name, age, height, weight) | Yes | No* | App functionality |
| Photos (profile picture) | Yes | No | App functionality |

*\*Data is sent to OpenAI API when using AI Assistant feature — must be disclosed as "shared with third party"*

---

## 7. PERMISSIONS AUDIT

| Permission | Status | Play Store Notes |
|------------|--------|------------------|
| `INTERNET` | ✅ | Standard — for API calls |
| `POST_NOTIFICATIONS` | ✅ | Runtime permission on Android 13+ — ensure you request it |
| `RECEIVE_BOOT_COMPLETED` | ✅ | For re-scheduling reminders after reboot |
| `SCHEDULE_EXACT_ALARM` | ⚠️ | Requires justification in Play Console. Must declare why exact alarms are needed |
| `USE_EXACT_ALARM` | ⚠️ | Added in API 33 — auto-granted but Google may review usage |
| `USE_FULL_SCREEN_INTENT` | ⚠️ | Requires `USE_FULL_SCREEN_INTENT` permission declaration in Play Console |
| `VIBRATE` | ✅ | Standard |
| `WAKE_LOCK` | ✅ | Standard for alarms |
| `FOREGROUND_SERVICE` | ✅ | Required for alarm service |
| `FOREGROUND_SERVICE_SPECIAL_USE` | ⚠️ | Must justify "special use" foreground service in Play Console |
| `WRITE_EXTERNAL_STORAGE` (max SDK 28) | ✅ | Properly scoped to legacy devices only |

---

## 8. SECURITY

| Item | Status | Details |
|------|--------|---------|
| API keys not hardcoded | ✅ | Loaded from `local.properties` via `BuildConfig` |
| `local.properties` in `.gitignore` | ✅ | Properly excluded |
| API keys in release APK | ⚠️ | `BuildConfig` fields can be extracted from the APK via decompilation. Consider using a backend proxy for API calls |
| Network security config | ❌ | No `network_security_config.xml` — add one to enforce HTTPS-only |
| `android:allowBackup="true"` | ⚠️ | Backup enabled but rules are empty/commented — configure `backup_rules.xml` or disable backup |
| `data_extraction_rules.xml` configured | ⚠️ | File exists but is essentially empty — configure what to include/exclude |
| BootReceiver exported=true | ⚠️ | Exported but has intent filter for `BOOT_COMPLETED` — acceptable but review |
| HTTPS enforcement | ⚠️ | Verify all API calls use HTTPS (OpenAI and Gemini APIs do use HTTPS) |
| No WebView usage | ✅ | No WebView vulnerabilities to worry about |
| ProGuard obfuscation | ✅ | Enabled for release builds |

---

## 9. CODE QUALITY & STABILITY

| Item | Status | Details |
|------|--------|---------|
| Fix `build.gradle.kts` syntax error | ❌ | Line 35: `}can you` — stray text will cause build failure |
| Crash-free release build test | ❌ | Build and test release variant on physical devices |
| Test all 7 health metrics entry/display | ❌ | Water, sleep, steps, mood, weight, calories, exercise |
| Test AI Assistant with real API key | ❌ | Verify OpenAI integration works in release build |
| Test alarm/reminder system | ❌ | Verify exact alarms, boot receiver, notification channels |
| Test on minimum SDK (API 28) | ❌ | Test on Android 9 device/emulator |
| Test on latest Android (API 35) | ❌ | Test on Android 15 device/emulator |
| Test offline behavior | ❌ | App should handle no-internet gracefully for AI features |
| Test database migration | ✅ | First release — no migration needed |
| Remove debug logging | ⚠️ | Check for `Log.d()`, `println()` statements in source code |
| Handle empty states | ⚠️ | Verify `nodata.xml` drawable is used when no entries exist |

---

## 10. TESTING

| Item | Status | Details |
|------|--------|---------|
| Unit tests | ❌ | Only example test exists. Recommended: ViewModel tests, UseCase tests, Repository tests |
| UI/Instrumented tests | ❌ | Only example test exists. Recommended: Screen navigation tests, entry flow tests |
| Manual QA pass on release build | ❌ | Full manual test of every screen and feature |

---

## 11. PERFORMANCE & OPTIMIZATION

| Item | Status | Details |
|------|--------|---------|
| App size check | ❌ | Build AAB and check download size — under 150MB limit |
| Cold start time | ⚠️ | Has splash screen — ensure it's not artificially delayed |
| Memory leaks check | ❌ | Run with LeakCanary or Android Studio Profiler |
| Battery usage | ⚠️ | Exact alarms + WorkManager — test battery impact |
| ANR (Application Not Responding) check | ❌ | Ensure no main-thread network calls or heavy DB operations |

---

## 12. ACCESSIBILITY & LOCALIZATION

| Item | Status | Details |
|------|--------|---------|
| Content descriptions on icons/images | ⚠️ | Verify Compose `contentDescription` params are set on all icons |
| Touch target sizes (min 48dp) | ⚠️ | Verify all clickable elements meet minimum size |
| Color contrast ratios | ⚠️ | Verify text is readable on all backgrounds |
| String extraction to `strings.xml` | ❌ | Most strings are hardcoded in Compose — extract for localization |
| RTL support | ✅ | `android:supportsRtl="true"` declared |

---

## 13. PRE-LAUNCH STEPS

| # | Step | Status |
|---|------|--------|
| 1 | Fix `applicationId` — change from `com.example.healthloop` | ❌ |
| 2 | Fix `build.gradle.kts` syntax error on line 35 | ❌ |
| 3 | Generate release keystore | ❌ |
| 4 | Configure signing in `build.gradle.kts` | ❌ |
| 5 | Add `*.jks`/`*.keystore` to `.gitignore` | ❌ |
| 6 | Create and host Privacy Policy | ❌ |
| 7 | Add medical + AI disclaimers in app | ❌ |
| 8 | Add `network_security_config.xml` | ❌ |
| 9 | Configure `backup_rules.xml` and `data_extraction_rules.xml` | ❌ |
| 10 | Remove/guard debug logs | ❌ |
| 11 | Build release AAB: `./gradlew bundleRelease` | ❌ |
| 12 | Test release build on multiple devices/API levels | ❌ |
| 13 | Prepare Play Store listing assets (icon 512×512, feature graphic 1024×500, screenshots) | ❌ |
| 14 | Create Google Play Developer account ($25) | ❌ |
| 15 | Create app in Play Console | ❌ |
| 16 | Complete Store Listing (title, descriptions, screenshots, category) | ❌ |
| 17 | Complete Content Rating questionnaire | ❌ |
| 18 | Complete Data Safety form | ❌ |
| 19 | Declare permissions usage (exact alarms, full-screen intent, foreground service) | ❌ |
| 20 | Upload AAB to internal/closed testing track first | ❌ |
| 21 | Test via internal testing for 1-2 weeks | ❌ |
| 22 | Promote to production | ❌ |

---

## 14. POST-LAUNCH

| Item | Details |
|------|---------|
| Monitor Android Vitals | Crash rate, ANR rate in Play Console |
| Set up Firebase Crashlytics | Real-time crash reporting (not currently integrated) |
| Monitor user reviews | Respond to reviews in Play Console |
| Plan update cadence | Increment `versionCode` for every release |
| Keep `targetSdk` current | Google requires targeting recent API levels |
| Monitor API key usage | Track OpenAI API costs and rate limits |

---

## CRITICAL BLOCKERS SUMMARY

These **must** be resolved before submission:

1. **Change `applicationId`** from `com.example.healthloop` to your own domain
2. **Fix syntax error** in `build.gradle.kts` line 35 (`}can you`)
3. **Generate keystore & configure signing**
4. **Create & host Privacy Policy** (mandatory for health apps)
5. **Complete Data Safety form** in Play Console
6. **Build & test release AAB**
