package club.ryans.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class DefaultTableAnalyzer implements TableAnalyzer {
    public Map<String, Function<String, Object>> getConverters() {
        return new HashMap<>();
    }

    public void analyze(final Log log, final ParseTable parseTable) {
        Table table = new Table();
        List<String> headers = parseTable.getHeaders();
        table.setHeaders(headers);

        for (List<String> parsedRow : parseTable.getRows()) {
            Map<String, Object> convertedRow = convertRow(headers, parsedRow);
            table.addRow(convertedRow);
        }

        log.getUnknownTables().add(table);
    }
}