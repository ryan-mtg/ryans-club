package club.ryans.utility.parser;

import club.ryans.models.items.Resource;
import club.ryans.models.managers.ResourceManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResourceParser {
    private final ResourceManager resourceManager;

    public Resource parseValue(final String text) {
        long resourceId = Long.parseLong(text);
        Resource resource = resourceManager.getResource(resourceId);
        if (resource == null) {
            Parse.throwError("Unknown resource: %d", resourceId);
        }
        return resource;
    }
}
