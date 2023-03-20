package club.ryans.controllers;

import club.ryans.models.RawLog;
import club.ryans.models.managers.BuildingManager;
import club.ryans.models.managers.LogManager;
import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.ResourceManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.models.managers.AssetManager;
import club.ryans.parser.Log;
import club.ryans.parser.LogAnalyzer;
import club.ryans.parser.ParseResult;
import club.ryans.utility.Json;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class IndexController {
    @Autowired
    private BuildingManager buildingManager;

    @Autowired
    private OfficerManager officerManager;

    @Autowired
    private ResourceManager resourceManager;

    @Autowired
    private ShipManager shipManager;

    @Autowired
    private AssetManager assetManager;

    @Autowired
    private LogManager logManager;

    @Autowired
    private final LogAnalyzer analyzer;

    @GetMapping(value = {"/", "index.html"})
    public String index(final Model model) {
        model.addAttribute("logs", logManager.getLogCount());
        return "index";
    }

    @GetMapping("register")
    public String registerNewUser() {
        return "user/register";
    }

    @GetMapping("user")
    public String profile() {
        return "user/profile";
    }

    @GetMapping("/log/{tag}")
    public String log(final Model model, @PathVariable final String tag) {
        RawLog rawLog = logManager.getLogFromTag(tag);

        try {
            ObjectMapper mapper = Json.createObjectMapper();
            ParseResult parseResult = mapper.readValue(rawLog.getData(), ParseResult.class);
            Log log = analyzer.analyze(parseResult, rawLog.getFileName(), rawLog.getTag());

            model.addAttribute("raw_log", rawLog);
            model.addAttribute("log", log);
            return "log";
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong!");
        }
    }

    @GetMapping(value = "/buildings")
    public String buildings(final Model model) {
        model.addAttribute("buildings", buildingManager.getBuildings());
        model.addAttribute("assets", assetManager);
        return "buildings";
    }

    @GetMapping(value = "/officers")
    public String officers(final Model model) {
        model.addAttribute("officers", officerManager.getOfficers());
        model.addAttribute("assets", assetManager);
        return "officers";
    }

    @GetMapping(value = "/resources")
    public String resources(final Model model) {
        model.addAttribute("resources", resourceManager.getResources());
        model.addAttribute("assets", assetManager);
        return "resources";
    }

    @GetMapping(value = "/ships")
    public String ships(final Model model) {
        model.addAttribute("ships", shipManager.getShips());
        model.addAttribute("assets", assetManager);
        return "ships";
    }

    @GetMapping(value = "/assets")
    public String assets(final Model model) {
        model.addAttribute("assets", assetManager);
        return "assets";
    }
}