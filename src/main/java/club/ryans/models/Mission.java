package club.ryans.models;

import lombok.Data;

import java.util.List;

@Data
public class Mission {
    private long id;
    private int recommendedLevel;
    private String title;

    private List<Long> pickupSystemIds;
    private List<System> pickupSystems;

    private int warp;
    private int warpWithSuperHighway;
    private int warpForCompletion;
    private int warpForCompletionWithSuperHighway;

    public boolean hasStartSystem() {
        return pickupSystems != null && !pickupSystems.isEmpty();
    }
}
