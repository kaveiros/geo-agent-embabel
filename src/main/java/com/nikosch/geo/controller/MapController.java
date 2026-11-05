package com.nikosch.geo.controller;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.nikosch.geo.record.AiComponent;
import com.nikosch.geo.record.GeoReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nikosch.geo.record.Location;

@Controller
public class MapController {

    private static final Logger log = LoggerFactory.getLogger(MapController.class);

    private final AiComponent aiComponent;
    private final AgentPlatform agentPlatform;

    public MapController(AiComponent aiComponent, AgentPlatform agentPlatform) {
        this.aiComponent = aiComponent;
        this.agentPlatform = agentPlatform;
    }

    @GetMapping("/map")
    public String showMap(Model model) {
        // Optional: Pass initial coordinates from backend to Thymeleaf
        model.addAttribute("defaultLat", 37.9838);
        model.addAttribute("defaultLng", 23.7275);
        return "index";
    }

    @PostMapping("/api/location")
    @ResponseBody
    public GeoReport postLocation(@RequestBody Location location) {
        var lat = location.latitude();
        var lon = location.longitude();
        log.info("Received location: {}, {}", lat, lon);
        AiComponent.Point point = new AiComponent.Point(lat, lon);
        Location location1 = new Location(lat, lon);
        //String info = aiComponent.getGeoInfoForPoint(point);
        log.info("Invoking agents");

        return AgentInvocation.create(agentPlatform, GeoReport.class).invoke(location);
    }

}