package club.ryans.controllers;

import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.BuildingManager;
import club.ryans.models.managers.MissionManager;
import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.ResearchManager;
import club.ryans.models.managers.ResourceManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.models.managers.SystemManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/listings")
@RequiredArgsConstructor
public class ListingsController {
    private final AssetManager assetManager;
    private final BuildingManager buildingManager;
    private final OfficerManager officerManager;
    private final ResearchManager researchManager;
    private final ResourceManager resourceManager;
    private final ShipManager shipManager;
    private final MissionManager missionManager;
    private final SystemManager systemManager;

    @GetMapping(value = "/buildings")
    public String buildings(final Model model) {
        model.addAttribute("buildings", buildingManager.getBuildings());
        model.addAttribute("assets", assetManager);
        return "listings/buildings";
    }

    @GetMapping(value = "/assets")
    public String assets(final Model model) {
        model.addAttribute("assets", assetManager);
        return "assets";
    }

    @GetMapping(value = "/officers")
    public String officers(final Model model) {
        model.addAttribute("officers", officerManager.getOfficers());
        model.addAttribute("assets", assetManager);
        return "listings/officers";
    }

    @GetMapping(value = "/research")
    public String research(final Model model) {
        model.addAttribute("researches", researchManager.getResearch());
        model.addAttribute("assets", assetManager);
        return "listings/research";
    }

    @GetMapping(value = "/resources")
    public String resources(final Model model) {
        model.addAttribute("resources", resourceManager.getResources());
        model.addAttribute("assets", assetManager);
        return "listings/resources";
    }

    @GetMapping(value = "/ships")
    public String ships(final Model model) {
        model.addAttribute("ships", shipManager.getShips());
        model.addAttribute("assets", assetManager);
        return "listings/ships";
    }

    @GetMapping(value = "/missions")
    public String missions(final Model model) {
        model.addAttribute("missions", missionManager.getMissions());
        model.addAttribute("assets", assetManager);
        return "listings/missions";
    }

    @GetMapping(value = "/systems")
    public String systems(final Model model) {
        model.addAttribute("systems", systemManager.getSystems());
        model.addAttribute("assets", assetManager);
        return "listings/systems";
    }
}