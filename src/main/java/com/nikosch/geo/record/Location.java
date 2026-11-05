package com.nikosch.geo.record;

public record Location(double latitude, double longitude) {

    // Leaflet reports longitudes beyond +-180 when the map is panned across
    // repeated world copies; external APIs reject those, so wrap into [-180, 180).
    public Location {
        longitude = ((longitude + 180) % 360 + 360) % 360 - 180;
    }
}
