package com.nikosch.agent.geo.controller;

import com.nikosch.agent.geo.record.AiComponent;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nikosch.map.Location;

@Controller
public class MapController {

    private final AiComponent aiComponent;

    public MapController(AiComponent aiComponent) {
        this.aiComponent = aiComponent;
    }

    @GetMapping("/map")
    public String showMap(Model model) {
        // Optional: Pass initial coordinates from backend to Thymeleaf
        model.addAttribute("defaultLat", 37.9838);
        model.addAttribute("defaultLng", 23.7275);
        return "map";
    }

    @PostMapping("/api/location")
    public String postLocation(@RequestBody Location location) {
        var lat = location.latitude();
        var lon = location.longitude();
        System.out.println("Received location: " + location.latitude() + ", " + location.longitude());
        AiComponent.Point point = new AiComponent.Point(lat, lon);
        String info = aiComponent.getGeoInfoForPoint(point);
        System.out.println(info);

        return "map";
    }

}