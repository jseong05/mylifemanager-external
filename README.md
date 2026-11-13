# 📱 마이 라이프 매니저 External

> **AI 재무 코칭과 가계부·일정 관리, 홈 화면 위젯을 하나로 담은 Android 앱.**

[English](README_EN.md) · [원본 앱 저장소](https://github.com/jseong05/mylifemanager) · [External 저장소](https://github.com/jseong05/mylifemanager-external)

## ✨ 프로젝트 소개

마이 라이프 매니저 External은 기존 [마이 라이프 매니저](https://github.com/jseong05/mylifemanager)의 가계부·일정 관리 기능을 바탕으로 AI 재무 상담, 홈 화면 위젯, 데이터 백업을 더한 실험용 Android 앱입니다. 원본 앱과 함께 설치할 수 있도록 앱 ID를 `com.mlm.external`로 분리했습니다.

## 🆕 원본 대비 추가·변경 사항

| 영역 | External 버전의 추가·변경 사항 |
| --- | --- |
| AI 자산 관리 | Groq의 OpenAI 호환 Chat Completions API와 `llama-3.3-70b-versatile` 모델을 사용하는 AI 채팅, 저장되는 대화 기록, 세 가지 응답 페르소나 |
| 오프라인 응답 | API 키가 없거나 네트워크/API 요청에 실패하면 앱 내부 규칙 기반 응답으로 대체 |
| 대시보드 | 저축 목표 달성률 및 AI 채팅 바로가기 추가 |
| 홈 화면 위젯 | 현재 잔액·오늘 지출·권장 지출액 위젯과 오늘 일정 미리보기 위젯 추가 |
| 가계부 | 지출 카테고리, 지출/수입/낭비 필터, 카테고리별 지출 통계 추가 |
| 백업·복원 | JSON 형식으로 앱 데이터 내보내기 및 복원 |
| 알림 | 기존 일정·지출 기록 알림에 매일 오전 8시 모닝 브리핑 및 매주 일요일 오후 8시 리포트 추가 |
| 설정 | AI 페르소나, 통화 단위(KRW·USD·JPY·EUR) 선택 추가 |
| 앱 식별자 | 원본 앱과 별도 설치하도록 패키지/애플리케이션 ID를 `com.mlm.external`로 변경 |

### 최신 변경 사항

2026년 11월 13일, 온라인 AI 연결을 Gemini에서 Groq로 전환했습니다. 현재 `AiClient`는 Groq의 OpenAI 호환 Chat Completions API와 `llama-3.3-70b-versatile` 모델을 사용합니다. API를 사용할 수 없으면 `OfflineAiEngine`의 로컬 규칙 기반 응답으로 대체합니다.

## 🌟 주요 기능

### 🏠 대시보드
- 사용자 이름, 날짜, 현재 잔액과 오늘 지출 표시
- 용돈 지급일까지 남은 날짜와 오늘 권장 지출액 계산
- 오늘 일정 및 최근 가계부 내역 요약
- GPS 또는 설정한 도시의 날씨 표시
- 저축 목표 달성률 표시 및 AI 채팅 바로가기

### 💰 가계부
- 수입·지출 내역 추가, 수정, 스와이프로 삭제
- 내역 및 카테고리 검색
- 지출·수입·낭비 내역 필터
- 식비, 교통, 쇼핑, 문화, 기타 카테고리 지정 및 카테고리별 지출 통계
- 낭비 지출 표시와 이번 달 낭비 건수·금액 집계
- 초기 잔액과 내역을 바탕으로 현재 잔액 계산

### 📅 계획표와 알림
- 달력에서 일정을 확인하고 일정이 있는 날짜 표시
- 일정 추가, 수정, 완료 표시, 검색 및 스와이프 삭제
- 반복 주기 설정: 반복 없음, 매주, 매월, 매년
- 일정 시각 또는 사용자 지정 시간 전 알림
- 매일 오전 8시 모닝 브리핑, 매주 일요일 오후 8시 리포트, 매일 오후 9시 지출 기록 리마인더

### 🤖 AI 자산 관리
- 현재 잔액과 오늘 지출을 참고한 재무 관련 대화
- 페르소나 선택: 팩폭형 코치, 천사형 상담사, 냉철한 재무관
- 채팅 기록을 기기에 저장
- Groq API를 사용할 수 없을 때 로컬 규칙 기반 응답 제공

> 오프라인 응답은 외부 생성형 AI 모델 호출이 아닌 앱 내부의 키워드·페르소나 기반 응답입니다.

### 📱 홈 화면 위젯
- **잔액 위젯:** 현재 잔액, 오늘 지출, 권장 지출액 및 용돈 지급일까지 남은 날짜
- **일정 위젯:** 오늘 일정 미리보기

### ⚙️ 설정 및 데이터 관리
- 사용자 이름, 초기 잔액, 용돈 금액 및 지급 주기 설정
- 저축 목표, 통화 표시 단위(KRW·USD·JPY·EUR) 설정
- GPS 사용 여부 및 날씨 조회 도시 설정
- JSON 백업 내보내기·복원 및 앱 데이터 초기화

## 🛠️ 기술 스택

| 구분 | 기술 |
| --- | --- |
| 언어 | Java (소스 호환성 11) |
| Android | 최소 SDK 24, Compile SDK 37, Target SDK 37 |
| 빌드 | Android Gradle Plugin 9.4.0, Gradle 9.6.0 |
| UI | Android Views, View Binding, Material Components, ConstraintLayout |
| 로컬 데이터 | `SharedPreferences`, Gson |
| 일정 달력 | Material CalendarView |
| 네트워크 | `HttpURLConnection`, OpenWeatherMap API, Groq OpenAI 호환 API |
| 알림·위젯 | `AlarmManager`, `BroadcastReceiver`, `NotificationManager`, Android App Widgets |

## 📂 프로젝트 구조

```text
app/src/main/java/com/mlm/external/
├── MainActivity.java                  # 대시보드 및 정기 알림 예약
├── ExpenseActivity.java               # 가계부, 검색·필터·카테고리 통계
├── ScheduleActivity.java              # 달력, 일정 관리 및 알림
├── SettingsActivity.java              # 설정, AI·통화·백업/복원
├── AiChatActivity.java                # AI 채팅 및 대화 기록
├── AiClient.java                      # Groq API 요청
├── OfflineAiEngine.java               # 로컬 규칙 기반 대체 응답
├── MyDataStore.java                   # SharedPreferences/Gson 데이터 관리
├── BalanceWidgetProvider.java          # 잔액 위젯
├── ScheduleWidgetProvider.java         # 일정 위젯
├── MorningBriefingAlarmReceiver.java  # 아침 브리핑 알림
├── WeeklyReportAlarmReceiver.java     # 주간 리포트 알림
├── ScheduleAlarmReceiver.java         # 일정 알림
├── DailyExpenseAlarmReceiver.java     # 지출 기록 리마인더
└── WeatherClient.java                 # 날씨 API 클라이언트

app/src/main/res/
├── layout/                            # 화면, 목록, 채팅 및 위젯 레이아웃
├── menu/                              # 하단 내비게이션 메뉴
├── values/                            # 문자열, 테마, 색상 및 설정 선택지
└── xml/                               # 앱 위젯 및 데이터 백업 설정
```

## 🚀 빌드 및 실행

1. Android Studio에서 프로젝트를 엽니다.
2. Gradle Sync를 수행합니다.
3. API 키를 사용할 경우 프로젝트 루트의 `local.properties`에 설정합니다.
4. Android 7.0(API 24) 이상 에뮬레이터 또는 기기에서 `app` 모듈을 실행합니다.

명령줄 빌드 및 로컬 단위 테스트:

```bash
./gradlew assembleDebug
./gradlew test
```

## 🔑 API 키 설정

날씨 조회와 온라인 AI 채팅에 API 키를 사용할 수 있습니다. 프로젝트 루트의 `local.properties`에 다음 값을 설정하세요.

```properties
weatherApiKey=YOUR_OPENWEATHERMAP_API_KEY
aiApiKey=gsk_YOUR_GROQ_API_KEY
```

- 날씨 기능은 OpenWeatherMap API 키를 사용합니다.
- 온라인 AI 응답은 Groq API 키를 사용합니다. 키가 없거나 요청이 실패하면 로컬 대체 응답을 사용합니다.
- API 키를 저장소에 커밋하지 마세요. `local.properties`는 `.gitignore`에 포함되어 있습니다.
- 앱 클라이언트에 포함된 API 키는 배포된 APK에서 추출될 수 있습니다. 실제 배포에서 키를 보호하려면 서버 프록시를 통한 API 호출을 고려하세요.
- GPS 날씨를 사용하려면 위치 권한을 허용해야 합니다. 권한이 없거나 위치를 가져올 수 없으면 설정한 도시를 사용합니다.
