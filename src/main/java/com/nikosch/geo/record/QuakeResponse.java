package com.nikosch.geo.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuakeResponse(Metadata metadata, List<Feature> features) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Metadata(long generated, String url, String title, int count) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Feature(String id, Properties properties, Geometry geometry) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Properties(
            Double mag,          // boxed: can be null for some events
            String place,
            long time,           // epoch millis
            String url,
            Integer felt,        // nullable
            Double cdi,          // nullable
            Double mmi,          // nullable
            String alert,        // nullable ("green", "yellow", ...)
            int tsunami,
            int sig,
            String magType,
            String type,
            String title) {}


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Geometry(String type, List<Double> coordinates) {
        public double longitude() { return coordinates.get(0); }
        public double latitude()  { return coordinates.get(1); }
        public double depthKm()   { return coordinates.get(2); }
    }
}
