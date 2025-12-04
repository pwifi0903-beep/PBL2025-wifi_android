# WiSafe Android App

WiFi 보안 스캔 안드로이드 앱

## 개요

WiSafe는 주변 WiFi 네트워크의 보안 상태를 분석하고 평가하는 Android 앱입니다.  실시간으로 WiFi 보안 정보를 제공합니다.

## 주요 기능

### 1. WiFi 네트워크 스캔
- 주변 WiFi 네트워크 자동 스캔
- 실시간 네트워크 검색 및 목록 표시
- 신호 강도 기반 정렬 (강함/보통/약함)
- 스캔 결과를 신호 강도 순으로 정렬하여 표시

### 2. 보안 프로토콜 분석
다양한 WiFi 보안 프로토콜을 자동으로 감지하고 분석합니다:
- **WPA3**: 최신 보안 프로토콜 (가장 안전)
- **WPA2-PERSONAL**: 개인용 WPA2 (안전)
- **WPA2-ENTERPRISE**: 기업용 WPA2 (안전)
- **WPA**: 구형 보안 프로토콜 (주의 필요)
- **WEP**: 취약한 보안 프로토콜 (위험)
- **OPEN**: 비밀번호 없는 공개 네트워크 (매우 위험)

### 3. 보안 수준 평가
각 네트워크의 보안 상태를 4단계로 평가합니다:
- **안전 (Safe)**: WPA2, WPA3 프로토콜 사용
- **주의 (Warning)**: WPA 프로토콜 사용
- **위험 (Danger)**: WEP 프로토콜 사용
- **매우 위험 (Critical)**: 비밀번호 없는 공개 네트워크

### 4. 가짜 와이파이(Rogue AP) 탐지
- 동일한 SSID를 가진 네트워크 중 보안 수준이 다른 경우 자동 감지
- 공격자가 만든 가짜 네트워크 의심 시 경고 표시
- 예: "MyWiFi"라는 이름의 WPA2 네트워크가 있는데, 같은 이름의 OPEN 네트워크가 함께 발견되면 경고

### 5. 현재 연결된 WiFi 정보 표시
- 현재 연결 중인 WiFi 네트워크 정보 자동 표시
- 연결된 네트워크의 보안 상태 실시간 확인
- 가짜 와이파이 의심 시 즉시 경고

### 6. 상세 정보 및 보안 가이드
- 각 네트워크 클릭 시 상세 정보 팝업 표시
- 프로토콜별 보안 설명 및 권장 사항 제공
- 가짜 와이파이 의심 시 대응 방법 안내
- 공통 보안 팁 제공

### 7. 신호 강도 표시
- 각 네트워크의 신호 강도를 dBm 단위로 표시
- 신호 강도를 "강함/보통/약함"으로 분류
- 신호 강도에 따른 네트워크 품질 파악 가능

## 요구사항

- Android Studio Hedgehog (2023.1.1) 이상
- Android SDK 23 (Android 6.0) 이상
- Gradle 8.0 이상


## 설정

### 권한

앱은 다음 권한을 요청합니다:
- `ACCESS_FINE_LOCATION` - WiFi 스캔을 위해 필요 (Android 6.0+)
- `ACCESS_COARSE_LOCATION` - WiFi 스캔을 위해 필요
- `ACCESS_WIFI_STATE` - WiFi 상태 확인
- `CHANGE_WIFI_STATE` - WiFi 스캔 시작
- `INTERNET` - 웹 페이지 로드

## 사용 방법

1. **앱 실행 및 권한 허용**
   - 앱을 실행하면 위치 권한 요청이 나타납니다.
   - WiFi 스캔을 위해 위치 권한을 허용해야 합니다.
   - Android 6.0 이상에서는 런타임 권한 요청이 표시됩니다.

2. **현재 연결된 WiFi 확인**
   - 앱 실행 시 자동으로 현재 연결된 WiFi 정보가 표시됩니다.
   - 연결된 네트워크의 보안 상태를 즉시 확인할 수 있습니다.

3. **WiFi 네트워크 스캔**
   - "와이파이 스캔 시작" 버튼을 클릭합니다.
   - 안드로이드 네이티브 WiFi 스캔이 실행되어 주변 네트워크를 검색합니다.
   - 스캔 중에는 "스캔 중..." 메시지가 표시됩니다.

4. **스캔 결과 확인**
   - 스캔이 완료되면 검색된 네트워크 목록이 표시됩니다.
   - 각 네트워크는 보안 수준 배지(안전/주의/위험/매우 위험)로 표시됩니다.
   - 신호 강도 순으로 정렬되어 표시됩니다.
   - 가짜 와이파이 의심 네트워크는 "가짜 와이파이 의심됩니다" 경고가 표시됩니다.

5. **상세 정보 확인**
   - 네트워크 항목을 클릭하면 상세 정보 팝업이 표시됩니다.
   - 프로토콜, 신호 강도, 위험도 등 상세 정보를 확인할 수 있습니다.
   - 프로토콜별 보안 설명 및 권장 사항을 확인할 수 있습니다.
   - 가짜 와이파이 의심 시 대응 방법 안내를 확인할 수 있습니다.

## 프로젝트 구조

