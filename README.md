# 📱 마이 라이프 매니저 External

> AI 재무 코칭과 가계부·일정 관리, 홈 화면 위젯을 하나로 담은 Android 앱.

[English](README_EN.md) · [원본 앱 저장소](https://github.com/jseong05/mylifemanager) · [External 저장소](https://github.com/jseong05/mylifemanager-external)

## 프로젝트 소개

마이 라이프 매니저 External은 기존 [마이 라이프 매니저](https://github.com/jseong05/mylifemanager)의 가계부·일정 관리 기능을 바탕으로 AI 재무 상담, 홈 화면 위젯, 데이터 백업을 더한 실험용 Android 앱입니다. 원본 앱과 함께 설치할 수 있도록 앱 ID를 `com.mlm.external`로 분리했습니다.

## 주요 기능

- AI 기반 소비/저축 코칭
- 가계부와 일정 관리
- 날씨, 잔액, 권장 지출 계산
- 홈 화면 위젯 및 알림
- JSON 백업/복원 및 통화 단위 지원

## 기술 스택

- Java, Android
- SharedPreferences, Gson
- Material CalendarView
- OpenWeatherMap API, Groq API
- AlarmManager, BroadcastReceiver, NotificationManager

## 프로젝트 설명

기존 생활관리 앱을 확장해 AI로 소비 습관을 돕고, 일정과 자산을 한 번에 관리할 수 있도록 만든 실험 프로젝트입니다.

## 한 줄 소개

AI 재무 코칭과 가계부·일정 관리를 하나로 합친 실험형 Android 앱.
