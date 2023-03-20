package club.ryans.utility;

public class Flags {
    public static boolean hasFlag(final int flags, final int flag) {
        return (flags & flag) != 0;
    }

    public static int setFlag(final int flags, final int flag, final boolean value) {
        return (value) ? flags | flag : flags & ~flag;
    }
}
