package club.ryans.views.inventory;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class InventoryGroup {
    private String name;
    private String id;
    private boolean main;
    private List<InventorySubgroup> subgroups;
}