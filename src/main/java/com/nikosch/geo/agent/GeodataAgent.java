package com.nikosch.geo.agent;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.nikosch.geo.client.GeoApiClients;
import com.nikosch.geo.record.*;

@Agent(description = "Gathers geological information about a geographic point and summarises it")
public class GeodataAgent {

    private final GeoApiClients geoApiClients;

    public GeodataAgent(GeoApiClients geoApiClients) {
        this.geoApiClients = geoApiClients;
    }

    @Action
    public Quake retrieveQuakeData(Location location) {
        return geoApiClients.getEarhquakeData(location);
    }

    @Action
    public Formation retrieveRockData(Location location) {
        //https://www.elevation-api.eu/v1/elevation/39.62711/21.31348?json
        //https://macrostrat.org/api/v2/geologic_units/map?lat=39.62711&lng=21.31348

        return geoApiClients.getRockData();
    }

    @Action
    public Fossil retrieveFosilData(Location location) {
        return geoApiClients.getFosilData();
    }

    @AchievesGoal(description = "Produce a report about the location")
    @Action
    public GeoReport summarize(Quake quake, Formation formation, Fossil fossil) {
        GeoReport report = new GeoReport("Quakes: " + quake.quakeData()
                + "\nRocks: " + formation.formationData()
                + "\nFossils: " + fossil.fossilData());
        System.out.println(report.summary());

        return report;
    }

}
