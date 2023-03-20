package club.ryans.utility;

public class Strings {
    public static String capitalize(final String s) {
        StringBuilder text = new StringBuilder();
        boolean capitalize = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (capitalize) {
                c = Character.toUpperCase(c);
            }
            text.append(c);

            capitalize = Character.isSpaceChar(c);
        }
        return text.toString();
    }

    public static boolean isBlank(final String s) {
        return s == null || s.isEmpty();
    }

    public static String trim(final String s) {
        return s == null ? null : s.trim();
    }
}
