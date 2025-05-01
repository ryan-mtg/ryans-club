package club.ryans.models;

import lombok.Data;

import java.util.Collection;

@Data
public class Requirement {
    private String type;
    private long requirementId;
    private long requiredLevel;
    private Integer powerGain;

    public boolean equalsAny(final Collection<Requirement> collection) {
        return collection.stream().anyMatch(this::equals);
    }
}
