# SmartTodo 📝

Kotlin Multiplatform을 기반으로 한 스마트 할 일 관리 애플리케이션입니다. Android와 Desktop (JVM) 플랫폼을 지원합니다.

## ✨ 주요 기능

- **할 일 관리**: 할 일 추가, 수정, 삭제, 완료 처리
- **카테고리 분류**: 학업, 업무, 개인, 기타 카테고리로 할 일 분류
- **캘린더 뷰**: 날짜별 할 일 확인 및 관리
- **알림 기능**: 할 일 마감일 및 알림 시간 설정
- **통계 대시보드**: 완료율, 진행 중인 할 일 수 등 통계 확인
- **검색 및 필터**: 할 일 검색 및 필터링 (전체, 오늘, 다가오는, 완료됨)
- **프로필 관리**: 사용자 프로필 설정
- **온보딩**: 초기 사용자를 위한 온보딩 화면
- **인증**: 로그인 및 비밀번호 찾기 기능

## 🛠 기술 스택

- **언어**: Kotlin
- **프레임워크**: Compose Multiplatform
- **플랫폼**: Android, Desktop (JVM)
- **빌드 도구**: Gradle (Kotlin DSL)
- **의존성 관리**: Version Catalog (libs.versions.toml)

### 주요 라이브러리

- Compose Multiplatform 1.9.1
- Material 3
- Kotlinx DateTime 0.6.0
- AndroidX Lifecycle
- Kotlinx Coroutines

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
git clone https://github.com/yourusername/SmartTodo.git
cd SmartTodo
```

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
SmartTodo/
├── composeApp/
│   ├── src/
│   │   ├── commonMain/          # 공통 코드
│   │   │   └── kotlin/
│   │   │       └── com/example/smarttodo/
│   │   │           ├── SmartTodoApp.kt      # 메인 앱
│   │   │           ├── HomeScreen.kt        # 홈 화면
│   │   │           ├── TodoModels.kt       # 데이터 모델
│   │   │           ├── CalendarScreen.kt    # 캘린더 화면
│   │   │           ├── CategoryScreen.kt    # 카테고리 화면
│   │   │           ├── StatisticsScreen.kt  # 통계 화면
│   │   │           └── ...
│   │   ├── androidMain/         # Android 전용 코드
│   │   └── jvmMain/             # Desktop 전용 코드
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml       # 버전 카탈로그
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

## 📱 사용 방법

1. **앱 실행**: 앱을 실행하면 온보딩 화면이 표시됩니다.
2. **인증**: 온보딩 후 로그인 화면에서 인증을 진행합니다.
3. **할 일 추가**: 홈 화면의 플로팅 액션 버튼(+)을 눌러 새 할 일을 추가합니다.
4. **할 일 관리**: 
   - 체크박스를 눌러 완료 처리
   - 할 일 항목의 메뉴(⋮)를 눌러 수정 또는 삭제
5. **필터링**: 홈 화면에서 "전체", "오늘", "다가오는", "완료됨" 필터를 사용하여 할 일을 필터링합니다.
6. **검색**: 검색창에 키워드를 입력하여 할 일을 검색합니다.

## 🏗 빌드 구성

프로젝트는 Kotlin Multiplatform을 사용하여 공통 코드를 공유하고, 플랫폼별 특화 코드는 각 플랫폼 폴더에 있습니다.

- `commonMain`: 모든 플랫폼에서 공유되는 코드
- `androidMain`: Android 전용 코드
- `jvmMain`: Desktop (JVM) 전용 코드

## 🤝 기여하기

프로젝트에 기여하고 싶으시다면 [CONTRIBUTING.md](CONTRIBUTING.md)를 참고해주세요.

## 📄 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참고하세요.

## 👥 작성자

- 프로젝트 작성자

## 🙏 감사의 말

- [JetBrains Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)

## 📞 문의

이슈나 질문이 있으시면 GitHub Issues를 통해 문의해주세요.

---

**참고**: 이 프로젝트는 Kotlin Multiplatform과 Compose Multiplatform을 학습하고 실습하기 위한 프로젝트입니다.
