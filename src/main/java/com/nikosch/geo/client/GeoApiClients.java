package com.nikosch.geo.client;

import com.nikosch.geo.record.*;
import com.nikosch.geo.util.TimeUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;


@Component
public class GeoApiClients {

    private final TimeUtils timeUtils = new TimeUtils();
    public Quake getEarhquakeData(Location location) {

        String baseQuakeUri ="https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson" +
                "&latitude=" + location.latitude() + "&longitude=" + location.longitude() +
                "&maxradiuskm=100&starttime=" + timeUtils.getCurrentTime();
                //+ "&minmagnitude=2.5";

        RestClient restClient = RestClient.create();
        QuakeResponse quakes = restClient.get()
                .uri(baseQuakeUri)
                .retrieve()
                .body(QuakeResponse.class);

        if (quakes == null || quakes.features() == null || quakes.features().isEmpty()) {
            return new Quake("No earthquakes found.");
        }

        StringBuilder quakesBuilder = new StringBuilder();
        for (var feature : quakes.features()) {
            quakesBuilder.append("\ntitle: ").append(feature.properties().title())
                    .append("\nmagnitude: ").append(feature.properties().mag())
                    .append("\ndepth: ").append(feature.geometry().depthKm()).append(" km")
                    .append("\nlatitude: ").append(feature.geometry().latitude())
                    .append("\nlongitude: ").append(feature.geometry().longitude())
                    .append("\n");
        }
        return new Quake("Got earthquake data:" + quakesBuilder);
    }

    public Formation getRockData() {
        return new Formation("Got rock data");
    }

    public Fossil getFosilData() {
        return new Fossil("Got fossil data");
    }
}
