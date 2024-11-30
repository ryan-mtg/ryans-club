package club.ryans.models.alliance;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class TerritoryOwnership {
    private Territory territory;
    private Map<String, Boolean> serviceUseMap;

    public List<ServiceApplication> getServiceApplications() {
        return territory.getServices().stream()
                .map(service -> new ServiceApplication(service, serviceUseMap.get(service.getName())))
                .collect(Collectors.toList());
    }
}
