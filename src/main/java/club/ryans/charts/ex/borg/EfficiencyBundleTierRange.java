package club.ryans.charts.ex.borg;

import club.ryans.models.accounting.ResourceAmount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class EfficiencyBundleTierRange {
    private Range tiers;
    private List<ResourceAmount> rewards;
}
