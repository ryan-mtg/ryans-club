package club.ryans.data.serializers;

import club.ryans.data.entities.UserRow;
import club.ryans.data.repositories.UserRepository;
import club.ryans.security.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@AllArgsConstructor
public class UserSerializer {
    private UserRepository userRepository;

    public User lookupUserById(final int id) {
        return createUser(userRepository.findById(id));
    }

    public User lookupUserByDiscordId(final long discordId) {
        return createUser(userRepository.findByDiscordId(discordId));
    }

    public User save(final User user) {
        UserRow userRow = createUserRow(user);
        userRepository.save(userRow);
        user.setId(userRow.getId());
        return user;
    }

    private User createUser(final UserRow userRow) {
        if (userRow == null) {
            return null;
        }

        int server = userRow.getServer() == null ? 0 : userRow.getServer();
        Instant registrationTime = userRow.getRegistrationTime() == 0 ? null
                : Instant.ofEpochSecond(userRow.getRegistrationTime());
        return new User(userRow.getId(), userRow.getFlags(), userRow.getDiscordName(), userRow.getDiscordId(),
                userRow.getServer(), userRow.getGameName(), registrationTime);
    }

    private UserRow createUserRow(final User user) {
        UserRow userRow = new UserRow();
        userRow.setId(user.getId());
        userRow.setFlags(user.getFlags());
        userRow.setDiscordName(user.getDiscordName());
        userRow.setDiscordId(user.getDiscordId());
        userRow.setServer(user.getServer() == 0 ? null : user.getServer());
        userRow.setGameName(user.getGameName());
        userRow.setRegistrationTime(user.getRegistrationTime() == null ? 0
                : user.getRegistrationTime().getEpochSecond());
        return userRow;
    }
}
