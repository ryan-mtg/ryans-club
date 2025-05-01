package club.ryans.models.alliance;

import club.ryans.models.items.Resource;
import club.ryans.utility.Time;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Data
public class Territory {
    private String name;
    private DayOfWeek takeoverDay;
    private LocalTime takeoverTime;
    private List<Service> services;
    private List<System> systems;
    private String notes;

    public String takeover() {
        return takeoverTime.format(Time.TIME_FORMATTER) + ' ' + takeoverDay.getDisplayName(TextStyle.FULL, Locale.US);
    }

    public List<Resource> resources() {
        List<Resource> resources = new ArrayList<>();
        if (systems != null) {
            for (System system : systems) {
                resources.addAll(system.getResources());
            }
        }

        return resources;
    }
}
