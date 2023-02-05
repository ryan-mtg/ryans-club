package club.ryans.parser;

import club.ryans.models.Officer;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OfficerProcCount {
    private Officer officer;
    private String officerName;
    private String ability;
    private int count;

    public void increment() {
        count++;
    }
}
