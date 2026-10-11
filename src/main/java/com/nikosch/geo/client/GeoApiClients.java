package com.nikosch.geo.client;

import com.nikosch.geo.record.Formation;
import com.nikosch.geo.record.Fossil;
import com.nikosch.geo.record.Quake;
import org.springframework.stereotype.Component;

@Component
public class GeoApiClients {

    public Quake getEarhquakeData() {
        return  new Quake("Got earthquake data");
    }

    public Formation getRockData() {
        return new Formation("Got rock data");
    }

    public Fossil getFosilData() {
        return new Fossil("Got fossil data");
    }
}
