package club.ryans.charts.ex.borg;

import club.ryans.models.ShipClass;
import club.ryans.models.accounting.ChestPrice;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class EfficiencyBundle {
    private ShipClass ship;
    private List<ChestPrice> costs;
    private List<EfficiencyBundleTierRange> tierRanges;
}