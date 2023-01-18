package club.ryans.parser;

import kotlin.text.Charsets;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogParser {
    private State state;

    public ParseResult parse(final InputStream inputStream) {
        Scanner scanner = new Scanner(inputStream, Charsets.UTF_8.name());
        state = State.HEADER;
        List<ParseTable> tables = new ArrayList<>();
        ParseTable currentTable = new ParseTable();

        int lines = 0;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lines++;

            if (line.isEmpty()) {
                state = State.HEADER;
                tables.add(currentTable);
                currentTable = new ParseTable();
                continue;
            }

            switch (state) {
                case HEADER:
                    currentTable.setHeaders(parseLine(line));
                    state = State.ENTRIES;
                    break;
                case ENTRIES:
                    currentTable.addRow(parseLine(line));
                    break;
            }
        }

        ParseResult result = new ParseResult();
        result.setLines(lines);
        result.setTables(tables);
        return result;
    }

    private static enum State {
        HEADER,
        ENTRIES,
    }

    private List<String> parseLine(final String line) {
        Scanner scanner = new Scanner(line);
        scanner.useDelimiter("\t");
        List<String> row = new ArrayList<>();

        while (scanner.hasNext()) {
            row.add(scanner.next());
        }

        return row;
    }
}