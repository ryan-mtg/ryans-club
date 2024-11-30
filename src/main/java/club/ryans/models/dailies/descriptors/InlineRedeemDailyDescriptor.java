package club.ryans.models.dailies.descriptors;

import club.ryans.charts.models.ChestGroup;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class InlineRedeemDailyDescriptor extends DailyDescriptor {
    private ChestGroup chestGroup;

    @Override
    public boolean isInline() {
        return true;
    }

    @Override
    public boolean isRedeem() {
        return true;
    }

    @Override
    public boolean isNullType() {
        return true;
    }
}