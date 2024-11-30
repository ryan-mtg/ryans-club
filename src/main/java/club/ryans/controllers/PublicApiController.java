package club.ryans.controllers;

import club.ryans.models.inventory.DataCollector;
import club.ryans.models.player.Update;
import club.ryans.interceptors.CommunityModEntrypoint;
import club.ryans.models.managers.UserManager;
import club.ryans.models.player.User;
import club.ryans.spocksclub.SpocksClubClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicApiController {
    public final static String SYNC_USER_ATTRIBUTE = "sync-user";
    private final static String TOKEN_HEADER = "stfc-sync-token";
    private final static SpocksClubClient SPOCKS_CLUB_CLIENT = SpocksClubClient.newClient();

    private final DataCollector collector;
    private final UserManager userManager;

    @PostMapping("/import")
    @CommunityModEntrypoint(token = TOKEN_HEADER)
    public boolean importData(final HttpServletRequest request, final HttpEntity<String> httpEntity) {
        LOGGER.info(">>> import---");
        User user = (User)request.getAttribute(SYNC_USER_ATTRIBUTE);
        LOGGER.info(">>> Sync incoming for {} on token {}", user.getDisplayName(), request.getHeader(TOKEN_HEADER));
        try {
            if (user.getAccount().shouldSyncToSpocksClub()) {
                LOGGER.info("Attempting to sync to Spock's club: {}", user.getAccount().getSpocksClubSyncToken());
                SPOCKS_CLUB_CLIENT.sync(user.getAccount().getSpocksClubSyncToken(), httpEntity.getBody());
            }
        } catch (Exception e) {
            LOGGER.info("Failed to sync to Spock's club: " + e.getMessage(), e);
            throw e;
           // just eat it
        }
        LOGGER.info(" >> Starting collect");
        Update update = collector.collect(user, httpEntity.getBody());
        LOGGER.info(" >> Done collecting, starting update");
        if (update.getState() == Update.State.COMPLETE) {
            userManager.updateUser(update);
        }
        LOGGER.info("<<< Done updating");
        return true;
    }
}