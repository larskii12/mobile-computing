package com.comp90018.uninooks.service.emulator;

import android.os.Build;

public class EmulatorServiceImpl implements EmulatorService {

    /**
     * Returns a boolean if it is running an emulator.
     */
    public static boolean isEmulator() {

        if ((Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("sdk_gphone64_arm64")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")) {

            System.out.println("This device is an emulator");

            return true;
        }

        System.out.println("This device is NOT an emulator");
        return false;
    }
}
