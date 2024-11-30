package club.ryans.data.serializers;

import club.ryans.models.player.User;

import java.time.Instant;

public class Utility {
    public static int getUserId(final User user) {
        return user != null ? user.getId() : 0;
    }

    public static Instant getTime(final Long value) {
        if (value == null || value == 0) {
            return null;
        }
        return Instant.ofEpochSecond(value);
    }

    public static Long getPersistTime(final Instant instant) {
        if (instant == null) {
            return null;
        }
        return instant.getEpochSecond();
    }
}
