package club.ryans.charts.ex.borg;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Range {
    private int lower;
    private int upper;

    public boolean matches(final int value) {
        return lower <= value && value <= upper;
    }
}
