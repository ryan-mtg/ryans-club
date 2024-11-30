package club.ryans.utility;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.function.Function;

public class Time {
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("h:mma");
    private static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT);
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("LLLL");

    private static final UnitDescriptor[] unitDescriptors = {
            new UnitDescriptor("week", Duration::toDays, 7, "a"),
            new UnitDescriptor("day", Duration::toHours, 24, "a"),
            new UnitDescriptor("hour", Duration::toMinutes, 60, "an"),
            new UnitDescriptor("minute", Duration::getSeconds, 60, "a"),
            new UnitDescriptor("second", Duration::getSeconds, 1, "a"),
            new UnitDescriptor("", duration -> 0L, 1, ""),
    };


    public static String toReadableString(final Duration duration) {
        for (int i = 0; i + 1 < unitDescriptors.length; i++) {
            String result = toReadableHelper(duration, unitDescriptors[i], unitDescriptors[i + 1]);
            if (result != null) {
                return result;
            }
        }
        return "no time";
    }

    public static boolean isInPast(final Instant time) {
        return time.compareTo(Instant.now()) < 0;
    }

    public static boolean isInFuture(final Instant time) {
        return time.compareTo(Instant.now()) > 0;
    }

    public static String toDevString(final Instant time) {
        if (time == null) {
            return "";
        }
        return toReadableString(ZonedDateTime.ofInstant(time, ZoneId.of("America/Chicago")));
    }

    public static String toReadableString(final Instant time, final String timeZone) {
        return toReadableString(ZonedDateTime.ofInstant(time, ZoneId.of(timeZone)));
    }

    public static String toReadableString(final LocalTime localTime) {
        return SHORT_FORMATTER.format(localTime);
    }

    public static String toReadableString(final ZonedDateTime zonedDateTime) {
        return toReadableString(zonedDateTime, ZonedDateTime.now(zonedDateTime.getZone()));
    }

    public static String toReadableString(final ZonedDateTime goal, final ZonedDateTime now) {
        int dayOfMonth = goal.getDayOfMonth();
        String time = SHORT_FORMATTER.format(goal);
        if (now.getYear() != goal.getYear()) {
            return String.format("%s on the %d%s of %s, %d", time, dayOfMonth, getSuffix(dayOfMonth),
                    MONTH_FORMATTER.format(goal), goal.getYear());
        } else if (now.getMonthValue() != goal.getMonthValue()) {
            return String.format("%s on the %d%s of %s", time, dayOfMonth, getSuffix(dayOfMonth),
                    MONTH_FORMATTER.format(goal));
        } else if (now.getDayOfMonth() != dayOfMonth) {
            return String.format("%s on the %d%s", time, dayOfMonth, getSuffix(dayOfMonth));
        }
        return time;
    }

    private static class UnitDescriptor {
        @Getter
        private final Function<Duration, Long> extractor;

        @Getter
        private final int conversion;

        @Getter
        private final String name;

        @Getter
        private final String article;

        public UnitDescriptor(final String name, final Function<Duration, Long> extractor, final int conversion,
                              final String article) {
            this.name = name;
            this.extractor = extractor;
            this.conversion = conversion;
            this.article = article;
        }
    }

    private static String toReadableHelper(final Duration duration, final UnitDescriptor bigUnitDescriptor,
                                           final UnitDescriptor smallUnitDescriptor) {
        long bigUnit = bigUnitDescriptor.getExtractor().apply(duration);
        int smallUnitConversion = bigUnitDescriptor.getConversion();
        long roundedBigUnit = (bigUnit + smallUnitConversion/2) / smallUnitConversion;
        long flooredBigUnit = bigUnit / smallUnitConversion;

        if (roundedBigUnit > 1) {
            return String.format("%d %ss", roundedBigUnit, bigUnitDescriptor.getName());
        } else if (flooredBigUnit == 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(bigUnitDescriptor.getArticle()).append(' ').append(bigUnitDescriptor.getName());

            long smallUnit = smallUnitDescriptor.getExtractor().apply(duration);
            int smallerUnitConversion = smallUnitDescriptor.getConversion();
            long roundedSmallUnit = (smallUnit + smallerUnitConversion/2) / smallerUnitConversion - smallUnitConversion;
            if (roundedSmallUnit > 1) {
                sb.append(" and ").append(roundedSmallUnit).append(' ');
                sb.append(smallUnitDescriptor.getName()).append('s');
            } else if (roundedSmallUnit == 1) {
                sb.append(" and ").append(smallUnitDescriptor.getArticle());
                sb.append(' ').append(smallUnitDescriptor.getName());
            }
            return sb.toString();
        }
        return null;
    }

    private static String getSuffix(final int number) {
        if (11 <= number && number <= 13) {
            return "th";
        }
        switch (number%10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 0:
                return "th";
            default:
                throw new RuntimeException("Unknown suffix");
        }
    }
}