package club.ryans.parser;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ParseTable {
    private List<String> headers;
    private List<List<String>> rows = new ArrayList<>();

    public void addRow(final List<String> row) {
        rows.add(row);
    }
}