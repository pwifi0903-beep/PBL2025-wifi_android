package com.wisafe.app;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private WebView webView;
    private WiFiScanHelper wifiScanHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // WiFi 스캔 헬퍼 초기화
        wifiScanHelper = new WiFiScanHelper(this);

        // WebView 설정
        webView = findViewById(R.id.webview);
        setupWebView();

        // 권한 확인 및 요청
        checkPermissions();
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        
        // 로컬 파일 접근 허용
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            webSettings.setAllowFileAccessFromFileURLs(true);
            webSettings.setAllowUniversalAccessFromFileURLs(true);
        }

        // JavaScript Bridge 추가
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        // WebViewClient 설정
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        // 로컬 서버 URL 로드 (Flask 서버가 실행 중이어야 함)
        // 실제 배포 시에는 서버 URL로 변경
        String serverUrl = "http://10.0.2.2:5000/user"; // 에뮬레이터용
        // String serverUrl = "http://YOUR_SERVER_IP:5000/user"; // 실제 기기용
        
        webView.loadUrl(serverUrl);
    }

    private void checkPermissions() {
        String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        };

        boolean allGranted = true;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                    != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (!allGranted) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (!allGranted) {
                Toast.makeText(this, "WiFi 스캔을 위해 위치 권한이 필요합니다.", 
                             Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * JavaScript와 통신하기 위한 인터페이스
     */
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * WiFi 스캔 시작
         * JavaScript에서 Android.startWiFiScan()으로 호출
         */
        @JavascriptInterface
        public void startWiFiScan() {
            runOnUiThread(() -> {
                if (!wifiScanHelper.hasPermissions()) {
                    checkPermissions();
                    return;
                }

                // WiFi 스캔 실행
                wifiScanHelper.scanWiFi(new WiFiScanHelper.ScanCallback() {
                    @Override
                    public void onScanComplete(List<WiFiScanHelper.WiFiNetwork> networks) {
                        // 스캔 결과를 JSON으로 변환하여 JavaScript로 전달
                        JSONArray jsonArray = new JSONArray();
                        for (WiFiScanHelper.WiFiNetwork network : networks) {
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("ssid", network.ssid);
                                jsonObject.put("protocol", network.protocol);
                                jsonObject.put("security_level", network.securityLevel);
                                jsonArray.put(jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // JavaScript 함수 호출
                        final String jsCode = String.format(
                            "if (typeof handleAndroidWiFiScan === 'function') { " +
                            "  handleAndroidWiFiScan(%s); " +
                            "} else { " +
                            "  console.log('WiFi scan result:', %s); " +
                            "}",
                            jsonArray.toString(),
                            jsonArray.toString()
                        );

                        runOnUiThread(() -> {
                            webView.evaluateJavascript(jsCode, null);
                        });
                    }

                    @Override
                    public void onScanError(String error) {
                        final String jsCode = String.format(
                            "if (typeof handleAndroidWiFiScanError === 'function') { " +
                            "  handleAndroidWiFiScanError('%s'); " +
                            "} else { " +
                            "  console.error('WiFi scan error:', '%s'); " +
                            "}",
                            error,
                            error
                        );

                        runOnUiThread(() -> {
                            webView.evaluateJavascript(jsCode, null);
                        });
                    }
                });
            });
        }

        /**
         * 권한 확인
         */
        @JavascriptInterface
        public boolean hasPermissions() {
            return wifiScanHelper.hasPermissions();
        }
    }
}
