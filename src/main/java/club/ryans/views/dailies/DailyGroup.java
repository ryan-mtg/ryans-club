package club.ryans.views.dailies;

import lombok.Data;

import java.util.List;

@Data
public class DailyGroup {
    private String name;
    private String id;
    private boolean main;
    private List<DailySubgroup> subgroups;
}
