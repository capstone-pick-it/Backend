# 🧲 Pick-It Backend

> 캡스톤디자인 프로젝트 - Pick-It 백엔드 레포지토리

---

## 🛠️ 기술 스택

- Java 21
- Spring Boot 4.0.1
- Spring Data JPA 
- MySQL
- Gradle

---

## 📁 프로젝트 구조

도메인 중심(Domain-Driven) 패키지 구조를 따릅니다.

```
com.capstone.pickIt
├── domain
│   ├── user
│   │   ├── User
│   │   ├── UserController
│   │   ├── UserService
│   │   ├── UserServiceImpl
│   │   ├── UserRepository
│   │   └── dto
│   │       ├── UserRequestDto
│   │       └── UserResponseDto
│   ├── [도메인명]
│   │   ├── [도메인명]
│   │   ├── [도메인명]Controller
│   │   ├── [도메인명]Service
│   │   ├── [도메인명]ServiceImpl
│   │   ├── [도메인명]Repository
│   │   └── dto
│   └── ...
├── global
│   ├── config
│   │   └── SecurityConfiguration
│   ├── exception
│   └── util
└── PickItApplication
```

---

## 📐 네이밍 컨벤션

### 클래스 네이밍

| 분류 | 패턴 | 예시 |
|------|------|------|
| 컨트롤러 | `[도메인명]Controller` | `UserController` |
| 서비스 인터페이스 | `[도메인명]Service` | `UserService` |
| 서비스 구현체 | `[도메인명]ServiceImpl` | `UserServiceImpl` |
| 리포지토리 | `[도메인명]Repository` | `UserRepository` |
| 엔티티 | `[도메인명]` | `User` |
| 요청 DTO | `[도메인명]RequestDto` | `UserRequestDto` |
| 응답 DTO | `[도메인명]ResponseDto` | `UserResponseDto` |
| 설정 클래스 | `[기능]Configuration` | `SecurityConfiguration` |

### 인터페이스 & 구현체

```java
// 인터페이스
public interface UserService {
    UserResponseDto getUser(Long id);
}

// 구현체
public class UserServiceImpl implements UserService {
    @Override
    public UserResponseDto getUser(Long id) {
        // 구현 내용
    }
}
```

---

## 🌿 브랜치 전략

`Github Flow` 기반으로 운영합니다.

```
main ← develop ← feat/*
```

| 브랜치 | 설명 |
|--------|------|
| `main` | 배포용 안정 브랜치. PR로만 병합, 직접 커밋 금지 |
| `develop` | 통합 개발 브랜치. feature 브랜치들이 병합되는 곳 |
| `feat/{도메인}-{이슈번호}-{기능명}` | 기능 단위 개발 브랜치 (예: `feat/auth-3-login`) |

### 작업 흐름

1. 작업 단위로 **Issue** 생성
2. `develop` 브랜치에서 `feat/` 브랜치 분기
3. 작업 완료 후 `develop`으로 **PR** 생성
4. **2인 이상 코드 리뷰 및 승인** 후 병합
5. 배포 가능 상태 확인 후 `develop` → `main` PR 생성

---

## ✍️ 커밋 컨벤션

```
Type: 제목 (#이슈번호)

본문 (선택) - 무엇을, 왜 변경했는지 설명

Resolves: #이슈번호
```

### 커밋 타입

| Type | 설명 | 예시 |
|------|------|------|
| `Feat` | 새로운 기능 추가 | `Feat: 로그인 API 추가 (#3)` |
| `Fix` | 버그 수정 | `Fix: 토큰 갱신 오류 수정 (#7)` |
| `Refactor` | 코드 리팩토링 (기능 변경 없음) | `Refactor: 유저 서비스 로직 분리` |
| `Docs` | 문서 수정 | `Docs: README 업데이트` |
| `Style` | 코드 스타일 변경 (로직 영향 없음) | `Style: import 정리` |
| `Test` | 테스트 코드 추가/수정 | `Test: 유저 저장 테스트 추가` |
| `Chore` | 빌드/설정/기타 작업 | `Chore: Gradle 의존성 업데이트` |
| `Init` | 프로젝트 초기 세팅 | `Init: Spring Boot 프로젝트 초기화` |
| `Rename` | 파일/폴더명 변경 | `Rename: UserDto → UserResponseDto` |
| `Remove` | 파일/코드 삭제 | `Remove: 미사용 테스트 코드 삭제` |
| `Merge` | 브랜치 병합 | `Merge: feat/user-1-signup` |
| `!HOTFIX` | 운영 긴급 수정 | `!HOTFIX: 로그인 실패 긴급 수정` |

### 예시

```
Feat: 아이템 찜하기 API 추가 (#12)

사용자가 아이템을 찜 목록에 추가/제거할 수 있는 API를
구현했습니다.

Resolves: #12
```

---

## 📋 Issue / PR 규칙

### Issue
```
[Tag] 제목
```
- Labels, Assignees 지정 필수
- 이슈 요약 / 상세 내용 / 체크리스트 / 참고 사항 포함

### Pull Request
```
[Tag] 제목
```
- Labels, Assignees, Reviewers 지정 필수
- 관련 이슈 / 작업 내용 / 테스트 결과 / 참고 사항 포함
- **2인 이상 승인 후 병합**
