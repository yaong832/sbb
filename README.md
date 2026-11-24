# SBB - AI 장비 알람 판정 플랫폼

Spring Boot 기반의 웹 애플리케이션으로, 센서 데이터를 학습·추론하여 장비 이상 여부를 판정하고 알람을 관리합니다. 기존 SBB 게시판(Q&A) 기능과 AI 파이프라인이 통합되어 있습니다.

## 기술 스택

| 항목 | 기술 |
|------|------|
| 언어 | Java 17 |
| 프레임워크 | Spring Boot 4.0.3 |
| ORM | Spring Data JPA / Hibernate |
| DB | H2 (인메모리/파일) |
| 템플릿 | Thymeleaf + Layout Dialect |
| 빌드 | Gradle |
| 기타 | Lombok, DevTools |

## 주요 기능

### 1. 게시판 (Q&A)
- 질문 목록/상세/작성/수정/삭제
- 답변 등록

### 2. 센서 학습 데이터 (`/sensor`)
- 장비명, 온도, 압력, 진동, 가스 유량, 라벨(NORMAL/ABNORMAL) 관리
- 테스트용 샘플 데이터 자동 생성

### 3. 모델 학습 (`/model`)
- NORMAL 라벨 센서 데이터의 평균값을 기준 모델로 저장
- 학습 결과(평균값, 학습 데이터 수, 학습 일시) 조회

### 4. AI 추론 (`/predict`)
- 입력 센서값과 학습 모델 평균의 편차를 점수화하여 NORMAL/ABNORMAL 판정
- 위험 점수(0~100) 및 판정 사유 제공
- ABNORMAL 시 자동 알람 생성
- 사용자 피드백 수집

### 5. 장비 알람 (`/alarm`)
- 수동/자동(AI) 알람 등록 및 목록 조회
- 알람 상세 및 조치 메모 기록

### 6. 모델 검증 (`/validation`)
- 전체 학습 데이터에 대해 예측 정확도(accuracy) 계산

## AI 판정 로직

학습된 모델의 평균값과 입력값의 차이를 기준으로 점수를 합산합니다.

| 항목 | 임계값 | 가중치 |
|------|--------|--------|
| 온도 | ±15 | 30 |
| 압력 | ±1.5 | 25 |
| 진동 | ±0.8 | 25 |
| 가스 유량 | ±40 | 20 |

- 합산 점수 **25점 이상** → `ABNORMAL`
- 그 미만 → `NORMAL`

## 프로젝트 구조

```
src/main/java/com/mysite/sbb/
├── alarm/          # 장비 알람 (Entity, Service, Controller, Repository)
├── answer/         # 게시판 답변
├── model/          # 학습 모델 정보
├── predict/        # AI 추론 및 결과
├── question/       # 게시판 질문
├── sensor/         # 센서 학습 데이터
├── validation/     # 모델 검증
└── SbbApplication.java
```

## 실행 방법

```bash
# Windows
gradlew.bat bootRun

# Linux / macOS
./gradlew bootRun
```

애플리케이션 실행 후 브라우저에서 접속:

- 메인: http://localhost:8080/
- H2 콘솔: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:~/local`
  - Username: `sa`
  - Password: (비어 있음)

## API 엔드포인트 요약

| 경로 | 설명 |
|------|------|
| `/question/list` | 질문 목록 |
| `/sensor/list` | 센서 데이터 목록 |
| `/sensor/generate` | 샘플 데이터 생성 |
| `/model/train` | 모델 학습 실행 |
| `/model/info` | 학습 결과 조회 |
| `/predict/form` | 추론 입력 폼 |
| `/predict/run` | 추론 실행 |
| `/predict/list` | 추론 이력 |
| `/alarm/list` | 알람 목록 |
| `/validation/run` | 검증 실행 |
| `/validation/result` | 검증 결과 |

## 테스트

```bash
gradlew.bat test
```
