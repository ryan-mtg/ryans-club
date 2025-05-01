package club.ryans.charts.models;

import club.ryans.models.items.Resource;
import club.ryans.models.player.PlayerItems;
import club.ryans.models.player.ResearchLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Column {
    private String title;
    private String color;
    private String background;
    private Resource resource;
    private List<ResearchLevel> research;
    private Float chance;
    private Integer rolls;
    private String cellType;
    private List<Float> probabilities;
    private RowValue value;

    public boolean matches(final PlayerItems playerItems) {
        return ResearchLevel.matches(research, playerItems);
    }
}