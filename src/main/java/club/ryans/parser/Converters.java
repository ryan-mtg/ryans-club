package club.ryans.parser;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Converters {
    public static String convertString(final String value) {
        return value;
    }

    public static boolean convertYesNo(final String value) {
        return "YES".equals(value);
    }

    public static int convertInteger(final String value) {
        return Integer.parseInt(value);
    }

    public static long convertLong(final String value) {
        return Long.parseLong(value);
    }

    public static double convertFloatingPoint(final String value) {
        return Double.parseDouble(value);
    }

    public static Instant convertTime(final String value) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd h:mm:ss a");
            return LocalDateTime.parse(value, formatter).toInstant(ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M/d/uuuu h:mm:ss a");
            return LocalDateTime.parse(value, formatter).toInstant(ZoneOffset.UTC);
        }
    }
}
