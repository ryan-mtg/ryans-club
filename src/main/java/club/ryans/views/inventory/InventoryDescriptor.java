package club.ryans.views.inventory;

import lombok.Data;

import java.util.List;

@Data
public class InventoryDescriptor {
    private List<InventoryGroupDescriptor> groups;
}
