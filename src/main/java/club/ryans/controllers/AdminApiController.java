package club.ryans.controllers;

import club.ryans.models.managers.UserManager;
import club.ryans.models.player.Invite;
import club.ryans.models.player.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminApiController {
    private final UserManager userManager;

    @PostMapping("/create_invite")
    public Invite createInvite(final User user) {
        return userManager.createInvite(user);
        // return new Invite(123, Invite.State.FRESH, "pretend-invite-token", user, Instant.now(), null, null);
    }
}