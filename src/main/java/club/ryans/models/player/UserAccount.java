package club.ryans.models.player;

import club.ryans.utility.Flags;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAccount {
    private static final int DEFAULT_FLAGS = 0x0;
    private static final int SYNC_TO_SPOCKS_CLUB_FLAG = 0x10;

    private int id;
    private int flags;
    private int server;
    private String gameName;
    private String syncToken;
    private String spocksClubSyncToken;

    public static UserAccount create(final int server, final String gameName) {
        return new UserAccount(0, DEFAULT_FLAGS, server, gameName, null, null);
    }

    public void setSyncToSpocksClub(final boolean value) {
        this.flags = Flags.setFlag(this.flags, SYNC_TO_SPOCKS_CLUB_FLAG, value);
    }

    public boolean shouldSyncToSpocksClub() {
        return Flags.hasFlag(this.flags, SYNC_TO_SPOCKS_CLUB_FLAG);
    }
}