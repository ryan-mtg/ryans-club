package club.ryans.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@Getter
@AllArgsConstructor
public class ShipAnalysisResult {
    private Map<ShipIdentifier, Ship> shipMap;
}
