package club.ryans.models;

import lombok.Data;

@Data
public class Requirement {
    private String type;
    private long requirementId;
    private int requiredLevel;
    private Integer powerGain;
}
