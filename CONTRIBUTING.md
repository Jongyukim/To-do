# 기여 가이드 (Contributing Guide)

SmartTodo 프로젝트에 기여해주셔서 감사합니다! 이 문서는 프로젝트에 기여하는 방법을 안내합니다.

## 🚀 시작하기

1. 이 저장소를 포크합니다.
2. 로컬에 클론합니다:
   ```bash
   git clone https://github.com/yourusername/SmartTodo.git
   cd SmartTodo
   ```
3. 새로운 브랜치를 생성합니다:
   ```bash
   git checkout -b feature/your-feature-name
   ```

## 📝 개발 가이드라인

### 코드 스타일

- Kotlin 코딩 컨벤션을 따릅니다.
- Compose 코드는 Material 3 디자인 가이드라인을 준수합니다.
- 함수와 클래스에는 명확한 이름을 사용합니다.
- 복잡한 로직에는 주석을 추가합니다.

### 커밋 메시지

커밋 메시지는 명확하고 간결하게 작성해주세요:

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅
refactor: 코드 리팩토링
test: 테스트 추가
chore: 빌드 설정 변경
```

예시:
- `feat: 할 일 알림 기능 추가`
- `fix: 캘린더 화면 날짜 표시 오류 수정`
- `docs: README에 설치 방법 추가`

## 🔍 Pull Request 프로세스

1. 변경사항을 커밋합니다:
   ```bash
   git add .
   git commit -m "feat: 새로운 기능 설명"
   ```

2. 브랜치를 푸시합니다:
   ```bash
   git push origin feature/your-feature-name
   ```

3. GitHub에서 Pull Request를 생성합니다.

### Pull Request 체크리스트

- [ ] 코드가 프로젝트의 코딩 스타일을 따릅니다
- [ ] 새로운 기능에 대한 테스트를 추가했습니다 (가능한 경우)
- [ ] 문서를 업데이트했습니다 (필요한 경우)
- [ ] 커밋 메시지가 명확합니다
- [ ] 변경사항이 빌드 오류 없이 작동합니다

## 🐛 버그 리포트

버그를 발견하셨다면, 다음 정보를 포함하여 이슈를 생성해주세요:

- 버그에 대한 명확한 설명
- 재현 단계
- 예상 동작
- 실제 동작
- 환경 정보 (OS, Android 버전 등)
- 스크린샷 (가능한 경우)

## 💡 기능 제안

새로운 기능을 제안하고 싶으시다면:

- 기능에 대한 명확한 설명
- 왜 이 기능이 유용한지
- 구현 방법에 대한 제안 (선택사항)

## 📚 추가 리소스

- [Kotlin 코딩 컨벤션](https://kotlinlang.org/docs/coding-conventions.html)
- [Compose Multiplatform 문서](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)
- [Material 3 디자인 가이드](https://m3.material.io/)

## ❓ 질문이 있으신가요?

질문이나 제안사항이 있으시면 GitHub Issues를 통해 문의해주세요.

감사합니다! 🎉

