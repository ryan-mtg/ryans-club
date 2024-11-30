package club.ryans.views.dailies;

import club.ryans.models.ShipClass;
import club.ryans.models.dailies.tasks.DailyTask;
import lombok.Data;

import java.util.List;

@Data
public class DailySubgroup {
    private String name;
    private String id;
    private boolean main;
    private ShipClass shipClass;
    private String chartId;
    private List<DailyTask> dailies;
}
