package club.ryans.models.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class Invite {
    public static enum State {
        FRESH,
        USED,
        EXPIRED;

        public boolean isAvailable() {
            return this == FRESH;
        }

        public String getDisplay() {
            switch (this) {
                case FRESH:
                    return "Available";
                case USED:
                    return "Used";
                case EXPIRED:
                    return "Expired";
            }
            throw new UnsupportedOperationException("Unknown state: " + this);
        }

        public String getStyle() {
            switch (this) {
                case FRESH:
                    return "invite-available";
                case USED:
                    return "invite-used";
                case EXPIRED:
                    return "invite-expired";
            }
            throw new UnsupportedOperationException("Unknown state: " + this);
        }
    }

    @Setter
    private int id;

    @Setter
    private State state;
    private String token;

    private User creator;
    private Instant creationTime;

    @Setter
    private User user;
    @Setter
    private Instant useTime;

    public String getStyle() {
        return state.getStyle();
    }

    public String getStateDisplay() {
        return state.getDisplay();
    }

    public static Invite newInvite(final String token, final User creator) {
        return new Invite(0, State.FRESH, token, creator, Instant.now(), null, null);
    }
}