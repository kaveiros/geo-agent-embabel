package com.nikosch.geo.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.embabel.agent.api.common.OperationContext;
import com.nikosch.geo.client.GeoApiClients;
import com.nikosch.geo.record.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Agent(description = "Gathers geological information about a geographic point and summarises it")
public class GeodataAgent {

    private static final Logger log = LoggerFactory.getLogger(GeodataAgent.class);

    private final GeoApiClients geoApiClients;

    public GeodataAgent(GeoApiClients geoApiClients) {
        this.geoApiClients = geoApiClients;
    }

    @Action
    public Quake retrieveQuakeData(Location location) {
        log.info("Retrieving quake data for {}", location);
        var result = geoApiClients.getEarhquakeData(location);
        log.info("Retrieved quake data ({} chars)", String.valueOf(result).length());
        return result;
    }

    @Action
    public Formation retrieveRockData(Location location) {

        log.info("Retrieving rock data for {}", location);
        var result = geoApiClients.getRockData(location);
        log.info("Retrieved rock data ({} chars)", String.valueOf(result).length());
        return result;
    }

    @Action
    public Fossil retrieveFosilData(Location location) {
        log.info("Retrieving fossil data for {}", location);
        var result = geoApiClients.getFosilData(location);
        log.info("Retrieved fossil data ({} chars)", String.valueOf(result).length());
        return result;
    }

    @AchievesGoal(description = "Produce a report about the location")
    @Action
    public GeoReport summarize(Location location, Quake quake, Formation formation, Fossil fossil,
                               OperationContext context) {
        log.info("Summarizing data for {}", location);
        String data = "Quakes: " + quake.quakeData()
                + "\nRocks: " + formation.formationData()
                + "\nFossils: " + fossil.fossilData();

        String prompt = loadSystemPrompt()
                + "\n\n---\n\n# INPUT DATA\nCoordinates: " + location.latitude() + ", " + location.longitude()
                + "\n\n" + data;

        String analysis;
        try {
            analysis = context.ai().withDefaultLlm().generateText(prompt);
        } catch (RuntimeException e) {
            // Keep the raw data usable even if the model call fails
            analysis = "Geological analysis unavailable: " + e.getMessage();
        }
        log.info("Summary complete ({} chars of analysis)", analysis.length());
        return new GeoReport(data, analysis);
    }

    private static String loadSystemPrompt() {
        try (var in = new ClassPathResource("prompts/field-geologist.md").getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
