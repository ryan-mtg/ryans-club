package club.ryans.models.dailies.descriptors;

import club.ryans.models.accounting.ResourceAmount;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class InlineClaimDailyDescriptor extends DailyDescriptor {
    private ResourceAmount capacity;
    private List<ResourceAmount> claims;
    private List<ResourceAmount> fallbackClaims;

    @Override
    public boolean isInline() {
        return true;
    }

    @Override
    public boolean isClaim() {
        return true;
    }

    @Override
    public boolean isNullType() {
        return true;
    }
}