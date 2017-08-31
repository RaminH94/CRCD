/***
*These codes and the corresponding Android application for D2D communications that are available at (https://github.com/CRCDAndroidApplicationRealtimeContent/CRCD) have been developed by the GTMD research Lab at Isfahan University of Technology and the NetSciWiS research Lab at Virginia Tech (ECE Department), under the supervision of Dr. Mohammad Hossein Manshaei and Dr. Walid Saad, respectively. 
*For further information, feel free to contact us at: manshaei@cc.iut.ac.ir or walids@vt.edu.
***/
package com.gtmd.crcd.begin;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;


public class ApManager {

//check whether wifi hotspot on or off
public static boolean isApOn(Context context) {
    WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);     
    try {
        Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
        method.setAccessible(true);
        return (Boolean) method.invoke(wifimanager);
    }
    catch (Throwable ignored) {}
    return false;
}

// toggle wifi hotspot on or off
public static boolean turnOffAP(Context context) {
    WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
    WifiConfiguration wificonfiguration = null;
    try {  
        // if WiFi is on, turn it off
        if(isApOn(context)) {               
            wifimanager.setWifiEnabled(false);
        }               
        Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);                   
        method.invoke(wifimanager, wificonfiguration, false);
        return true;
    } 
    catch (Exception e) {
        e.printStackTrace();
    }       
    return false;
}
} // end of class