# BeadyEyes 👁️

<img width="2095" height="1289" alt="목업 (2)" src="https://github.com/user-attachments/assets/cefb8414-e177-4815-8326-928bf5c1e904" />


> GDSC Solution Challenge 2024 출품작  
> 저시력자를 위한 실시간 글씨 및 지폐 인식 음성 안내 애플리케이션

## 📖 프로젝트 소개

BeadyEyes는 시각장애인과 저시력자의 일상생활을 돕기 위해 개발된 Android 애플리케이션입니다.  
카메라와 ML 모델을 활용하여 실시간으로 문자와 화폐를 인식하고, 음성으로 안내하여 시각적 정보 접근성을 높입니다.

<img width="660" height="452" alt="스크린샷 2025-10-22 오전 10 05 46" src="https://github.com/user-attachments/assets/a276c3a6-a5b0-48f8-8d82-d95530cea870" />


## ✨ 주요 기능

- **실시간 문자 인식**: Google ML Kit 기반 한글/영문 텍스트 실시간 인식 및 음성 안내
- **화폐 인식**: TFLite 모델을 활용한 한국/미국 지폐 및 한국 동전 분류
- **음성 내비게이션**: Google STT/TTS를 이용한 음성 명령 기반 화면 전환
- **접근성 중심 UX**: 시각장애인을 고려한 직관적인 음성 피드백 및 간편한 조작

## 🛠️ 기술 스택

### Android
- **Language**: Kotlin
- **UI**: Jetpack Compose, Material3
- **Camera**: Compose CameraX, Camera Preview
- **ML**: TensorFlow Lite, Google ML Kit (Text Recognition, Google Vision API)

### Backend & Cloud
- Spring, Firebase, Docker, Github Actions

### 개발 환경
- Android Studio (API Level 34)
- Gradle 8.6
- Java 17

## 📂 프로젝트 구조

```
app/
├── src/main/
│   ├── java/com/pointer/beadyeyes/
│   │   ├── MainActivity.kt
│   │   ├── ui/
│   │   │   ├── main/          # 메인 화면 (ViewPager 기반 화면 전환)
│   │   │   ├── pointer/       # 문자 인식 화면
│   │   │   ├── money/         # 화폐 인식 화면 (CameraX + TFLite)
│   │   │   ├── camera/        # 카메라 관련 공통 컴포넌트
│   │   │   ├── stt/           # 음성 인식 (Speech-to-Text)
│   │   │   └── tts/           # 음성 출력 (Text-to-Speech)
│   │   └── util/              # 유틸리티 (BitmapUtils 등)
│   ├── ml/
│   │   └── model.tflite       # 화폐 인식용 TFLite 모델
│   └── res/                   # 리소스 파일
└── build.gradle
```

## 🎯 핵심 구현 내용

### 1. 이미지 처리 파이프라인
CameraX로 캡처한 이미지를 Bitmap → ByteBuffer로 변환하고, 리사이즈 최적화를 거쳐 TFLite 모델 및 자체 AI API와 통신합니다.

### 2. 화폐 인식 모델
- 미국/한국 지폐 데이터 직접 수집 및 지도학습
- 1,000개의 한국 동전 데이터셋 구축 및 학습
- TFLite 모델을 Kotlin에서 ML Model Binding으로 통합

### 3. 음성 인터랙션
Google STT API를 활용하여 음성 명령으로 화면 전환이 가능하며, TTS API로 인식 결과를 즉시 음성으로 안내합니다.

### 4. 손가락 포인팅 부분 텍스트 인식
손가락 끝으로 가르킨 부분의 일정 영역에 텍스트가 있다면 이를 읽어, Google vision OCR 파인튜닝 AI모델 응답 결과를 반환하는 API 불러와 인식한 텍스트를 'Balloon'을 통해 텍스트 및 음성으로 반환해, 저시력자 사용자의 일상을 돕습니다. (키오스크, 전단지, 간판 등)

## 🚀 실행 방법

### 요구사항
- Android Studio (최신 Stable 버전 권장)
- Android SDK Platform 34 이상
- 실제 기기 또는 AVD (카메라 및 마이크 권한 필요)

### 실행 단계
1. Android Studio에서 프로젝트 열기
2. Gradle 동기화 완료 대기
3. 실제 기기 또는 AVD 선택
4. `Run ▶` 버튼 클릭하여 앱 빌드 및 실행

### 권한 안내
앱 실행 시 다음 권한이 필요합니다:
- 카메라 (문자/화폐 인식)
- 마이크 (음성 명령)

## 👥 팀 구성

**GDSC Solution Challenge Team 4** (2024.02)

- Backend Developer: 1명
- AI/ML Developer: 1명  
- Android Developer: 2명 (PM 포함)

## 📝 라이선스

이 프로젝트는 GDSC Solution Challenge 2024 출품작입니다.

---

**Made with ❤️ by GDSC Solution Challenge Team 4**
