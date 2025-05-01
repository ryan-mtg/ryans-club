package club.ryans.utility.parser;

import club.ryans.charts.ex.borg.Range;
import club.ryans.charts.models.RowValue;
import club.ryans.models.items.Building;
import club.ryans.models.items.Research;
import club.ryans.models.items.Resource;
import club.ryans.models.ShipClass;
import club.ryans.models.player.ResearchLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

@Slf4j
public class FieldParser<StructType, FieldType> {
    @Getter
    private String name;
    private BiFunction<String, Scanner, FieldType> parser;
    private BiConsumer<StructType, FieldType> setter;

    public FieldParser(final String name, final BiFunction<String, Scanner, FieldType> parser,
            final BiConsumer<StructType, FieldType> setter) {
        this.name = name;
        this.parser = parser;
        this.setter = setter;
    }

    public void parse(final StructType struct, final String text, final Scanner scanner) {
        try {
            FieldType fieldValue = parser.apply(text, scanner);
            setter.accept(struct, fieldValue);
        } catch (Exception e) {
            LOGGER.info("Failed to parse {}, value: '{}'", name, text);
            throw e;
        }
    }

    public static <StructType, FieldType> FieldParser<StructType, FieldType> valueField(final String name,
            final ValueParser<FieldType> valueParser, final BiConsumer<StructType, FieldType> setter) {
        return new FieldParser<>(name, createFieldParser(valueParser), setter);
    }

    public static <StructType> FieldParser<StructType, String> stringField(final String name,
            final BiConsumer<StructType, String> setter) {
        return valueField(name, ValueParser.stringParser(), setter);
    }

    public static <StructType> FieldParser<StructType, Boolean> booleanField(final String name,
            final BiConsumer<StructType, Boolean> setter) {
        return valueField(name, ValueParser.booleanParser(), setter);
    }

    public static <StructType> FieldParser<StructType, Duration> durationField(final String name,
            final BiConsumer<StructType, Duration> setter) {
        return valueField(name, ValueParser.durationParser(), setter);
    }

    public static <StructType> FieldParser<StructType, Integer> integerField(final String name,
            final BiConsumer<StructType, Integer> setter) {
        return valueField(name, ValueParser.integerParser(), setter);
    }

    public static <StructType> FieldParser<StructType, Long> longField(final String name,
            final BiConsumer<StructType, Long> setter) {
        return valueField(name, ValueParser.longParser(), setter);
    }

    public static <StructType> FieldParser<StructType, Float> percentageField(final String name,
            final BiConsumer<StructType, Float> setter) {
        return valueField(name, ValueParser.percentageParser(), setter);
    }

    public static <StructType> FieldParser<StructType, Range> rangeField(final String name,
            final BiConsumer<StructType, Range> setter) {
        return valueField(name, ValueParser.rangeParser(), setter);
    }

    public static <StructType> FieldParser<StructType, RowValue> rowValueField(final String name,
            final BiConsumer<StructType, RowValue> setter) {
        return valueField(name, ValueParser.rowValueParser(), setter);
    }

    public static <StructType> FieldParser<StructType, Resource> resourceField(final String name,
            final ResourceParser resourceParser, final BiConsumer<StructType, Resource> setter) {
        return valueField(name, new ValueParser<>(resourceParser::parseValue), setter);
    }

    public static <StructType> FieldParser<StructType, Research> researchField(final String name,
            final ResearchParser researchParser, final BiConsumer<StructType, Research> setter) {
        return valueField(name, new ValueParser<>(researchParser::parseValue), setter);
    }

    public static <StructType> FieldParser<StructType, Building> buildingField(final String name,
            final BuildingParser buildingParser, final BiConsumer<StructType, Building> setter) {
        return valueField(name, new ValueParser<>(buildingParser::parseValue), setter);
    }

    public static <StructType> FieldParser<StructType, ShipClass> shipClassField(final String name,
            final ShipClassParser shipClassParser, final BiConsumer<StructType, ShipClass> setter) {
        return valueField(name, new ValueParser<>(shipClassParser::parseValue), setter);
    }

    public static <StructType> FieldParser<StructType, List<Float>> percentageArrayField(final String name,
            final BiConsumer<StructType, List<Float>> setter) {
        return valueField(name, ValueParser.arrayParser(ValueParser.percentageParser()), setter);
    }

    public static <StructType> FieldParser<StructType, List<Resource>> resourceArrayField(final String name,
            final ResourceParser resourceParser, final BiConsumer<StructType, List<Resource>> setter) {
        return valueField(name, ValueParser.arrayParser(resourceParser::parseValue), setter);
    }

    public static <StructType> FieldParser<StructType, List<ResearchLevel>> researchLevelArrayField(final String name,
            final ResearchLevelParser researchLevelParser, final BiConsumer<StructType, List<ResearchLevel>> setter) {
        return valueField(name, ValueParser.arrayParser(researchLevelParser::parseValue), setter);
    }

    public static <StructType, FieldType> FieldParser<StructType, FieldType> structField(final String name,
            final StructParser<FieldType> structFieldParser, final BiConsumer<StructType, FieldType> setter) {
        return new FieldParser<>(name, structFieldParser::parseSingle, setter);
    }

    public static <StructType, FieldType> FieldParser<StructType, List<FieldType>> structArrayField(final String name,
            final StructParser<FieldType> structFieldParser, final BiConsumer<StructType, List<FieldType>> setter) {
        return new FieldParser<>(name, Parse.createStructArrayParser(structFieldParser::parseSingle), setter);
    }

    public static <StructType, FieldType> FieldParser<StructType, List<FieldType>> structArrayField(final String name,
            final PolymorphicStructParser<FieldType> structFieldParser,
            final BiConsumer<StructType, List<FieldType>> setter) {
        return new FieldParser<>(name, Parse.createStructArrayParser(structFieldParser::parseSingle), setter);
    }

    private static <FieldType> BiFunction<String, Scanner, FieldType> createFieldParser(
            final ValueParser<FieldType> valueParser) {
        return (String text, Scanner scanner) -> valueParser.parse(extractValue(text));
    }

    private static String extractValue(final String text) {
        int commaIndex = text.lastIndexOf(',');
        if (commaIndex < 0) {
            throw new RuntimeException("uh oh: " + text);
        }
        return text.substring(0, commaIndex).trim();
    }
}