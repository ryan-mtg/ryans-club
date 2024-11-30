package club.ryans.charts.models;

import club.ryans.models.Resource;
import club.ryans.models.player.ResearchLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class ChestDescriptor {
    private int chests;
    private Resource resource;
    private RowValue value;
    private List<ResearchLevel> research;
}
