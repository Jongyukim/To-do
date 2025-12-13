# SmartTodo 📝

Kotlin Multiplatform을 기반으로 한 스마트 할 일 관리 애플리케이션입니다. Android와 Desktop (JVM) 플랫폼을 지원하며, Firebase를 백엔드로 사용합니다.

## ✨ 주요 기능

- **할 일 관리**: 할 일 추가, 수정, 삭제, 완료 처리
- **카테고리 분류**: 학업, 업무, 개인, 기타 카테고리로 할 일 분류
- **캘린더 뷰**: 날짜별 할 일 확인 및 관리
- **알림 기능**: 할 일 마감일 및 알림 시간 설정 (Android Work Manager 사용)
- **통계 대시보드**: 완료율, 진행 중인 할 일 수 등 통계 확인
- **검색 및 필터**: 할 일 검색 및 필터링 (전체, 오늘, 다가오는, 완료됨)
- **프로필 관리**: 사용자 프로필 설정
- **온보딩**: 초기 사용자를 위한 온보딩 화면
- **인증**: Firebase Authentication을 통한 로그인 및 비밀번호 찾기 기능
- **클라우드 동기화**: Firebase Firestore를 통한 데이터 동기화

## 🛠 기술 스택

- **언어**: Kotlin 2.2.20
- **프레임워크**: Compose Multiplatform 1.9.1
- **플랫폼**: Android, Desktop (JVM)
- **백엔드**: Firebase (Firestore, Authentication)
- **빌드 도구**: Gradle (Kotlin DSL)
- **의존성 관리**: Version Catalog (libs.versions.toml)

### 주요 라이브러리

- **UI**: Compose Multiplatform 1.9.1, Material 3
- **비동기**: Kotlinx Coroutines 1.10.2
- **날짜/시간**: Kotlinx DateTime 0.6.0
- **생명주기**: AndroidX Lifecycle 2.9.5
- **백엔드**: 
  - Firebase Firestore KTX 25.0.0
  - Firebase Auth KTX 23.0.0
- **알림**: AndroidX Work Manager 2.9.0 (Android)

## 📋 요구사항

### Android
- Android Studio Hedgehog | 2023.1.1 이상
- JDK 11 이상
- Android SDK 24 이상 (Min SDK: 24, Target SDK: 36)

### Desktop (JVM)
- JDK 11 이상
- Windows, macOS, 또는 Linux

## 🚀 시작하기

### 프로젝트 클론

```bash
git clone https://github.com/Jongyukim/To-do.git
cd To-do
```

### Firebase 설정 (Android)

Android 앱을 실행하기 전에 Firebase 프로젝트를 설정해야 합니다:

