package com.nikosch.geo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimestampParser {

    private static final Logger log = LoggerFactory.getLogger(TimestampParser.class);

    static void main(String[] args) {
        // Η χρονική σήμανση σε milliseconds (χρησιμοποιούμε long)
        
        TimeUtils timeUtils = new TimeUtils();
        String f = timeUtils.calculateCurrentTime(1791430972895L);
        log.info(f);
        timeUtils.getCurrentTime();

    }
}
