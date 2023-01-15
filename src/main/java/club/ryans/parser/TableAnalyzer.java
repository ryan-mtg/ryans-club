package club.ryans.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface TableAnalyzer {
    void analyze(final Log log, final ParseTable parseTable);
    Map<String, Function<String, Object>> getConverters();

    default Map<String, Object> convertRow(final List<String> headers, final List<String> parsedRow) {
        Map<String, Object> row = new HashMap<>();

        for (int i = 0; i < parsedRow.size(); i++) {
            String data = parsedRow.get(i);
            if (i < headers.size()) {
                String header = headers.get(i);
                row.put(header, convertValue(header, data));
            }
        }

        return row;
    }

    default Object convertValue(final String header, final String value) {
        try {
            if (value.equals("--")) {
                return null;
            }
            final Map<String, Function<String, Object>> converters = getConverters();
            if (converters.containsKey(header)) {
                return converters.get(header).apply(value);
            }

            return value;
        } catch (Exception e) {
            throw new RuntimeException(String.format("Problem converting %s from value: %s", header, value), e);
        }
    }

    default <Type> Type getValue(final String header, final Map<String, Object> row) {
        if (row.containsKey(header)) {
            return (Type) row.get(header);
        }

        return null;
    }
}