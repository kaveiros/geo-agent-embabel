package com.nikosch.geo.record;

import com.embabel.agent.api.common.Ai;
import org.springframework.stereotype.Component;

@Component
public record AiComponent(Ai ai) {


    public record Point(double lat, double lon) {}

    public String getGeoInfoForPoint(Point point) {
        return ai.withDefaultLlm().generateText("Find info about the location with lat " + point.lat() + " and lon "
                + point.lon() + ". Always focus on the nearest city with largest population.");
    }
}
