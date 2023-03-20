package club.ryans.security.user;

import club.ryans.data.serializers.UserSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
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

    public UserManager(final UserSerializer userSerializer, @Value("${admin.discord.ids}") final String adminDiscordIds) {
        this.userSerializer = userSerializer;
        this.adminDiscordIds = Arrays.stream(adminDiscordIds.split(",")).map(Long::parseLong)
                .collect(Collectors.toList());
    }

    public User lookupUser(final int id) {
        if (idMap.containsKey(id)) {
            return idMap.get(id);
        }

        return userSerializer.lookupUserById(id);
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

    public User registerUser(final int id, final int server, final String handle) {
        User user = lookupUser(id);
        user.setRegistered();
        user.setServer(server);
        user.setGameName(handle);
        user.setRegistrationTime(Instant.now());

        return userSerializer.save(user);
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
