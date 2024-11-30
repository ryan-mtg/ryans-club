package club.ryans.models.dailies;

import lombok.Data;

import java.util.List;

@Data
public class DailyGroupsDescriptor {
    private List<DailyGroupDescriptor> groups;
}