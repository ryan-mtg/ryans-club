package club.ryans.controllers;

import club.ryans.charts.ex.borg.ExBorgEfficiencyCalculator;
import club.ryans.error.ServerError;
import club.ryans.models.Building;
import club.ryans.models.RawLog;
import club.ryans.models.Research;
import club.ryans.models.calculators.BuildingCalculator;
import club.ryans.models.calculators.DailiesCalculator;
import club.ryans.models.managers.AllianceManager;
import club.ryans.models.managers.BuildingManager;
import club.ryans.models.managers.LogManager;
import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.AssetManager;
import club.ryans.models.managers.ResearchManager;
import club.ryans.models.managers.UserManager;
import club.ryans.models.player.BuildingStats;
import club.ryans.models.player.DailyConfigurations;
import club.ryans.models.player.PlayerItems;
import club.ryans.models.player.User;
import club.ryans.parser.Log;
import club.ryans.parser.LogAnalyzer;
import club.ryans.parser.ParseResult;
import club.ryans.utility.Json;
import club.ryans.views.PlayerItemsView;
import club.ryans.views.inventory.InventoryGrouper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
public class IndexController {
    @Autowired
    private AllianceManager allianceManager;

    @Autowired
    private OfficerManager officerManager;

    @Autowired
    private BuildingManager buildingManager;

    @Autowired
    private ResearchManager researchManager;

    @Autowired
    private AssetManager assetManager;

    @Autowired
    private ExBorgEfficiencyCalculator exBorgEfficiencyCalculator;

    @Autowired
    private BuildingCalculator buildingCalculator;

    @Autowired
    private DailiesCalculator dailiesCalculator;

    @Autowired
    private UserManager userManager;

    @Autowired
    private LogManager logManager;

    @Autowired
    private final LogAnalyzer analyzer;

    @Autowired
    private final InventoryGrouper inventoryGrouper;

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
    public String profile(final Model model) {
        final User user = (User) model.getAttribute("user");
        final PlayerItems items = userManager.getItems(user);
        PlayerItemsView itemsView = items != null ? new PlayerItemsView(items, inventoryGrouper) : null;
        model.addAttribute("items", itemsView);
        return "user/profile";
    }

    @GetMapping(value = "/user/dailies")
    public String dailies(final Model model) throws ServerError {
        final User user = (User) model.getAttribute("user");
        final PlayerItems items = userManager.getItems(user);
        final DailyConfigurations dailyConfigurations = userManager.getDailies(user);

        model.addAttribute("dailies", dailiesCalculator.getDailies(items, dailyConfigurations));
        model.addAttribute("officers", officerManager.getOfficers());
        model.addAttribute("assets", assetManager);
        return "user/dailies";
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

    @GetMapping(value = "/alliance")
    public String alliance(final Model model) {
        model.addAttribute("alliance", allianceManager.createPrototype());
        model.addAttribute("assets", assetManager);
        return "alliance";
    }

    @GetMapping(value = "/crews")
    public String crews(final Model model) {
        model.addAttribute("officers", officerManager.getOfficers());
        model.addAttribute("assets", assetManager);
        return "crews";
    }

    @GetMapping(value = "/officer/{index}")
    public String officer(final Model model, @PathVariable final int index) {
        model.addAttribute("officer", officerManager.getOfficer(index));
        model.addAttribute("assets", assetManager);
        return "items/officer";
    }

    @GetMapping(value = "/building/{index}")
    public String building(final Model model, @PathVariable final int index) {
        final User user = (User) model.getAttribute("user");
        final PlayerItems items = userManager.getItems(user);

        Building building = buildingManager.getBuilding(index);

        if (building == null) {
            return "not_found";
        }

        BuildingStats stats = buildingCalculator.computePlayerStats(building, items);

        model.addAttribute("building", building);
        model.addAttribute("stats", stats);
        model.addAttribute("items", items);
        model.addAttribute("assets", assetManager);
        return "items/building";
    }

    @GetMapping(value = "/research/{index}")
    public String research(final Model model, @PathVariable final long index) {
        final User user = (User) model.getAttribute("user");
        final PlayerItems items = userManager.getItems(user);

        Research research = researchManager.getResearch(index);

        if (research == null) {
            return "not_found";
        }

        //BuildingStats stats = buildingCalculator.computePlayerStats(building, items);

        model.addAttribute("research", research);
        //model.addAttribute("stats", stats);
        model.addAttribute("items", items);
        model.addAttribute("assets", assetManager);
        return "items/research";
    }

    @GetMapping(value = "/ex-borg")
    public String exBorg(final Model model) {
        model.addAttribute("bundles", exBorgEfficiencyCalculator.getBundles());
        model.addAttribute("ranks", exBorgEfficiencyCalculator.getRanks());
        model.addAttribute("officers", officerManager.getOfficers());
        model.addAttribute("assets", assetManager);
        return "grind/ex-borg";
    }
}