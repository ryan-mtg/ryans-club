package club.ryans.views;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.text.DecimalFormat;

public class Utility {
    private static long[] PARTS = {1_000_000_000_000_000L, 1_000_000_000_000L, 1_000_000_000L, 1_000_000L, 1_000L, 1};
    private static char[] PART_NAME = {'Q', 'T', 'B', 'M', 'K', '\0'};

    public static String scoplify(final long number) {
        if (number < 0) {
            return '-' + scoplify(-number);
        }

        for (int p = 0; p + 1 < PARTS.length; p++) {
            if (number >= PARTS[p]) {
                return composeNumber(number/PARTS[p + 1], PART_NAME[p]);
            }
        }

        return Long.toString(number);
    }

    public static String sanitize(final String text) {
        return Jsoup.clean(text, Safelist.none());
    }

    private static String composeNumber(long number, char name) {
        String integralPart = Long.toString(number / 1000);

        int decimalLength = 3 - integralPart.length();
        String decimalPart = Long.toString(number % 1000);
        if (decimalPart.length() > decimalLength) {
            decimalPart = decimalPart.substring(0, decimalLength);
        }

        while (decimalPart.length() > 0 && decimalPart.charAt(decimalPart.length() - 1) == '0') {
            decimalPart = decimalPart.substring(0, decimalPart.length() - 1);
        }

        if (decimalPart.length() > 0) {
            return integralPart + '.' + decimalPart + name;
        }
        return integralPart + name;
    }
}