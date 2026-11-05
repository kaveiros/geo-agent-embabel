package com.nikosch.geo.client;

import com.nikosch.geo.record.*;
import com.nikosch.geo.util.TimeUtils;
import tools.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


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

    public Formation getRockData(Location location) {
        String uri = "https://macrostrat.org/api/v2/geologic_units/map?response=long"
                + "&lat=" + location.latitude() + "&lng=" + location.longitude();

        JsonNode root = RestClient.create().get().uri(uri).retrieve().body(JsonNode.class);
        JsonNode data = root == null ? null : root.path("success").path("data");
        if (data == null || !data.isArray() || data.isEmpty()) {
            return new Formation("No rock formations found.");
        }

        // Macrostrat returns the same unit from several map scales, so skip repeated names
        Set<String> seen = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (JsonNode unit : data) {
            String name = text(unit, "name");
            if (!seen.add(name)) {
                continue;
            }
            String description = text(unit, "descrip");
            if (description.isEmpty()) {
                description = text(unit, "comments");
            }
            sb.append('\n').append(name.isEmpty() ? "Unnamed unit" : name);
            appendLine(sb, "formation", text(unit, "strat_name"));
            appendLine(sb, "lithology", text(unit, "lith"));
            appendLine(sb, "age", ageRange(text(unit, "b_int_name"), text(unit, "t_int_name"),
                    text(unit, "b_int_age"), text(unit, "t_int_age")));
            appendLine(sb, "description", description);
            sb.append('\n');
        }
        return new Formation(sb.toString());
    }

    public Fossil getFosilData(Location location) {
        // PBDB has no point+radius filter, so query a small bounding box around the point
        double d = 0.5;
        String uri = "https://paleobiodb.org/data1.2/occs/list.json?vocab=pbdb&show=strat,class&limit=20"
                + "&latmin=" + (location.latitude() - d) + "&latmax=" + (location.latitude() + d)
                + "&lngmin=" + (location.longitude() - d) + "&lngmax=" + (location.longitude() + d);

        JsonNode root = RestClient.create().get().uri(uri).retrieve().body(JsonNode.class);
        JsonNode records = root == null ? null : root.path("records");
        if (records == null || !records.isArray() || records.isEmpty()) {
            return new Fossil("No fossils found.");
        }

        // A collection often lists the same taxon more than once, so skip repeats
        Set<String> seen = new HashSet<>();
        StringBuilder sb = new StringBuilder();
        for (JsonNode r : records) {
            String taxon = text(r, "accepted_name");
            if (taxon.isEmpty()) {
                taxon = text(r, "identified_name");
            }
            String age = ageRange(text(r, "early_interval"), text(r, "late_interval"),
                    text(r, "max_ma"), text(r, "min_ma"));
            if (!seen.add(taxon + age)) {
                continue;
            }
            String group = Stream.of(text(r, "phylum"), text(r, "class"), text(r, "order"), text(r, "family"))
                    .filter(s -> !s.isEmpty() && !s.startsWith("NO_"))
                    .collect(Collectors.joining(" › "));
            sb.append('\n').append(taxon.isEmpty() ? "Unknown taxon" : taxon);
            appendLine(sb, "group", group);
            appendLine(sb, "age", age);
            appendLine(sb, "formation", text(r, "formation"));
            sb.append('\n');
        }
        return new Fossil(sb.toString());
    }

    // Trimmed text of a field, or "" when it is missing, null or blank
    private static String text(JsonNode node, String field) {
        JsonNode value = node.path(field);
        return value.isMissingNode() || value.isNull() ? "" : value.asText().trim();
    }

    // Appends "label: value" only when there is a value to show
    private static void appendLine(StringBuilder sb, String label, String value) {
        if (!value.isEmpty()) {
            sb.append('\n').append(label).append(": ").append(value);
        }
    }

    // "Miocene (23.04 - 5.333 Ma)" or "Early Miocene - Late Miocene (...)"
    private static String ageRange(String oldInterval, String youngInterval, String oldMa, String youngMa) {
        String interval = youngInterval.isEmpty() || youngInterval.equals(oldInterval)
                ? oldInterval : oldInterval + " - " + youngInterval;
        String ma = oldMa.isEmpty() || youngMa.isEmpty() ? "" : " (" + oldMa + " - " + youngMa + " Ma)";
        return (interval + ma).trim();
    }
}
