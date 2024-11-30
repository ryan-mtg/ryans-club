package club.ryans.utility.parser;

import club.ryans.charts.ex.borg.Range;
import club.ryans.charts.models.RowValue;
import club.ryans.utility.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Parse {
    private final static String BEGIN_TOKENS = "[{<";
    private final static String END_TOKENS = "]}>";

    public static void throwError(final String message, final Object... args) {
        throw new RuntimeException(String.format(message, args));
    }

    public static void throwError(final Exception e) {
        throw new RuntimeException(e);
    }

    public static Range parseRange(final String text) {
        int dashIndex = text.indexOf('-');
        if (dashIndex >= 0)  {
            int lower = Integer.parseInt(text.substring(0, dashIndex));
            int upper = Integer.parseInt(text.substring(dashIndex + 1));
            return new Range(lower, upper);
        }

        int value = Integer.parseInt(text);
        return new Range(value, value);
    }

    public static long parseScopelyValue(final String originalText) {
        final String text = originalText.trim();
        if (text.endsWith("K")) {
            return (long) Double.parseDouble(text.substring(0, text.length() - 1)) * 1000;
        }
        if (text.endsWith("M")) {
            return (long) Double.parseDouble(text.substring(0, text.length() - 1)) * 1_000_000;
        }
        return Long.parseLong(text);
    }

    public static RowValue parseRowValue(final String originalText) {
        final String text = originalText.trim();
        if (text.equals("X")) {
            return RowValue.nullValue();
        }

        if (text.indexOf("?") >= 0) {
            return RowValue.unknownValue();
        }

        if (text.indexOf("/") > 0) {
            List<Long> values = new ArrayList<>();
            for (String value : text.split("/")) {
                values.add(Parse.parseRowScalar(value));
            }

            return RowValue.createValue(values);
        }

        return RowValue.createValue(Parse.parseRowScalar(text));
    }

    public static long parseRowScalar(final String originalText) {
        final String text = originalText.trim();
        if (text.endsWith("%")) {
            return Parse.parseScopelyValue(text.substring(0, text.length() - 1));
        }
        if (text.endsWith("s")) {
            return Long.parseLong(text.substring(0, text.length() - 1));
        }
        return Parse.parseScopelyValue(text);
    }

    static <ValueType> Function<String, List<ValueType>> createArrayParser(final ValueParser<ValueType> valueParser) {
        return (String text) -> {
            if (!text.startsWith("[")) {
                throwError("unexpected start of array: %s", text);
            }

            if (!text.endsWith("]")) {
                throwError("unexpected end of array: %s", text);
            }

            List<String> entries = getArrayEntries(text);

            List<ValueType> values = new ArrayList<>();
            for (String entry : entries) {
                values.add(valueParser.parse(entry));
            }

            return values;
        };
    }

    public static <StructType> BiFunction<String, Scanner, List<StructType>> createStructArrayParser(
            final BiFunction<String, Scanner, StructType> structParser) {
        return (String text, final Scanner scanner) -> {
            List<StructType> list = new ArrayList<>();

            if (text.equals("[],")){
                return list;
            }

            if (!text.equals("[")) {
                Parse.throwError("unexpected start of array: %s", text);
            }

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }

                if (line.startsWith("]")) {
                    if (!"]".equals(line) && !"],".equals(line)) {
                        Parse.throwError("unexpected end of array: %s", line);
                    }
                    break;
                }

                list.add(structParser.apply(line, scanner));
            }

            return list;
        };
    }

    static private List<String> getArrayEntries(final String text) {
        List<String> entries = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        int level = 0;
        for (int i = 1; i + 1 < text.length(); i++) {
            final char c = text.charAt(i);
            if (level == 0 && c == ',') {
                entries.add(current.toString().trim());
                current = new StringBuilder();
                continue;
            }
            current.append(c);

            if (BEGIN_TOKENS.indexOf(c) > 0) {
                level++;
            } else if (END_TOKENS.indexOf(c) > 0) {
                level--;
                if (level < 0) {
                    Parse.throwError("unmatched token: %c in %s", c, text);
                }
            }
        }

        if (level > 0) {
            Parse.throwError("unmatched token in %s", text);
        }

        final String last = current.toString().trim();
        if (!Strings.isBlank(last)) {
            entries.add(last);
        }

        return entries;
    }
}