package fr.noctu.moonnet.common.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoField;

public class LogUtils {
    private static void sendTimedMessage(String message){
        LocalDateTime now = LocalDateTime.now();
        System.out.println("(" + now.getHour() + ":" + now.getMinute() + ":" + now.getSecond() + ":" + now.get(ChronoField.MILLI_OF_SECOND) + ") " + message);
    }

    public static void logSuccess(String message){
        sendTimedMessage("[+] " + message);
    }

    public static void logInfo(String message){
        sendTimedMessage("[*] " + message);
    }

    public static void logError(String message){
        sendTimedMessage("[-] " + message);
    }
}
