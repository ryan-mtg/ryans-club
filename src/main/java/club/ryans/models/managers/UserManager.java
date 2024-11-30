package club.ryans.models.managers;

import club.ryans.data.entities.Constraints;
import club.ryans.models.player.DailyConfiguration;
import club.ryans.models.player.DailyConfigurations;
import club.ryans.models.player.Invite;
import club.ryans.models.player.PlayerItems;
import club.ryans.models.player.PlayerItemsDelta;
import club.ryans.models.player.Update;
import club.ryans.data.serializers.UserSerializer;
import club.ryans.error.ServerError;
import club.ryans.models.player.User;
import club.ryans.models.player.UserAccount;
import club.ryans.security.user.UserInfo;
import club.ryans.utility.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserManager {
    private final UserSerializer userSerializer;
    private final List<Long> adminDiscordIds;

    private Map<Long, User> discordIdMap = new HashMap();
    private Map<Integer, User> idMap = new HashMap();

    private final TagManager syncTokenManager;
    private final TagManager inviteTokenManager;

    public UserManager(final UserSerializer userSerializer, @Value("${admin.discord.ids}") final String adminDiscordIds) {
        this.userSerializer = userSerializer;
        this.adminDiscordIds = Arrays.stream(adminDiscordIds.split(",")).map(Long::parseLong)
                .collect(Collectors.toList());

        syncTokenManager = new TagManager(userSerializer::hasSyncToken);
        inviteTokenManager = new TagManager(userSerializer::hasInviteToken);
    }

    public synchronized User lookupUser(final int id) {
        if (idMap.containsKey(id)) {
            return idMap.get(id);
        }

        return userSerializer.lookupUserById(id);
    }

    public synchronized Collection<User> getUsers() {
        for (User user : userSerializer.lookupAllUsers()) {
            if (!idMap.containsKey(user.getId())) {
                addUser(user);
            }
        }
        return idMap.values();
    }

    public synchronized Collection<Invite> getInvites() {
        return userSerializer.getInvites();
    }

    public User lookupUserBySyncToken(final String syncToken) {
        Integer userId = userSerializer.lookupUserIdBySyncToken(syncToken);
        if (userId == null) {
            return null;
        }

        return lookupUser(userId);
    }

    public User lookupUserByDiscordInfo(final UserInfo userInfo) {
        if (discordIdMap.containsKey(userInfo.getId())) {
            return discordIdMap.get(userInfo.getId());
        }

        User user = userSerializer.lookupUserByDiscordId(userInfo.getId());
        if (user != null) {
            return addUser(user);
        }

        return addUser(createUser(userInfo));
    }

    public User createUnauthenticatedUser() {
        return User.createUnauthenticatedUser();
    }

    public User registerUser(final User user, final String inviteToken, final int server, final String handle)
            throws ServerError {
        if (user == null || user.isRegistered()) {
            throw new ServerError(HttpStatus.BAD_REQUEST, "Invalid user.");
        }

        Invite invite = userSerializer.getInviteByToken(inviteToken.trim());

        if (invite == null || !invite.getState().isAvailable()) {
            throw new ServerError(HttpStatus.BAD_REQUEST, "Invite is invalid.");
        }

        UserAccount account = UserAccount.create(server, handle);

        Instant now = Instant.now();

        user.setRegistered();
        user.setRegistrationTime(now);
        user.setAccount(account);

        invite.setState(Invite.State.USED);
        invite.setUser(user);
        invite.setUseTime(now);

        return userSerializer.save(user, invite);
    }

    public Invite createInvite(final User creator) {
        final String inviteToken = inviteTokenManager.createTag();
        Invite invite = Invite.newInvite(inviteToken, creator);
        return userSerializer.save(invite);
    }

    public PlayerItems getItems(final User user) {
        final UserAccount account = user.getAccount();
        if (account == null) {
            return new PlayerItems();
        }

        return userSerializer.loadAccountItems(account);
    }

    public void updateUser(final Update update) {
        UserAccount account = update.getUser().getAccount();;

        PlayerItems items = userSerializer.loadAccountItems(account);
        PlayerItemsDelta delta = items.update(update);
        userSerializer.save(account, delta);
    }

    public DailyConfigurations getDailies(final User user) throws ServerError {
        UserAccount account = user.getAccount();
        if (account == null) {
            throw new ServerError(HttpStatus.BAD_REQUEST, "No Account associated with user.");
        }

        return userSerializer.loadDailyConfigurations(account.getId());
    }

    public DailyConfiguration updateUserDaily(final User user, final String dailyId, final Integer chests)
            throws ServerError {

        UserAccount account = user.getAccount();
        if (account == null) {
            throw new ServerError(HttpStatus.BAD_REQUEST, "No Account associated with user.");
        }

        DailyConfiguration dailyConfiguration = userSerializer.loadDailyConfiguration(account.getId(), dailyId);
        if (dailyConfiguration == null) {
            dailyConfiguration = DailyConfiguration.create(dailyId, chests);
        } else {
            dailyConfiguration.setChests(chests);
        }

        userSerializer.save(account, dailyConfiguration);
        return dailyConfiguration;
    }

    public void updateSpocksClubSyncToken(final UserAccount account, final String token) throws ServerError {
        if (Strings.isBlank(token)) {
            account.setSyncToSpocksClub(false);
            account.setSpocksClubSyncToken(null);
        } else {
            if (token.length() > Constraints.EXTERNAL_TOKEN_SIZE) {
                throw new ServerError(HttpStatus.BAD_REQUEST, "Token size too long");
            }
            account.setSyncToSpocksClub(true);
            account.setSpocksClubSyncToken(token);
        }
        userSerializer.save(account);
    }

    public String createSyncToken(final User user) throws ServerError {
        if (user == null || !user.isRegistered()) {
            throw new ServerError(HttpStatus.BAD_REQUEST, "Invalid user");
        }

        UserAccount account = user.getAccount();

        if (account.getSyncToken() == null) {
            account.setSyncToken(syncTokenManager.createTag());
            userSerializer.save(account);
        }

        return account.getSyncToken();
    }

    private User createUser(final UserInfo userInfo) {
        User user;
        if (adminDiscordIds.contains(userInfo.getId())) {
            user = User.createUser(User.UNAUTHENTICATED_ID, User.ADMIN_FLAG, userInfo.getName(), userInfo.getId());
        } else {
            user = User.createUser(User.UNAUTHENTICATED_ID, User.DEFAULT_FLAGS, userInfo.getName(), userInfo.getId());
        }
        return userSerializer.save(user);
    }

    private User addUser(final User user) {
        idMap.put(user.getId(), user);
        discordIdMap.put(user.getDiscordId(), user);
        return user;
    }
}