1. [Firebase Console](https://console.firebase.google.com/)에서 프로젝트 생성
2. Android 앱 등록 (패키지명: `com.example.smarttodo`)
3. `google-services.json` 파일을 다운로드하여 `composeApp/src/androidMain/` 디렉토리에 배치
4. Firebase Authentication과 Firestore를 활성화

### Android 앱 빌드 및 실행

#### macOS/Linux
```bash
./gradlew :composeApp:assembleDebug
```

#### Windows
```bash
.\gradlew.bat :composeApp:assembleDebug
```

빌드된 APK는 `composeApp/build/outputs/apk/debug/composeApp-debug.apk`에 생성됩니다.

Android Studio에서 실행하려면:
1. Android Studio에서 프로젝트 열기
2. 실행 구성에서 Android 앱 선택
3. 실행 버튼 클릭

### Desktop 앱 빌드 및 실행

#### macOS/Linux
```bash
./gradlew :composeApp:run
```

#### Windows
```bash
.\gradlew.bat :composeApp:run
```

### 프로젝트 구조

```
To-do/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/                    # 공통 코드
│   │   │   └── kotlin/com/example/smarttodo/
│   │   │       ├── SmartTodoApp.kt        # 메인 앱 진입점
│   │   │       ├── App.kt                 # 앱 구성
│   │   │       ├── Screen.kt              # 화면 정의
│   │   │       ├── HomeScreen.kt          # 홈 화면
│   │   │       ├── CalendarScreen.kt      # 캘린더 화면
│   │   │       ├── CategoryScreen.kt     # 카테고리 화면
│   │   │       ├── StatisticsScreen.kt   # 통계 화면
│   │   │       ├── ProfileScreen.kt      # 프로필 화면
│   │   │       ├── SettingsScreen.kt     # 설정 화면
│   │   │       ├── AuthScreen.kt         # 인증 화면
│   │   │       ├── NotificationScreen.kt # 알림 화면
│   │   │       ├── OnboardingScreens.kt  # 온보딩 화면
│   │   │       ├── EditTodoSheet.kt      # 할 일 편집 시트
│   │   │       ├── TodoModels.kt         # 데이터 모델
│   │   │       ├── AuthManager.kt        # 인증 관리
│   │   │       ├── NotificationManager.kt # 알림 관리
│   │   │       ├── Platform.kt           # 플랫폼 추상화
│   │   │       └── data/
│   │   │           ├── FirebaseRepository.kt    # Firebase 데이터 저장소
│   │   │           └── RepositoryProvider.kt    # 저장소 제공자
│   │   ├── androidMain/                   # Android 전용 코드
│   │   │   ├── kotlin/com/example/smarttodo/
│   │   │   │   ├── MainActivity.kt       # Android 메인 액티비티
│   │   │   │   ├── AuthManager.android.kt
│   │   │   │   ├── NotificationManager.android.kt
│   │   │   │   ├── NotificationWorker.kt # Work Manager 워커
│   │   │   │   ├── Platform.android.kt
│   │   │   │   └── data/
│   │   │   │       ├── FirebaseRepository.kt
│   │   │   │       └── RepositoryProvider.kt
│   │   │   ├── google-services.json      # Firebase 설정 파일
│   │   │   └── res/                      # Android 리소스
│   │   ├── jvmMain/                      # Desktop 전용 코드
│   │   │   └── kotlin/com/example/smarttodo/
│   │   │       ├── main.kt               # Desktop 진입점
│   │   │       ├── NotificationManager.jvm.kt
│   │   │       ├── Platform.jvm.kt
│   │   │       └── data/
│   │   │           ├── FirebaseRepository.kt
│   │   │           └── RepositoryProvider.kt
│   │   └── commonTest/                   # 공통 테스트 코드
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml                 # 버전 카탈로그
├── build.gradle.kts
├── settings.gradle.kts
├── README.md
├── CONTRIBUTING.md
├── CHANGELOG.md
└── LICENSE
```

## 📱 사용 방법

1. **앱 실행**: 앱을 실행하면 온보딩 화면이 표시됩니다.
2. **회원가입/로그인**: 
   - 온보딩 후 로그인 화면에서 Firebase Authentication을 통해 계정을 생성하거나 로그인합니다.
   - 이메일과 비밀번호로 인증이 가능합니다.
3. **할 일 추가**: 홈 화면의 플로팅 액션 버튼(+)을 눌러 새 할 일을 추가합니다.
   - 제목, 설명, 카테고리, 마감일, 알림 시간을 설정할 수 있습니다.
4. **할 일 관리**: 
   - 체크박스를 눌러 완료 처리
   - 할 일 항목의 메뉴(⋮)를 눌러 수정 또는 삭제
   - 모든 변경사항은 Firebase Firestore에 자동으로 동기화됩니다.
5. **필터링**: 홈 화면에서 "전체", "오늘", "다가오는", "완료됨" 필터를 사용하여 할 일을 필터링합니다.
6. **검색**: 검색창에 키워드를 입력하여 할 일을 검색합니다.
7. **카테고리별 보기**: 카테고리 화면에서 학업, 업무, 개인, 기타별로 할 일을 확인할 수 있습니다.
8. **통계 확인**: 통계 화면에서 완료율과 진행 상황을 확인할 수 있습니다.

## 🏗 빌드 구성

프로젝트는 Kotlin Multiplatform을 사용하여 공통 코드를 공유하고, 플랫폼별 특화 코드는 각 플랫폼 폴더에 있습니다.

- **commonMain**: 모든 플랫폼에서 공유되는 UI 및 비즈니스 로직
- **androidMain**: Android 전용 코드 (Firebase, Work Manager, 알림 등)
- **jvmMain**: Desktop (JVM) 전용 코드
- **commonTest**: 공통 테스트 코드

### 아키텍처

- **Repository 패턴**: `FirebaseRepository`를 통해 데이터 접근 추상화
- **Platform-specific 구현**: 플랫폼별 기능은 `expect/actual` 메커니즘 사용
- **Compose Multiplatform**: 공통 UI 코드로 Android와 Desktop 지원

## 🤝 기여하기

프로젝트에 기여하고 싶으시다면 [CONTRIBUTING.md](CONTRIBUTING.md)를 참고해주세요.

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참고하세요.

## 👥 팀원 및 기여 분야

이 프로젝트는 4명의 팀원이 협업하여 개발되었습니다:

### 김종유
- **UI/UX 디자인 및 구현**: 전체 앱의 사용자 인터페이스 설계 및 구현
- **알림 시스템**: Android Work Manager를 활용한 할 일 알림 기능 구현
- **프로젝트 초기 설정**: Kotlin Multiplatform 프로젝트 구조 설계 및 설정
### 김완진
- **인증 시스템**: Firebase Authentication을 활용한 로그인/회원가입 기능 구현
- **화면 구성**: 홈, 프로필, 설정, 캘린더, 통계 화면의 기본 구조 및 레이아웃 구현

### 오성민
- **Firebase 연동**: Firebase 프로젝트 설정 및 연동
- **데이터 계층**: Firestore 데이터 저장소 및 Repository 패턴 구현
- **백엔드 로직**: 로그아웃, 프로필 데이터 불러오기 등 백엔드 기능 구현
- **플랫폼별 구현**: Android/JVM 플랫폼별 Firebase Repository 구현

### 이성민
- **홈 화면 기능**: 카테고리별 할 일 보기, 날짜 정렬 기능 구현
- **캘린더 화면**: 캘린더에서 할 일 완료 처리 및 데이터 연동 기능
- **프로필 화면**: 업적 시스템 및 프로필 데이터 표시 기능
- **알림 페이지**: 알림 on/off 토글 기능 구현
- **통계 화면**: 완료율 표시 및 통계 데이터 연동

## 🙏 감사의 말

- [JetBrains Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)

## 📞 문의

이슈나 질문이 있으시면 GitHub Issues를 통해 문의해주세요.

---

**참고**: 이 프로젝트는 Kotlin Multiplatform과 Compose Multiplatform을 학습하고 실습하기 위한 프로젝트입니다.
