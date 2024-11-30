package club.ryans.utility.parser;

import lombok.Getter;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;

public class StructParser<StructType> {
    @Getter
    private final String name;
    private final Consumer<StructType> postProcess;
    private final Class<StructType> structClass;
    private final Map<String, FieldParser> fieldParserMap = new HashMap<>();

    private StructParser(final Class<StructType> structClass, final String name, final Consumer<StructType> postProcess,
            final FieldParser... fields) {
        this.structClass = structClass;
        this.name = name;
        this.postProcess = postProcess;
        for (FieldParser field : fields) {
            fieldParserMap.put(field.getName(), field);
        }
    }

    public StructType parseStruct(final Scanner scanner) {
        StructType struct = createStruct();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            if (line.charAt(0) == '}') {
                break;
            }

            int colonIndex = line.indexOf(':');
            if (colonIndex < 0) {
                Parse.throwError("unexpected field: " + line);
            }

            String fieldName = line.substring(0, colonIndex).trim();
            FieldParser fieldParser = fieldParserMap.get(fieldName);

            if (fieldParser == null) {
                Parse.throwError("Unknown field of %s: %s", name, fieldName);
            }

            String valueText = line.substring(colonIndex + 1).trim();
            fieldParser.parse(struct, valueText, scanner);
        }

        if (postProcess != null) {
            postProcess.accept(struct);
        }

        return struct;
    }

    public StructType parseSingle(final String resourceFile) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resourceFile);
        Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name());

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            return parseSingle(line, scanner);
        }

        return null;
    }

    public StructType parseSingle(final String text, final Scanner scanner) {
        int braceIndex = text.indexOf('{');

        if (braceIndex < 0) {
            Parse.throwError("unexpected struct %s start: %s", name, text);
        }

        if (!text.substring(0, braceIndex).trim().equals(name)) {
            Parse.throwError("unexpected struct %s start: %s", name, text);
        }

        if (text.endsWith("{}") || text.endsWith("{},")) {
            return createStruct();
        }

        return parseStruct(scanner);
    }

    public List<StructType> parseList(final String resourceFile) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(resourceFile);
        Scanner scanner = new Scanner(stream, StandardCharsets.UTF_8.name());

        List<StructType> list = new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }

            list.add(parseSingle(line, scanner));
        }

        return list;
    }

    public static <StructType> StructParser<StructType> createStructParser(final Class<StructType> structClass,
            final String name, final Consumer<StructType> postProcess, final FieldParser... fields) {
        return new StructParser<>(structClass, name, postProcess, fields);
    }

    public static <StructType> StructParser<StructType> createStructParser(final Class<StructType> structClass,
            final String name, final FieldParser... fields) {
        return new StructParser<>(structClass, name, null, fields);
    }

    private StructType createStruct() {
        try {
            return structClass.getDeclaredConstructor().newInstance();
        } catch (InvocationTargetException e) {
            Parse.throwError(e);
        } catch (InstantiationException e) {
            Parse.throwError(e);
        } catch (IllegalAccessException e) {
            Parse.throwError(e);
        } catch (NoSuchMethodException e) {
            Parse.throwError(e);
        }
        return null;
    }
}