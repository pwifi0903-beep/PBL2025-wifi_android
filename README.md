# WiSafe Android App

WiFi 보안 스캔 안드로이드 앱

## 개요

이 앱은 웹 기반 WiFi 보안 스캔 솔루션을 안드로이드 앱으로 포팅한 것입니다. WebView를 사용하여 웹 페이지를 표시하고, 안드로이드 네이티브 WiFi 스캔 기능을 JavaScript Bridge를 통해 연동합니다.

## 주요 기능

- WebView를 통한 웹 페이지 표시
- 안드로이드 네이티브 WiFi 스캔 기능
- JavaScript Bridge를 통한 웹-네이티브 통신
- 실시간 WiFi 보안 상태 확인

## 요구사항

- Android Studio Hedgehog (2023.1.1) 이상
- Android SDK 23 (Android 6.0) 이상
- Gradle 8.0 이상

## 빌드 방법

1. Android Studio에서 프로젝트 열기
2. `android` 폴더를 프로젝트 루트로 선택
3. Gradle 동기화
4. 빌드 및 실행

## 설정

### 서버 URL 설정

`MainActivity.java` 파일에서 서버 URL을 설정해야 합니다:

```java
// 에뮬레이터용 (로컬 서버)
String serverUrl = "http://10.0.2.2:5000/user";

// 실제 기기용 (같은 네트워크에 있는 서버)
String serverUrl = "http://YOUR_SERVER_IP:5000/user";
```

### 권한

앱은 다음 권한을 요청합니다:
- `ACCESS_FINE_LOCATION` - WiFi 스캔을 위해 필요 (Android 6.0+)
- `ACCESS_COARSE_LOCATION` - WiFi 스캔을 위해 필요
- `ACCESS_WIFI_STATE` - WiFi 상태 확인
- `CHANGE_WIFI_STATE` - WiFi 스캔 시작
- `INTERNET` - 웹 페이지 로드

## 사용 방법

1. 앱 실행 시 위치 권한 요청이 나타납니다. 허용해야 WiFi 스캔이 가능합니다.
2. 웹 페이지가 로드되면 "와이파이 스캔" 버튼을 클릭합니다.
3. 안드로이드 네이티브 WiFi 스캔이 실행되어 주변 WiFi 네트워크를 스캔합니다.
4. 스캔 결과가 웹 페이지에 표시됩니다.

## 프로젝트 구조

```
android/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/wisafe/app/
│   │       │   ├── MainActivity.java      # 메인 액티비티 (WebView)
│   │       │   └── WiFiScanHelper.java   # WiFi 스캔 헬퍼
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml
│   │       │   └── values/
│   │       │       ├── strings.xml
│   │       │       └── styles.xml
│   │       └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## 주의사항

- WiFi 스캔은 Android 6.0 이상에서 위치 권한이 필요합니다.
- 실제 기기에서 테스트할 때는 Flask 서버가 실행 중이어야 합니다.
- 에뮬레이터에서는 `10.0.2.2`를 사용하여 호스트 머신의 로컬 서버에 접근할 수 있습니다.

## 문제 해결

### WiFi 스캔이 작동하지 않는 경우
- 위치 권한이 허용되었는지 확인
- WiFi가 활성화되어 있는지 확인
- Android 10 이상에서는 위치 서비스가 켜져 있어야 합니다.

### 웹 페이지가 로드되지 않는 경우
- Flask 서버가 실행 중인지 확인
- 서버 URL이 올바른지 확인
- 네트워크 연결 상태 확인
