package club.ryans.utility.parser;

import club.ryans.charts.ex.borg.Range;
import club.ryans.charts.models.RowValue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

public class ValueParser<ValueType> {
    private Function<String, ValueType> parser;

    public ValueParser(final Function<String, ValueType> parser) {
        this.parser = parser;
    }

    public ValueType parse(final String text) {
        return parser.apply(text);
    }

    public static <ValueType> ValueParser<List<ValueType>> arrayParser(final Function<String, ValueType> parser) {
        return ValueParser.arrayParser(new ValueParser<>(parser));
    }

    public static <ValueType> ValueParser<List<ValueType>> arrayParser(final ValueParser<ValueType> valueParser) {
        return new ValueParser<>(Parse.createArrayParser(valueParser));
    }

    public static ValueParser<String> stringParser() {
        return new ValueParser<>(ValueParser::parseString);
    }

    public static ValueParser<Boolean> booleanParser() {
        return new ValueParser<>(ValueParser::parseBoolean);
    }

    public static ValueParser<Duration> durationParser() {
        return new ValueParser<>(ValueParser::parseDuration);
    }

    public static ValueParser<Integer> integerParser() {
        return new ValueParser<>(ValueParser::parseInteger);
    }

    public static ValueParser<Long> longParser() {
        return new ValueParser<>(ValueParser::parseLong);
    }
    public static ValueParser<Float> percentageParser() {
        return new ValueParser<>(ValueParser::parsePercentage);
    }

    public static ValueParser<Range> rangeParser() {
        return new ValueParser<>(Parse::parseRange);
    }

    public static ValueParser<RowValue> rowValueParser() {
        return new ValueParser<>(Parse::parseRowValue);
    }

    private static String parseString(final String text) {
        return text;
    }

    private static Boolean parseBoolean(final String text) {
        return Boolean.parseBoolean(text);
    }

    private static Integer parseInteger(final String text) {
        return parseScopelyInt(text);
    }

    private static Long parseLong(final String text) {
        return parseScopelyLong(text);
    }

    private static Float parsePercentage(final String text) {
        if (!text.endsWith("%")) {
            Parse.throwError("expected percentage, but instead: :%s", text);
        }
        return Float.parseFloat(text.substring(0, text.length() - 1));
    }

    private static Duration parseDuration(final String text) {
        Scanner scanner = new Scanner(text);
        Duration duration = Duration.of(0, ChronoUnit.SECONDS);

        while (scanner.hasNextInt()) {
            int value = scanner.nextInt();
            switch (scanner.next(".")) {
                case "m":
                    duration = duration.plusMinutes(value);
                    break;
                case "h":
                    duration = duration.plusHours(value);
                    break;
                case "d":
                    duration = duration.plusDays(value);
                    break;
            }
        }

        return duration;
    }

    private static int parseScopelyInt(final String text) {
        char lastChar = text.charAt(text.length() - 1);
        if (!Character.isAlphabetic(lastChar)) {
            return Integer.parseInt(text);
        }

        int prefix = Integer.parseInt(text.substring(0, text.length() - 1));
        return (int) scopelyMultiplier(lastChar) * prefix;
    }

    private static long parseScopelyLong(final String text) {
        char lastChar = text.charAt(text.length() - 1);
        if (!Character.isAlphabetic(lastChar)) {
            return Long.parseLong(text);
        }

        long prefix = Long.parseLong(text.substring(0, text.length() - 1));
        return scopelyMultiplier(lastChar) * prefix;
    }

    private static long scopelyMultiplier(final char c) {
        switch (Character.toLowerCase(c)) {
            case 'k':
                return 1_000;
            case 'm':
                return 1_000_000;
            case 'b':
                return 1_000_000_000;
            case 't':
                return 1_000_000_000_000L;
            case 'q':
                return 1_000_000_000_000_000L;
            default:
                throw new RuntimeException("Unknown suffix: " + c);
        }
    }
}