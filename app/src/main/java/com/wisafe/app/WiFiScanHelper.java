package com.wisafe.app;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

public class WiFiScanHelper {
    private static final String TAG = "WiFiScanHelper";
    private static final int INVALID_RSSI = -127;
    private Context context;
    private WifiManager wifiManager;
    private ScanCallback scanCallback;
    private BroadcastReceiver wifiScanReceiver;

    public interface ScanCallback {
        void onScanComplete(List<WiFiNetwork> networks);
        void onScanError(String error);
    }

    public static class WiFiNetwork {
        public String ssid;
        public String protocol;
        public String securityLevel;
        public int signalStrength;
        public String bssid;

        public WiFiNetwork(String ssid, String protocol, String securityLevel, 
                         int signalStrength, String bssid) {
            this.ssid = ssid;
            this.protocol = protocol;
            this.securityLevel = securityLevel;
            this.signalStrength = signalStrength;
            this.bssid = bssid;
        }
    }

    public WiFiScanHelper(Context context) {
        this.context = context;
        this.wifiManager = (WifiManager) context.getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);
    }

    public boolean hasPermissions() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED &&
               ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) 
                == PackageManager.PERMISSION_GRANTED;
    }

    public void scanWiFi(ScanCallback callback) {
        if (!hasPermissions()) {
            callback.onScanError("위치 권한이 필요합니다.");
            return;
        }

        if (wifiManager == null) {
            callback.onScanError("WiFi 매니저를 사용할 수 없습니다.");
            return;
        }

        if (!wifiManager.isWifiEnabled()) {
            callback.onScanError("WiFi가 비활성화되어 있습니다.");
            return;
        }

        this.scanCallback = callback;

        // BroadcastReceiver 등록
        wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    processScanResults();
                } else {
                    if (scanCallback != null) {
                        scanCallback.onScanError("스캔 실패");
                    }
                }
                unregisterReceiver();
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.registerReceiver(wifiScanReceiver, intentFilter);

        // 스캔 시작
        boolean scanStarted = wifiManager.startScan();
        if (!scanStarted) {
            callback.onScanError("스캔을 시작할 수 없습니다.");
            unregisterReceiver();
        }
    }

    private void processScanResults() {
        if (!hasPermissions()) {
            if (scanCallback != null) {
                scanCallback.onScanError("권한이 없습니다.");
            }
            return;
        }

        List<ScanResult> scanResults = wifiManager.getScanResults();
        List<WiFiNetwork> networks = new ArrayList<>();

        for (ScanResult result : scanResults) {
            if (result.SSID == null || result.SSID.isEmpty()) {
                continue; // 숨겨진 네트워크는 SSID가 비어있을 수 있음
            }

            String protocol = getSecurityProtocol(result);
            String securityLevel = getSecurityLevel(protocol);

            WiFiNetwork network = new WiFiNetwork(
                result.SSID,
                protocol,
                securityLevel,
                result.level,
                result.BSSID
            );

            networks.add(network);
        }

        if (scanCallback != null) {
            scanCallback.onScanComplete(networks);
        }
    }

    public WiFiNetwork getCurrentConnection() {
        if (!hasPermissions() || wifiManager == null) {
            return null;
        }

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo == null || wifiInfo.getNetworkId() == -1) {
            return null;
        }

        String ssid = sanitizeSsid(wifiInfo.getSSID());
        if (ssid == null || ssid.isEmpty()) {
            return null;
        }

        String bssid = wifiInfo.getBSSID();
        int rssi = wifiInfo.getRssi();
        ScanResult matchedResult = findMatchingScanResult(ssid, bssid);

        String protocol = matchedResult != null
                ? getSecurityProtocol(matchedResult)
                : "UNKNOWN";

        if (rssi == INVALID_RSSI && matchedResult != null) {
            rssi = matchedResult.level;
        }

        String securityLevel = getSecurityLevel(protocol);

        return new WiFiNetwork(
                ssid,
                protocol,
                securityLevel,
                rssi,
                bssid
        );
    }

    private ScanResult findMatchingScanResult(String ssid, String bssid) {
        try {
            List<ScanResult> results = wifiManager.getScanResults();
            if (results == null) {
                return null;
            }

            for (ScanResult result : results) {
                boolean ssidMatch = result.SSID != null && result.SSID.equals(ssid);
                boolean bssidMatch = bssid != null && bssid.equals(result.BSSID);
                if (ssidMatch || bssidMatch) {
                    return result;
                }
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Scan results unavailable", e);
        }
        return null;
    }

    private String sanitizeSsid(String ssid) {
        if (ssid == null || ssid.equalsIgnoreCase("<unknown ssid>")) {
            return null;
        }
        if (ssid.startsWith("\"") && ssid.endsWith("\"") && ssid.length() >= 2) {
            return ssid.substring(1, ssid.length() - 1);
        }
        return ssid;
    }

    private String getSecurityProtocol(ScanResult result) {
        String capabilities = result.capabilities;

        if (capabilities == null || capabilities.isEmpty()) {
            return "OPEN";
        }

        String upperCapabilities = capabilities.toUpperCase();

        if (upperCapabilities.contains("WPA3")) {
            return "WPA3";
        }

        if (upperCapabilities.contains("WPA2")) {
            boolean isEnterprise = upperCapabilities.contains("EAP") || upperCapabilities.contains("ENTERPRISE");
            boolean isPersonal = upperCapabilities.contains("PSK") || upperCapabilities.contains("SAE");

            if (isEnterprise && !isPersonal) {
                return "WPA2-ENTERPRISE";
            }

            return "WPA2-PERSONAL";
        }

        if (upperCapabilities.contains("WPA")) {
            return "WPA";
        }

        if (upperCapabilities.contains("WEP")) {
            return "WEP";
        }

        return "OPEN";
    }

    private String getSecurityLevel(String protocol) {
        String normalized = protocol == null ? "" : protocol.toUpperCase();
        switch (normalized) {
            case "OPEN":
                return "critical";
            case "WEP":
                return "danger";
            case "WPA":
                return "warning";
            case "WPA2-PERSONAL":
            case "WPA2-ENTERPRISE":
            case "WPA3":
                return "safe";
            default:
                return "warning";
        }
    }

    private void unregisterReceiver() {
        if (wifiScanReceiver != null) {
            try {
                context.unregisterReceiver(wifiScanReceiver);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, "Receiver not registered", e);
            }
            wifiScanReceiver = null;
        }
    }

    public void cleanup() {
        unregisterReceiver();
    }
}
