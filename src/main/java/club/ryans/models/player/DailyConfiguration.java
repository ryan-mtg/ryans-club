package club.ryans.models.player;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyConfiguration {
    private String dailyId;
    private int flags;
    private int chests;
    private int goal;

    public static DailyConfiguration create(final String dailyId) {
        return create(dailyId, 0);
    }

    public static DailyConfiguration create(final String dailyId, final int chests) {
        return new DailyConfiguration(dailyId, 0, chests, 0);
    }
}
