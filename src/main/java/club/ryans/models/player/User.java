package club.ryans.models.player;

import club.ryans.utility.Flags;
import club.ryans.utility.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class User {
    public static final String ID_PROPERTY = "user.id";

    public static final int DEFAULT_FLAGS = 0;
    public static final int SUPER_ADMIN_FLAG = 1;
    public static final int ADMIN_FLAG = 2;
    public static final int REGISTERED_FLAG = 4;

    public static final int UNAUTHENTICATED_ID = 0;

    @Setter
    private int id;

    private int flags;

    private String discordName;

    private long discordId;

    @Setter
    private Instant registrationTime;

    @Setter
    private UserAccount account;

    public static User createUnauthenticatedUser() {
        return createUser(User.UNAUTHENTICATED_ID, User.DEFAULT_FLAGS, null, 0);
    }

    public static User createUser(final int id, final int flags, final String discordName, final long discordId) {
        return new User(id, flags, discordName, discordId, null, null);
    }

    public boolean isAdmin() {
        return Flags.hasFlag(flags, ADMIN_FLAG);
    }

    public boolean isRegistered() {
        return Flags.hasFlag(flags, REGISTERED_FLAG) && account != null;
    }

    public boolean isAuthenticated() {
        return id != UNAUTHENTICATED_ID;
    }

    public String getDisplayName() {
        if (account !=null && !Strings.isBlank(account.getGameName())) {
            return account.getGameName();
        }

        if (discordName.indexOf("#") > 0) {
            return discordName.substring(0, discordName.indexOf('#'));
        }

        return discordName;
    }

    public void setRegistered() {
        this.flags = Flags.setFlag(flags, REGISTERED_FLAG, true);
    }
}