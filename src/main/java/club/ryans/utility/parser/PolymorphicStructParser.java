package club.ryans.utility.parser;


import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PolymorphicStructParser<StructType> {
    private final Map<String, StructParser<? extends StructType>> nameMap = new HashMap<>();

    public PolymorphicStructParser(final StructParser<? extends StructType>... parsers) {
        for (StructParser<? extends StructType> parser : parsers) {
            nameMap.put(parser.getName(), parser);
        }
    }

    public StructType parseSingle(final String text, final Scanner scanner) {
        int braceIndex = text.indexOf('{');

        if (braceIndex < 0) {
            Parse.throwError("unexpected struct start: %s", text);
        }

        final String name = text.substring(0, braceIndex).trim();
        if (!nameMap.containsKey(name)) {
            Parse.throwError("unexpected struct type: %s", text);
        }

        return nameMap.get(name).parseSingle(text, scanner);
    }
}