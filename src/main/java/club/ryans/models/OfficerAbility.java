package club.ryans.models;

import lombok.Data;

@Data
public class OfficerAbility {
    private long id;
    private String name;
    private boolean percentage;
    private int maxLevel;
    private int artId;
    private int flag;
}
