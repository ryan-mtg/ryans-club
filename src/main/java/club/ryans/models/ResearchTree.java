package club.ryans.models;

import lombok.Data;

@Data
public class ResearchTree {
    private int id;
    private long stfcSpaceId;

    private ResearchType type;

    private String name;
}
