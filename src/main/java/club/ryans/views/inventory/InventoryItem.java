package club.ryans.views.inventory;

import club.ryans.models.accounting.ResourceAmount;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InventoryItem {
    private ResourceAmount resourceAmount;
    private List<String> annotations = new ArrayList<>();

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

    public void addAnnotation(final String annotation) {
        annotations.add(annotation);
    }
}