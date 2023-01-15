package club.ryans.controllers;

import club.ryans.models.managers.OfficerManager;
import club.ryans.models.managers.ShipManager;
import club.ryans.models.managers.AssetManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class IndexController {
    @Autowired
    private OfficerManager officerManager;

    @Autowired
    private ShipManager shipManager;

    @Autowired
    private AssetManager assetManager;

    @GetMapping(value = {"/", "index.html"})
    public String index(final Model model) {
        model.addAttribute("recipient", "world!");
        return "index";
    }

    @GetMapping(value = "/officers")
    public String officers(final Model model) {
        model.addAttribute("officers", officerManager.getOfficers());
        model.addAttribute("assets", assetManager);
        return "officers";
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