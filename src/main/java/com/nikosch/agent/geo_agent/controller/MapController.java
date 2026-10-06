package com.nikosch.agent.geo_agent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.nikosch.map.Location;

@Controller
public class MapController {

    @GetMapping("/map")
    public String showMap(Model model) {
        // Optional: Pass initial coordinates from backend to Thymeleaf
        model.addAttribute("defaultLat", 37.9838);
        model.addAttribute("defaultLng", 23.7275);
        return "map";
    }

    @PostMapping("/api/location")
    public String postLocation(@RequestBody Location location) {
        location.latitude();
        location.longitude();
        System.out.println("Received location: " + location.latitude() + ", " + location.longitude());
        return "map";
    }

}