package club.ryans.charts.models;

import club.ryans.charts.ex.borg.Range;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Getter @Setter
@Slf4j
public class RangedRow {
    private Range range;

    private List<RowValue> values;

    public RowValue getValue(final int column) {
        if (column > values.size()) {
           throw new RuntimeException("uh oh");
        }
        return values.get(column - 1);
    }

    public List<RowValue> subarray(final int begin, final int end) {
        return values.subList(begin - 1, end - 1);
    }
}