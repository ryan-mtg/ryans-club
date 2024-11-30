package club.ryans.views.inventory;

import club.ryans.models.accounting.ResourceAmount;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class InventoryItem {
    private ResourceAmount resourceAmount;
    private String annotation;

    public InventoryItem(final ResourceAmount resourceAmount) {
        this.resourceAmount = resourceAmount;
    }

    public String getName() {
        return resourceAmount.getResource().getName();
    }

    public long getResourceId() {
        return resourceAmount.getResourceId();
    }

    public long getAmount() {
        return resourceAmount.getAmount();
    }

    public String getImageUrl() {
        return resourceAmount.getResource().getImageUrl();
    }

    public String getStyle() {
        return resourceAmount.getResource().getStyle();
    }
}