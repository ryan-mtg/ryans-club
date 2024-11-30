package club.ryans.views.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InventorySubgroup {
    private String name;
    private String description;
    private List<InventoryItem> items;
}