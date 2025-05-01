package club.ryans.models;

import lombok.Data;

import java.text.DecimalFormat;

@Data
public class BuffValue {
    private static DecimalFormat PERCENT_FORMAT = new DecimalFormat("#.##%");
    private static DecimalFormat FORMAT = new DecimalFormat("#.##");

    private double value;
    private double chance;

    public String describe(final boolean isPercentage) {
        if (isPercentage)  {
            return PERCENT_FORMAT.format(value);
        }
        return FORMAT.format(value);
    }
}
