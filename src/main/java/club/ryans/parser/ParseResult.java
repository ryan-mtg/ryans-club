package club.ryans.parser;

import lombok.Data;

import java.util.List;

@Data
public class ParseResult {
    private List<ParseTable> tables;
    private int lines;
}