```
android_wifi-sceurity_scanner/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/wisafe/app/
│   │       │   ├── MainActivity.java      # 메인 액티비티 (WebView 설정 및 JavaScript Bridge)
│   │       │   └── WiFiScanHelper.java   # WiFi 스캔 헬퍼 (네트워크 스캔 및 보안 분석)
│   │       ├── assets/
│   │       │   └── index.html            # 웹 UI (HTML/CSS/JavaScript)
│   │       ├── res/
│   │       │   ├── layout/
│   │       │   │   └── activity_main.xml # 메인 레이아웃
│   │       │   └── values/
│   │       │       ├── strings.xml       
│   │       │       └── styles.xml       
│   │       └── AndroidManifest.xml       
│   └── build.gradle                      
├── build.gradle                          
├── settings.gradle                      
└── gradle.properties                    
```

## 보안 분석 로직

### 프로토콜 감지 알고리즘

`WiFiScanHelper.java`의 `getSecurityProtocol()` 메서드는 Android의 `ScanResult.capabilities` 문자열을 분석하여 보안 프로토콜을 감지합니다:

1. **WPA3 감지**: capabilities 문자열에 "WPA3" 포함 여부 확인
2. **WPA2 감지**: capabilities 문자열에 "WPA2" 포함 여부 확인
   - "EAP" 또는 "ENTERPRISE" 포함 시 → WPA2-ENTERPRISE
   - "PSK" 또는 "SAE" 포함 시 → WPA2-PERSONAL
3. **WPA 감지**: capabilities 문자열에 "WPA" 포함 (WPA2, WPA3 제외)
4. **WEP 감지**: capabilities 문자열에 "WEP" 포함
5. **OPEN**: 위 조건에 해당하지 않거나 capabilities가 비어있는 경우

### 보안 수준 평가 기준

`getSecurityLevel()` 메서드는 프로토콜에 따라 보안 수준을 평가합니다:

- **Safe (안전)**: WPA2-PERSONAL, WPA2-ENTERPRISE, WPA3
- **Warning (주의)**: WPA
- **Danger (위험)**: WEP
- **Critical (매우 위험)**: OPEN (비밀번호 없음)

### 가짜 와이파이 탐지 알고리즘

1. 스캔 결과를 SSID별로 그룹화
2. 동일한 SSID를 가진 네트워크 중:
   - 보안 프로토콜이 있는 네트워크(WPA2, WPA3 등)가 존재하고
   - 동시에 OPEN(비밀번호 없음) 네트워크가 존재하는 경우
3. OPEN 네트워크를 가짜 와이파이로 의심하여 경고 표시

이 로직은 공격자가 진짜 네트워크와 같은 이름의 비밀번호 없는 가짜 네트워크를 만들어 사용자를 유인하는 공격을 탐지합니다.

## JavaScript Bridge API

웹 페이지에서 안드로이드 네이티브 기능을 호출하기 위한 JavaScript 인터페이스입니다.

### `Android.startWiFiScan()`
WiFi 스캔을 시작합니다. 스캔이 완료되면 `handleAndroidWiFiScan()` 함수가 자동으로 호출됩니다.

```javascript
Android.startWiFiScan();
```

### `Android.hasPermissions()`
필요한 권한이 모두 허용되었는지 확인합니다.

```javascript
const hasPermissions = Android.hasPermissions();
```

### `Android.getCurrentWiFiInfo()`
현재 연결된 WiFi 네트워크 정보를 JSON 문자열로 반환합니다.

```javascript
const wifiInfo = Android.getCurrentWiFiInfo();
// 예: {"ssid":"MyWiFi","protocol":"WPA2-PERSONAL","security_level":"safe",...}
```

### 콜백 함수

웹 페이지에서 구현해야 하는 콜백 함수들:

- `handleAndroidWiFiScan(networks)`: 스캔 완료 시 호출됩니다. 네트워크 배열을 인자로 받습니다.
- `handleAndroidWiFiScanError(error)`: 스캔 실패 시 호출됩니다. 에러 메시지를 인자로 받습니다.

## 기술 스택

- **언어**: Java
- **최소 SDK**: Android 6.0 (API 23)
- **타겟 SDK**: Android 14 (API 34)
- **UI**: WebView + HTML/CSS/JavaScript
- **주요 라이브러리**:
  - AndroidX AppCompat
  - Material Design Components
  - ConstraintLayout

## 주의사항

- **위치 권한 필수**: WiFi 스캔은 Android 6.0 이상에서 위치 권한이 필요합니다. 위치 권한을 허용하지 않으면 스캔이 작동하지 않습니다.
- **WiFi 활성화 필요**: WiFi가 비활성화되어 있으면 스캔할 수 없습니다.
- **Android 10 이상**: Android 10 이상에서는 위치 서비스가 켜져 있어야 WiFi 스캔이 가능합니다.
- **서버 불필요**: 이 앱은 로컬 HTML 파일을 사용하므로 별도의 서버가 필요하지 않습니다.

## 문제 해결

### WiFi 스캔이 작동하지 않는 경우
- 위치 권한이 허용되었는지 확인
- WiFi가 활성화되어 있는지 확인
- Android 10 이상에서는 위치 서비스가 켜져 있어야 합니다.

### 웹 페이지가 로드되지 않는 경우
- 앱을 재시작해보세요
- `app/src/main/assets/index.html` 파일이 존재하는지 확인하세요
- WebView가 제대로 초기화되었는지 확인하세요

### 가짜 와이파이 경고가 잘못 표시되는 경우
- 가짜 와이파이 탐지는 동일한 SSID를 가진 네트워크 중 보안 수준이 다른 경우를 감지합니다.
- 일부 공공장소에서는 의도적으로 OPEN 네트워크를 제공할 수 있으므로, 실제 상황을 고려하여 판단하세요.