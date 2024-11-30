package club.ryans.models.dailies;

import club.ryans.models.ShipClass;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import lombok.Data;

import java.util.List;

@Data
public class DailySubgroupDescriptor {
    private String name;
    private String id;
    private ShipClass shipClass;
    private String chartId;
    private List<DailyDescriptor> dailies;
}