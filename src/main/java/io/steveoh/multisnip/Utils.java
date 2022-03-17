package io.steveoh.multisnip;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class Utils {
    public enum OSType {
        Windows, MacOS, Linux, Other
    }

    protected static OSType detectedOS;

    public static OSType getOperatingSystemType() {
        if (detectedOS == null) {
            String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
            if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                detectedOS = OSType.MacOS;
            } else if (OS.contains("win")) {
                detectedOS = OSType.Windows;
            } else if (OS.contains("nux")) {
                detectedOS = OSType.Linux;
            } else {
                detectedOS = OSType.Other;
            }
        }
        return detectedOS;
    }

    public static String getFileSpearator() {
        if (getOperatingSystemType() == OSType.Windows) return "\\"; else return "/";
    }

    public static String getWorkingDirectory() {
        if (getOperatingSystemType() == OSType.Windows)
            return System.getenv("AppData") + Multisnip.fileSeparator + "MultisnipOCR";
        else if (getOperatingSystemType() == OSType.Linux)
            return System.getProperty("user.home") + Multisnip.fileSeparator + "MultisnipOCR";
        else if (getOperatingSystemType() == OSType.MacOS)
            return System.getProperty("user.home/Library/Application Support") + Multisnip.fileSeparator + "MultisnipOCR";
        return null;
    }
}
