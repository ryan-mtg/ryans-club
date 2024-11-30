package club.ryans.charts.models;

import club.ryans.models.Research;
import club.ryans.models.player.PlayerItems;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ResearchChart extends Chart {
    private Research research;

    @Override
    protected int selectRow(final PlayerItems playerItems) {
        return playerItems.getResearchLevel(research.getId());
    }
}