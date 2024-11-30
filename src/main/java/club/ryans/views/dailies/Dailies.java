package club.ryans.views.dailies;

import lombok.Data;

import java.util.List;

@Data
public class Dailies {
    private String defaultGroupId;
    private List<DailyGroup> groups;
}
