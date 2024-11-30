package club.ryans.models.dailies.tasks.selectors;

import club.ryans.charts.models.Chart;
import club.ryans.charts.models.ResearchChart;
import club.ryans.models.Research;
import club.ryans.models.dailies.descriptors.DailyDescriptor;
import club.ryans.models.dailies.descriptors.ResearchDailyDescriptor;
import club.ryans.models.player.PlayerItems;
import lombok.Data;

@Data
public class ResearchSelector extends Selector {
    private Research research;
    private int level;

    @Override
    public boolean isResearchType() {
        return true;
    }

    @Override
    public void update(final DailyDescriptor dailyDescriptor, final Chart chart, final PlayerItems playerItems) {
        ResearchChart researchChart = (ResearchChart) chart;

        research = ((ResearchDailyDescriptor) dailyDescriptor).getResearch();
        if (research == null) {
            research = researchChart.getResearch();
        }

        level = playerItems.getResearchLevel(research.getId());
    }
}