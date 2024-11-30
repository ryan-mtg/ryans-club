package club.ryans.charts.models;

import java.util.Arrays;
import java.util.List;

public class RowValue {
    private TYPE type;
    private List<Long> values;

    private RowValue(final TYPE type, final long value) {
        this.type = type;
        this.values = Arrays.asList(value);
    }

    private RowValue(final TYPE type, final List<Long> values) {
        this.type = type;
        this.values = values;
    }

    public boolean isNull() {
        return type == TYPE.NULL;
    }

    public boolean isUnknown() {
        return type == TYPE.UNKNOWN;
    }

    public boolean isSingleValued() {
        return type == TYPE.LONG;
    }

    public boolean isMultivalued() {
        return type == TYPE.UNKNOWN_PROBABILITY;
    }

    public long getLong() {
        assert isSingleValued();
        return values.get(0);
    }

    public List<Long> getValues() {
        assert isMultivalued();
        return values;
    }

    public static RowValue nullValue() {
        return new RowValue(TYPE.NULL, 0);
    }

    public static RowValue unknownValue() {
        return new RowValue(TYPE.UNKNOWN, 0);
    }

    public static RowValue createValue(final long value) {
        return new RowValue(TYPE.LONG, value);
    }

    public static RowValue createValue(final List<Long> values) {
        return new RowValue(TYPE.UNKNOWN_PROBABILITY, values);
    }

    private static enum TYPE {
        NULL,
        LONG,
        UNKNOWN,
        UNKNOWN_PROBABILITY,
    }
}