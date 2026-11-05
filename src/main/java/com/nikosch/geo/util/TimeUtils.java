package com.nikosch.geo.util;


import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public String calculateCurrentTime(long timestamp) {

        Instant instant = Instant.ofEpochMilli(timestamp);
        ZonedDateTime utcDateTime = instant.atZone(ZoneId.of("UTC"));
        ZonedDateTime localDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return  "UTC Time:   " + utcDateTime.minusDays(10).format(formatter) +
        "\nLocal Time: " + localDateTime.minusDays(10).format(formatter);
    }


    public String getCurrentTime() {
        Instant instant = Instant.now();
        ZonedDateTime localDateTime = instant.atZone(ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDateTime.minusDays(10).format(formatter);
    }

}



