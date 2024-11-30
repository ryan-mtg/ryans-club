package club.ryans.utility.parser;

import club.ryans.models.Research;
import club.ryans.models.managers.ResearchManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResearchParser {
    private final ResearchManager researchManager;

    public Research parseValue(final String text) {
        long resourceId = Long.parseLong(text);
        Research research = researchManager.getResearch(resourceId);
        if (research == null) {
            Parse.throwError("Unknown research: %d", resourceId);
        }
        return research;
    }
}