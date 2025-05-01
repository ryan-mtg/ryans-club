package club.ryans.models;

import lombok.Data;

import java.util.List;

@Data
public class Buff {
    private int id;
    private long stfcSpaceId;

    private int artId;

    private boolean isPercentage;
    private boolean showPercentage;

    private int valueType;
    private double rankedIsValueChance;

    private List<BuffValue> values;

    public String describe(final int index) {
        return values.get(index).describe(isPercentage);
    }
}