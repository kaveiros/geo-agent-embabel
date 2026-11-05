package com.nikosch.geo.util;

public class TimestampParser {
    static void main(String[] args) {
        // Η χρονική σήμανση σε milliseconds (χρησιμοποιούμε long)
        
        TimeUtils timeUtils = new TimeUtils();
        String f = timeUtils.calculateCurrentTime(1791430972895L);
        System.out.println(f);
        timeUtils.getCurrentTime();

    }
}
