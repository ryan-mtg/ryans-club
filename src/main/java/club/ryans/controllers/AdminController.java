package club.ryans.controllers;

import club.ryans.models.inventory.DataCollector;
import club.ryans.models.managers.UserManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final DataCollector collector;
    private final UserManager userManager;

    @GetMapping("")
    public String adminPage(final Model model) {
        return "admin/index";
    }

    @GetMapping("/collector")
    public String collector(final Model model) {
        model.addAttribute("updates", collector.getUpdates());
        return "admin/collector";
    }

    @GetMapping("/users")
    public String users(final Model model) {
        model.addAttribute("users", userManager.getUsers());
        model.addAttribute("invites", userManager.getInvites());
        return "admin/users";
    }
}
