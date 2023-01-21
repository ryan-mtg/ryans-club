package club.ryans.models.managers;

import java.util.Random;
import java.util.function.Predicate;

public class TagManager {
    private static final int TAG_LENGTH = 10;
    private static final int TAG_BASE = 36;
    private static final long MAX_TAG = (long) Math.pow(TAG_BASE, TAG_LENGTH);

    private Predicate<String> tagSearcher;
    private Random random = new Random();

    public TagManager(final Predicate<String> tagSearcher) {
        this.tagSearcher = tagSearcher;
    }

    public boolean hasTag(final String tag) {
        return tagSearcher.test(tag);
    }

    public String createTag() {
        String tag;
        while (hasTag(tag = generateTag()));
        return tag;
    }

    private String generateTag() {
        return convertTag(Math.abs(random.nextLong()) % MAX_TAG);
    }

    private static String convertTag(long tagLong) {
        StringBuilder tag = new StringBuilder();
        for (int i = 0; i < TAG_LENGTH; i++) {
            int digit = (int) (tagLong % TAG_BASE);
            char c = (char) (digit > 9 ? ('A' + digit - 10) : ('0' + digit));
            tag.append(c);
            tagLong /= TAG_BASE;
        }
        return tag.toString();
    }
}
