package club.ryans.models.dailies;

import lombok.Data;

import java.util.List;

@Data
public class DailyGroupDescriptor {
    private String name;
    private String id;
    private boolean main;
    private List<DailySubgroupDescriptor> subgroups;
}