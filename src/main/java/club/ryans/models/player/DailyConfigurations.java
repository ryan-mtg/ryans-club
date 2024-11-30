package club.ryans.models.player;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DailyConfigurations {
    private final Map<String, DailyConfiguration> dailyMap = new HashMap<>();

    public DailyConfigurations(final Collection<DailyConfiguration> dailies) {
        dailies.forEach(dailyConfiguration -> dailyMap.put(dailyConfiguration.getDailyId(), dailyConfiguration));
    }

    public DailyConfiguration getDaily(final String dailyId) {
        return dailyMap.get(dailyId);
    }
}