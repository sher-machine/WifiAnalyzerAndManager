package com.example.mylibrary;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.widget.ListView;

import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class myWifiManager {

    private static final String TAG ="MAINACTIVITY" ;
    private Element[] nets;
    private WifiManager wifiManager;
    private WifiConfiguration config;
    private ListView list;

    private static final int REQUEST_STATE = 1;
    private final int REQUEST_ENABLE_LOCATION_SYSTEM_SETTINGS = 101;
    private boolean isHotspot26apiPlusEnabled = false;
    private WifiManager.LocalOnlyHotspotReservation mReservation;
    WifiConfiguration currentConfig;

    public myWifiManager() {
    }

    public String infoAboutConnection() {
        return String.valueOf(wifiManager.getConnectionInfo());
    }

    public static String getMacAddr() {
        try {
            List <NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif: all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b: macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {}
        return "02:00:00:00:00:00";
    }


    public static void finallyConnect(String pwd, String ssid, WifiManager wifiManager) {
        //WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);;
        WifiConfiguration config;
        String mSSID = ssid;
        String mPWD = pwd;
        //wifiManager
        config = new WifiConfiguration();
        config.SSID = "\"" + mSSID + "\"";
        if(pwd.equals("")){
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        else{

            config.preSharedKey = "\"" + mPWD + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            ////
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.status = WifiConfiguration.Status.ENABLED;
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            ////
        }

        config.status = WifiConfiguration.Status.ENABLED;
        config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
        //wifiManager.startScan();
        int networkId = wifiManager.addNetwork(config);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();

    }

    public static WIFI_AP_STATE getWifiApStateMy(WifiManager wifiManager) {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApState");

            int tmp = ((Integer) method.invoke(wifiManager));

            // Fix for Android 4
            if (tmp > 10) {
                tmp = tmp - 10;
            }

            return WIFI_AP_STATE.class.getEnumConstants()[tmp];
        } catch (Exception e) {
            //Log.e(this.getClass().toString(), "", e);
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
    }

    public static void startHotSpot16_api(boolean enable, WifiManager wifiManager) {


        String SSID="Hello";
        String PASS="123321123321";
        if(enable){
            wifiManager.setWifiEnabled(!enable);    // Disable all existing WiFi Network
        }else {
            if (!wifiManager.isWifiEnabled())
                wifiManager.setWifiEnabled(!enable);
        }
        Method[] methods = wifiManager.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (method.getName().equals("setWifiApEnabled")) {
                WifiConfiguration netConfig = new WifiConfiguration();
                if(!SSID.isEmpty() || !PASS.isEmpty()){
                    netConfig.SSID=SSID;
                    netConfig.preSharedKey = PASS;
                    netConfig.hiddenSSID = false;
                    netConfig.status = WifiConfiguration.Status.ENABLED;              netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);              netConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);           netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                    netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);              netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);     netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                }
                try {
                    method.invoke(wifiManager, netConfig, enable);
                    Log.e(TAG,"set hotspot enable method");
                } catch (Exception ex) {}
                break;}}
    }

    public static void findMethods(WifiManager wm) {

        //WifiManager wm = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String str = "";
        Class<?> cls = wm.getClass();
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            str = str + method.getName()+"\n";
        }

        //hw.setText(str+ "q");
        Log.d(TAG,str);


        String str2="DECLARED ";
        Class<?> cls2 = wm.getClass();
        Method[] methods2 = cls2.getDeclaredMethods();
        for (Method method2 : methods2) {
            str2 = str2 +  method2.getName()+"\n";
        }

        Log.d(TAG,str2);

    }

    public static boolean isWifiApEnabled(WifiManager wifiManager) {
        return getWifiApStateMy(wifiManager) == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    public static String infoAboutWifiSupported (WifiManager wifiManager) {
        String info = "Mac-address:" + getMacAddr()
                +"\n Wi-Fi State - " + wifiManager.getWifiState();

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            info = info + "\nSupport 5GHz - " + wifiManager.is5GHzBandSupported()
                    + "\nSupport Wi-Fi Direct - " + wifiManager.isP2pSupported()
                    + "\nSupport Tdls - " + wifiManager.isTdlsSupported()
                    + "\nSupport always scan Wi-Fi  - " + wifiManager.isScanAlwaysAvailable()
                    + "\nDeviceToApRttSupported - " + wifiManager.isDeviceToApRttSupported();
        }
        return info;
    }


}
