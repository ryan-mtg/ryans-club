package club.ryans.models.alliance;

import lombok.Getter;

public class ServiceApplication {
    private final Service service;

    @Getter
    private final String style;

    public ServiceApplication(final Service service, final boolean enabled) {
        this.service = service;
        this.style = enabled ? "service-enabled" : "service-disabled";
    }

    public String getName() {
        return service.getName();
    }

    public String getDescription() {
        return service.getDescription();
    }

    public String getImageUrl() {
        return service.getImageUrl();
    }
}