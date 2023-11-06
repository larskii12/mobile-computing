package com.comp90018.uninooks.service.emulator;

import android.os.Build;

public class EmulatorServiceImpl implements EmulatorService {
    public static boolean isEmulator() {

        if (Build.FINGERPRINT.startsWith("google/sdk_gphone_")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                // Android Studio Emulator
                || Build.MANUFACTURER.contains("Google")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || Build.PRODUCT.equals("google_sdk")
                // BlueStacks
                || "BlueStacks".equalsIgnoreCase(Build.MANUFACTURER)) {

            System.out.println("This device is an emulator");

            return true;
        }

        System.out.println("This device is NOT an emulator");
        return false;
    }
}
